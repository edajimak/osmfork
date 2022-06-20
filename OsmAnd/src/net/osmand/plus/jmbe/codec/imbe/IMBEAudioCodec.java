package net.osmand.plus.jmbe.codec.imbe;

/*******************************************************************************
 * jmbe - Java MBE Library
 * Copyright (C) 2015 Dennis Sheirer
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>
 ******************************************************************************/

import net.osmand.plus.jmbe.audio.AudioWithoutMetadata;
import net.osmand.plus.jmbe.iface.FrameInterface;
import net.osmand.plus.jmbe.iface.IAudioCodec;
import net.osmand.plus.jmbe.iface.IAudioDecoder;
import net.osmand.plus.jmbe.iface.IAudioWithMetadata;


public class IMBEAudioCodec implements IAudioCodec
{

    public static final String CODEC_NAME = "IMBE";

    private IMBESynthesizer mSynthesizer;

    public IMBEAudioCodec()
    {
        mSynthesizer = new IMBESynthesizer();
    }

    @Override
    public void reset()
    {
        mSynthesizer.reset();
    }

    /**
     * Converts imbe frame data into PCM audio samples at 8kHz 16-bit rate
     */
    public float[] getAudio(byte[] frameData)
    {
        return getAudio(frameData, null);
    }

    @Override
    public float[] getAudio(byte[] frameData, IAudioDecoder decoder) {
        IMBEFrame frame = new IMBEFrame(frameData);
        return mSynthesizer.getAudio(frame);
    }

    /**
     * Converts imbe frame data into PCM audio samples at 8kHz 16-bit rate
     *
     * Note: this method is for compatibility with the AMBE synthesizer and does not return any metadata.
     *
     * @param frameData byte array for an audio frame
     * @return audio with empty metadata.
     */
    @Override
    public IAudioWithMetadata getAudioWithMetadata(byte[] frameData)
    {
        return getAudioWithMetadata(frameData, null);
    }

    @Override
    public IAudioWithMetadata getAudioWithMetadata(byte[] frameData, IAudioDecoder decoder) {
        IMBEFrame frame = new IMBEFrame(frameData);
        return AudioWithoutMetadata.create(mSynthesizer.getAudio(frame));
    }

    @Override
    public IAudioWithMetadata getAudioWithMetadata(FrameInterface frame)
    {
        return AudioWithoutMetadata.create(mSynthesizer.getAudio((IMBEFrame)frame));
    }

    /**
     * CODEC Name
     */
    @Override
    public String getCodecName()
    {
        return CODEC_NAME;
    }
}
