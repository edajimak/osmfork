package net.osmand.plus.jmbe.codec.ambe;

/**
 * AMBE tone frame parameters
 */
public class ToneParameters
{
    private Tone mTone;
    private int mAmplitude;

    /**
     * Constructs an instance
     * @param tone enumeration entry
     * @param amplitude of the tone
     */
    public ToneParameters(Tone tone, int amplitude)
    {
        mTone = tone;
        mAmplitude = amplitude;
    }

    /**
     * Tone for this segment
     */
    public Tone getTone()
    {
        return mTone;
    }

    /**
     * Amplitude of the tone
     */
    public int getAmplitude()
    {
        return mAmplitude;
    }

    /**
     * Indicates if the tone is valid (ie not the INVALID enumeration entry)
     */
    public boolean isValidTone()
    {
        return getTone() != Tone.INVALID;
    }
}
