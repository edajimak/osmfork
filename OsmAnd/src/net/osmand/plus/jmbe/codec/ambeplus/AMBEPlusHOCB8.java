package net.osmand.plus.jmbe.codec.ambeplus;

/**
 * Higher order coefficients - b8
 */
public enum AMBEPlusHOCB8
{
    V0(new float[]{-0.453125f, 0.179688f, 0.078125f, -0.015625f}),
    V1(new float[]{-0.414063f, -0.179688f, -0.031250f, 0.015625f}),
    V2(new float[]{-0.281250f, 0.187500f, -0.203125f, 0.046875f}),
    V3(new float[]{-0.210938f, -0.007813f, -0.031250f, -0.031250f}),
    V4(new float[]{-0.148438f, -0.031250f, 0.218750f, -0.054688f}),
    V5(new float[]{-0.140625f, -0.085938f, 0.039063f, 0.187500f}),
    V6(new float[]{-0.117188f, 0.234375f, 0.031250f, -0.054688f}),
    V7(new float[]{-0.062500f, -0.273438f, -0.007813f, -0.015625f}),
    V8(new float[]{-0.054688f, 0.093750f, -0.078125f, 0.078125f}),
    V9(new float[]{-0.023438f, -0.062500f, -0.210938f, -0.054688f}),
    V10(new float[]{0.023438f, 0.000000f, 0.023438f, -0.046875f}),
    V11(new float[]{0.125000f, 0.234375f, -0.187500f, -0.015625f}),
    V12(new float[]{0.164063f, -0.054688f, -0.093750f, 0.070313f}),
    V13(new float[]{0.187500f, 0.179688f, 0.093750f, 0.015625f}),
    V14(new float[]{0.203125f, -0.171875f, 0.140625f, -0.015625f}),
    V15(new float[]{0.421875f, -0.039063f, -0.046875f, -0.007813f});

    private float[] mCoefficients;

    AMBEPlusHOCB8(float[] coefficients)
    {
        mCoefficients = coefficients;
    }

    public float[] getCoefficients()
    {
        return mCoefficients;
    }

    public static AMBEPlusHOCB8 fromValue(int value)
    {
        if(0 <= value && value <= 15)
        {
            return AMBEPlusHOCB8.values()[value];
        }

        throw new IllegalArgumentException("Value must be in range 0-15.  Unsupported value: " + value);
    }
}
