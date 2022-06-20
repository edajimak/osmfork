package net.osmand.plus.jmbe.iface;

/**
 * Audio converter interface.  Defines methods for a stand-alone converter that
 * can convert byte data from one audio format into byte data of another.
 */
public interface IAudioCodec
{
    /**
     * Name of the CODEC for this audio converter
     */
    String getCodecName();

    /**
     * Converts frameData into the audio format specified by the getConvertedAudioFormat() method
     */
    float[] getAudio(byte[] frameData);

    /**
     * Converts frameData into the audio format specified by the getConvertedAudioFormat() method
     * @param decoder Decoder
     */
    float[] getAudio(byte[] frameData, IAudioDecoder decoder);

    /**
     * Converts frameData to 8 kHz 16-bit PCM audio and provides optional decoded metadata like Tones or DTMF
     * @param frameData byte array for an audio frame
     * @return audio data with optional metadata
     */
    IAudioWithMetadata getAudioWithMetadata(byte[] frameData);

    /**
     * Converts frameData to 8 kHz 16-bit PCM audio and provides optional decoded metadata like Tones or DTMF
     * @param frameData byte array for an audio frame
     * @param decoder Decoder
     * @return audio data with optional metadata
     */
    IAudioWithMetadata getAudioWithMetadata(byte[] frameData, IAudioDecoder decoder);

    /**
     * Converts frameData to 8 kHz 16-bit PCM audio and provides optional decoded metadata like Tones or DTMF
     *
     * @param frame FrameInterface for an audio frame
     * @return audio data with optional metadata
     */
    IAudioWithMetadata getAudioWithMetadata(FrameInterface frame);

    /**
     * Resets the audio converter for a new call.  This causes the stored previous frame to be reset to a default
     * audio frame.
     */
    void reset();
}
