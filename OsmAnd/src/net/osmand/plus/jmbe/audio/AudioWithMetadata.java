package net.osmand.plus.jmbe.audio;

import net.osmand.plus.jmbe.codec.ambe.AMBEFrame;
import net.osmand.plus.jmbe.iface.IAudioWithMetadata;

import java.util.HashMap;
import java.util.Map;

/**
 * Audio without any accompanying metadata.
 */
public class AudioWithMetadata implements IAudioWithMetadata
{
    private float[] mAudio;
    private Map<String,String> mMetadataMap = new HashMap<>();
    private AMBEFrame ambeFrame;

    /**
     * Constructs an instance
     * @param audio samples
     */
    private AudioWithMetadata(float[] audio)
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


    public void addMetadata(String key, String value)
    {
        mMetadataMap.put(key, value);
    }

    public AMBEFrame getAmbeFrame() {
        return ambeFrame;
    }

    public void setAmbeFrame(AMBEFrame ambeFrame) {
        this.ambeFrame = ambeFrame;
    }

    /**
     * Indicates if there is any metadata associated with this audio block
     */
    @Override
    public boolean hasMetadata()
    {
        return !mMetadataMap.isEmpty();
    }

    /**
     * Metadata map.
     * @return map of metadata
     */
    @Override
    public Map<String,String> getMetadata()
    {
        return mMetadataMap;
    }

    /**
     * Convenience method to create an instance
     */
    public static AudioWithMetadata create(float[] audio)
    {
        return new AudioWithMetadata(audio);
    }
}
