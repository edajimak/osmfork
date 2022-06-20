package net.osmand.plus.jmbe.codec.imbe;

import net.osmand.plus.jmbe.codec.MBEModelParameters;
import net.osmand.plus.jmbe.codec.MBESynthesizer;


/**
 * IMBE synthesizer for IMBE audio frames
 */
public class IMBESynthesizer extends MBESynthesizer
{
    private IMBEModelParameters mPreviousParameters = new IMBEModelParameters();

    /**
     * Synthesizes 8 kHz 16-bit audio from IMBE audio frames
     */
    public IMBESynthesizer()
    {
    }

    @Override
    public MBEModelParameters getPreviousFrame()
    {
        return mPreviousParameters;
    }

    public void reset()
    {
        mPreviousParameters = new IMBEModelParameters();
    }

    /**
     * Synthesizes 20 milliseconds of audio from the imbe frame parameters in
     * the following format:
     *
     * Sample Rate: 8 kHz
     * Sample Size: 16-bits
     * Frame Size: 160 samples
     * Bit Format: Little Endian
     *
     * @return ByteBuffer containing the audio sample bytes
     */
    public float[] getAudio(IMBEFrame frame)
    {
        IMBEModelParameters parameters = frame.getModelParameters(mPreviousParameters);

        float[] audio = null;

        if(parameters.isMaxFrameRepeat() || parameters.requiresMuting())
        {
            audio = getWhiteNoise();
        }
        else
        {
            audio = getVoice(parameters);
        }

        mPreviousParameters = parameters;

        return audio;
    }
}
