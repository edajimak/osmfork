package net.osmand.plus.jmbe.codec.ambe;

import net.osmand.plus.jmbe.codec.oscillator.Oscillator;

/**
 * Tone Generator
 */
public class ToneGenerator
{
    private static final double SAMPLE_RATE = 8000.0;
    private static final int SAMPLE_COUNT = 160;  //20ms of samples at 8000 Hz
    private static final float TWO_CHANNEL_GAIN_REDUCTION = 0.5f;

    private Oscillator mOscillator1 = new Oscillator(0.0, SAMPLE_RATE);
    private Oscillator mOscillator2 = new Oscillator(0.0, SAMPLE_RATE);

    /**
     * Constructs an instance
     */
    public ToneGenerator()
    {
    }

    /**
     * Generates 20 ms of PCM audio samples at 8000Hz sample rate using the specified frequency and gain parameters
     *
     * @param toneParameters containing frequency(s) and amplitude
     * @return pcm audio samples
     */
    public float[] generate(ToneParameters toneParameters)
    {
        if(!toneParameters.isValidTone())
        {
            throw new IllegalArgumentException("Cannot generate tone audio - INVALID tone");
        }

        Tone tone = toneParameters.getTone();
        float gain = ((float)toneParameters.getAmplitude() / 127.0f);

        if(tone.hasFrequency2())
        {
            gain *= TWO_CHANNEL_GAIN_REDUCTION;

            mOscillator1.setFrequency(tone.getFrequency1());
            mOscillator2.setFrequency(tone.getFrequency2());

            float[] samples = mOscillator1.generate(SAMPLE_COUNT, gain);
            float[] samples2 = mOscillator2.generate(SAMPLE_COUNT, gain);

            for(int x = 0; x < SAMPLE_COUNT; x++)
            {
                samples[x] += samples2[x];
            }

            return samples;
        }
        else
        {
            mOscillator1.setFrequency(tone.getFrequency1());
            return mOscillator1.generate(SAMPLE_COUNT, gain);
        }
    }
}
