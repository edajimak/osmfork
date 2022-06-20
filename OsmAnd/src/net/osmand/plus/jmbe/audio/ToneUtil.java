package net.osmand.plus.jmbe.audio;

import net.osmand.plus.jmbe.codec.oscillator.Oscillator;

public class ToneUtil {
    public static float[] getTone(double toneFq, float gain, int sampleCount)
    {
        Oscillator oscillator = new Oscillator(toneFq, 8000.0);
        float[] samples = oscillator.generate(sampleCount, gain);

        //Attenuate beginning and end samples
        if(sampleCount > 100)
        {
            for(int x = 0; x < 20; x++)
            {
                samples[x] *= (float)x / 10.0f;
            }

            for(int x = 0; x < 20; x++)
            {
                samples[samples.length - 1 - x] *= (float)x / 10.0f;
            }
        }

        return samples;
    }
}
