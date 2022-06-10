package net.osmand.plus.jmbe;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;

import net.osmand.plus.jmbe.codec.ambe.AMBEFrame;
import net.osmand.plus.jmbe.codec.ambe.AMBESynthesizer;

import java.util.ArrayList;
import java.util.List;

public class WavSynthesizer
{
    /**
     * @param segment 160 samples (20 ms) of audio from the ambe frame.
     */
    public byte[] convertToWav(List<float[]> segment)
    {
        int idx = 0;
        byte[] generated = new byte[2 * segment.get(0).length * segment.size()];
        short[] volume = new short[segment.get(0).length * segment.size()];
        int maxValue = 0;
        for (float[] chunk: segment) {
            for (float tval: chunk) {
                short val = (short) ((tval * 32767));
                if (Math.abs(val) > maxValue) {
                    maxValue = Math.abs(val);
                }
                volume[idx++] = val;
            }
        }

        double coff = 1;
        double rs = maxValue/32767.0;
        if (rs < 0.95) {
            coff = 0.96 * (1/rs);
        }

        idx = 0;
        for (short tval: volume) {
            short val = (short) (tval * coff);
            generated[idx++] = (byte) (val & 0x00ff);
            generated[idx++] = (byte) ((val & 0xff00) >>> 8);
        }

        return generated;
    }

    public byte[] convertMbeToWav(List<AMBEFrame> frames) {
        AMBESynthesizer mSynthesizer = new AMBESynthesizer();
        List<float[]> audioSegment = new ArrayList<>();
        for (AMBEFrame ambeFrame: frames) {
            float[] samples = mSynthesizer.getAudio(ambeFrame);
            audioSegment.add(samples);
        }

        return convertToWav(audioSegment);
    }

    void playSound(byte[] generated) {
        AudioTrack audioTrack = new AudioTrack(AudioManager.STREAM_MUSIC,
                8000, AudioFormat.CHANNEL_OUT_MONO,
                AudioFormat.ENCODING_PCM_16BIT, generated.length,
                AudioTrack.MODE_STATIC);

        audioTrack.write(generated, 0, generated.length);
        audioTrack.play();
    }
}
