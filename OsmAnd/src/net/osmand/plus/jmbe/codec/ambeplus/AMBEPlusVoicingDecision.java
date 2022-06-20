package net.osmand.plus.jmbe.codec.ambeplus;

/**
 * AMBE+ Voice/Unvoiced Quantization Vector enumeration
 */
public enum AMBEPlusVoicingDecision
{
    V0(new boolean[]{false, false, false, false, false, false, false, false}),
    V1(new boolean[]{false, false, false, false, false, false, true, true}),
    V2(new boolean[]{false, false, false, false, true, true, false, false}),
    V3(new boolean[]{false, false, false, false, true, true, true, true}),
    V4(new boolean[]{false, false, true, true, false, false, false, false}),
    V5(new boolean[]{false, false, true, true, false, false, true, true}),
    V6(new boolean[]{false, false, true, true, true, true, false, false}),
    V7(new boolean[]{false, false, true, true, true, true, true, true}),
    V8(new boolean[]{true, true, false, false, false, false, false, false}),
    V9(new boolean[]{true, true, false, false, false, false, true, true}),
    V10(new boolean[]{true, true, false, false, true, true, false, false}),
    V11(new boolean[]{true, true, false, false, true, true, true, true}),
    V12(new boolean[]{true, true, true, true, false, false, false, false}),
    V13(new boolean[]{true, true, true, true, false, false, true, true}),
    V14(new boolean[]{true, true, true, true, true, true, false, false}),
    V15(new boolean[]{true, true, true, true, true, true, true, true});

    private boolean[] mVoiceDecisions;

    AMBEPlusVoicingDecision(boolean[] voiceDecisions)
    {
        mVoiceDecisions = voiceDecisions;
    }

    public boolean isVoiced(int index)
    {
        if(0 <= index && index <= 7)
        {
            return mVoiceDecisions[index];
        }

        throw new IllegalArgumentException("Voice decision index must be in range 0-7.  Unsupported index: " + index);
    }

    public static AMBEPlusVoicingDecision fromValue(int value)
    {
        if(0 <= value && value <= 15)
        {
            return AMBEPlusVoicingDecision.values()[value];
        }

        throw new IllegalArgumentException("Quantization vector values must be in range 0-15.  Unsupported value: " + value);
    }
}

