package net.osmand.plus.jmbe;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;

import net.osmand.plus.jmbe.audio.ToneUtil;
import net.osmand.plus.jmbe.codec.ambe.AMBEFrame;
import net.osmand.plus.jmbe.codec.ambe.AMBESynthesizer;

import java.util.ArrayList;
import java.util.List;

public class WavSynthesizer
{
    private double lastGain = 0;

    /**
     * @param segment 160 samples (20 ms) of audio from the ambe frame.
     */
    private byte[] convertToWav(List<float[]> segment)
    {
        int idx = 0;
        int toneLength = 600;
        byte[] generated = new byte[2 * segment.get(0).length * segment.size() + 4*toneLength];
        short[] volume = new short[segment.get(0).length * segment.size()];
        int maxValue = 0;
        for (float[] chunk: segment) {
            for (float tval: chunk) {
                short val = (short) (tval * 32767);
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

        float[] tone = ToneUtil.getTone(650, 0.33f, toneLength);
        for (float tval: tone) {
            short val = (short) (tval * 32767);
            generated[idx++] = (byte) (val & 0x00ff);
            generated[idx++] = (byte) ((val & 0xff00) >>> 8);
        }

        for (short tval: volume) {
            short val = (short) (tval * coff);
            generated[idx++] = (byte) (val & 0x00ff);
            generated[idx++] = (byte) ((val & 0xff00) >>> 8);
        }

        tone = ToneUtil.getTone(600, 0.33f, toneLength);
        for (float tval: tone) {
            short val = (short) (tval * 32767);
            generated[idx++] = (byte) (val & 0x00ff);
            generated[idx++] = (byte) ((val & 0xff00) >>> 8);
        }

        lastGain = coff;
        return generated;
    }

    public double getLastGain()
    {
        return lastGain;
    }

    public byte[] convertMbeToWav(List<AMBEFrame> frames) {
        AMBESynthesizer mSynthesizer = new AMBESynthesizer();
        List<float[]> audioSegment = new ArrayList<>();
        for (AMBEFrame ambeFrame: frames) {
            float[] samples = mSynthesizer.getAudio(ambeFrame);
            audioSegment.add(samples);
        }

        ToneUtil.getTone(1200, 0.4f, 800);

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
