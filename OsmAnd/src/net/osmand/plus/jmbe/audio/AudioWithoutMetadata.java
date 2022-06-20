package net.osmand.plus.jmbe.audio;

import net.osmand.plus.jmbe.iface.IAudioWithMetadata;

import java.util.Collections;
import java.util.Map;

/**
 * Audio without any accompanying metadata.
 */
public class AudioWithoutMetadata implements IAudioWithMetadata
{
    private float[] mAudio;

    /**
     * Constructs an instance
     * @param audio samples
     */
    public AudioWithoutMetadata(float[] audio)
    {
        mAudio = audio;
    }

    /**
     * PCM audio samples
     */
    @Override
    public float[] getAudio()
    {
        return mAudio;
    }

    /**
     * Always indicates false
     */
    @Override
    public boolean hasMetadata()
    {
        return false;
    }

    /**
     * Metadata map.
     * @return empty map
     */
    @Override
    public Map<String,String> getMetadata()
    {
        return Collections.emptyMap();
    }

    /**
     * Convenience method to create an instance
     */
    public static AudioWithoutMetadata create(float[] audio)
    {
        return new AudioWithoutMetadata(audio);
    }
}
