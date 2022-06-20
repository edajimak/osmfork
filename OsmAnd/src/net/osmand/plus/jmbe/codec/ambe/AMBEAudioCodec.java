package net.osmand.plus.jmbe.codec.ambe;

import net.osmand.plus.jmbe.audio.AudioWithMetadata;
import net.osmand.plus.jmbe.codec.FrameType;
import net.osmand.plus.jmbe.iface.FrameInterface;
import net.osmand.plus.jmbe.iface.IAudioCodec;
import net.osmand.plus.jmbe.iface.IAudioDecoder;
import net.osmand.plus.jmbe.iface.IAudioWithMetadata;

/**
 * Audio converter for AMBE frames encoded at 3600 bps with 2450 bps data and 1250 bps FEC
 */
public class AMBEAudioCodec implements IAudioCodec
{

    public static final String CODEC_NAME = "AMBE 3600 x 2450";
    private AMBESynthesizer mSynthesizer = new AMBESynthesizer();

    public AMBEAudioCodec()
    {
    }

    /**
     * Converts the AMBE frame data into PCM audio samples at 8kHz 16-bit rate.
     *
     * @param frameData byte array of AMBE frame data
     */
    public float[] getAudio(byte[] frameData)
    {
        return getAudio(frameData, null);
    }

    @Override
    public float[] getAudio(byte[] frameData, IAudioDecoder decoder) {
        AMBEFrame frame = new AMBEFrame(frameData, decoder);
        return getAudio(frame);
    }

    /**
     * Converts the AMBE frame into PCM audio samples at 8kHz 16-bit rate
     */
    public float[] getAudio(AMBEFrame ambeFrame)
    {
        return mSynthesizer.getAudio(ambeFrame);
    }

    /**
     * Converts the AMBE frame data into PCM audio samples at 8kHz 16-bit rate and includes metadata about any
     * tone(s) contained in the frame.
     *
     * @param frameData byte array for an audio frame
     * @return decoded audio and any associated metadata such as tones or dtmf/knox codes
     */
    @Override
    public IAudioWithMetadata getAudioWithMetadata(byte[] frameData)
    {
        return getAudioWithMetadata(frameData, null);
    }

    @Override
    public IAudioWithMetadata getAudioWithMetadata(FrameInterface abmeFrame)
    {
        AMBEFrame frame = (AMBEFrame) abmeFrame;
        AudioWithMetadata audioWithMetadata = AudioWithMetadata.create(getAudio(frame));
        audioWithMetadata.setAmbeFrame(frame);

        if(frame.getFrameType() == FrameType.TONE)
        {
            Tone tone = frame.getToneParameters().getTone();

            if(Tone.CALL_PROGRESS_TONES.contains(tone))
            {
                audioWithMetadata.addMetadata("CALL PROGRESS", tone.toString());
            }
            else if(Tone.DISCRETE_TONES.contains(tone))
            {
                audioWithMetadata.addMetadata("TONE", tone.toString());
            }
            else if(Tone.DTMF_TONES.contains(tone))
            {
                audioWithMetadata.addMetadata("DTMF", tone.toString());
            }
            else if(Tone.KNOX_TONES.contains(tone))
            {
                audioWithMetadata.addMetadata("KNOX", tone.toString());
            }
        }

        return audioWithMetadata;
    }

    @Override
    public IAudioWithMetadata getAudioWithMetadata(byte[] frameData, IAudioDecoder decoder) {
        AMBEFrame frame = new AMBEFrame(frameData, decoder);

        return getAudioWithMetadata(frame);
    }

    /**
     * Resets the audio converter at the end or beginning of each call so that the starting frame is a default frame.
     */
    @Override
    public void reset()
    {
        mSynthesizer.reset();
    }

    /**
     * CODEC Name constant
     */
    @Override
    public String getCodecName()
    {
        return CODEC_NAME;
    }
}
