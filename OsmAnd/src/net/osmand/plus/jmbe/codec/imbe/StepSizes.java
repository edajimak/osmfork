package net.osmand.plus.jmbe.codec.imbe;

import java.util.Map;
import java.util.TreeMap;

/**
 * Defines the deltaM step sizes for each of the b3 through b(L + 1) prediction residuals stored in indexes 0
 * through (L + 1 - 3).
 *
 * Used by Alg 68 and 71
 *
 * Step sizes are defined in Annex F and also Tables 3 and 4 using the indexes from Annex G.
 */
public enum StepSizes
{
    L09( 9, new float[]{0.00310f, 0.00402f, 0.00336f, 0.00290f, 0.00264f, 0.00614f, 0.01228f, 0.02456f}),
    L10(10, new float[]{0.00620f, 0.00402f, 0.00672f, 0.00580f, 0.00528f, 0.00614f, 0.02456f, 0.04605f, 0.08596f}),
    L11(11, new float[]{0.01240f, 0.00804f, 0.00672f, 0.01160f, 0.01056f, 0.00614f, 0.02456f, 0.04605f, 0.08596f, 0.12280f}),
    L12(12, new float[]{0.01240f, 0.01608f, 0.01344f, 0.01160f, 0.01056f, 0.01228f, 0.02456f, 0.04605f, 0.08596f, 0.12280f, 0.19955f}),
    L13(13, new float[]{0.02480f, 0.01608f, 0.01344f, 0.02175f, 0.01980f, 0.02456f, 0.02456f, 0.04605f, 0.08596f, 0.12280f, 0.19955f, 0.15665f}),
    L14(14, new float[]{0.02480f, 0.03015f, 0.02520f, 0.02175f, 0.01980f, 0.02456f, 0.02456f, 0.08596f, 0.12280f, 0.12280f, 0.15665f, 0.12280f, 0.15665f}),
    L15(15, new float[]{0.02480f, 0.03015f, 0.02520f, 0.02175f, 0.03696f, 0.04605f, 0.02456f, 0.08596f, 0.12280f, 0.09640f, 0.19955f, 0.15665f, 0.19955f, 0.15665f}),
    L16(16, new float[]{0.04650f, 0.03015f, 0.02520f, 0.04060f, 0.03696f, 0.04605f, 0.04605f, 0.08596f, 0.09640f, 0.12280f, 0.15665f, 0.19955f, 0.15665f, 0.19955f, 0.20485f}),
    L17(17, new float[]{0.04650f, 0.03015f, 0.04704f, 0.04060f, 0.03696f, 0.08596f, 0.08596f, 0.06748f, 0.12280f, 0.09640f, 0.12280f, 0.15665f, 0.19955f, 0.20485f, 0.19955f, 0.20485f}),
    L18(18, new float[]{0.04650f, 0.05628f, 0.04704f, 0.04060f, 0.03696f, 0.08596f, 0.09640f, 0.08596f, 0.06748f, 0.12280f, 0.15665f, 0.19955f, 0.15665f, 0.19955f, 0.20485f, 0.26095f, 0.20485f}),
    L19(19, new float[]{0.04650f, 0.05628f, 0.04704f, 0.05800f, 0.05280f, 0.08596f, 0.09640f, 0.08596f, 0.09640f, 0.12280f, 0.15665f, 0.19955f, 0.15665f, 0.19955f, 0.20485f, 0.19955f, 0.20485f, 0.24840f}),
    L20(20, new float[]{0.04650f, 0.05628f, 0.04704f, 0.05800f, 0.05280f, 0.08596f, 0.09640f, 0.08596f, 0.09640f, 0.12280f, 0.15665f, 0.19955f, 0.20485f, 0.19955f, 0.20485f, 0.24840f, 0.19955f, 0.20485f, 0.24840f}),
    L21(21, new float[]{0.08680f, 0.05628f, 0.04704f, 0.05800f, 0.05280f, 0.12280f, 0.09640f, 0.08596f, 0.09640f, 0.12280f, 0.15665f, 0.19955f, 0.20485f, 0.17595f, 0.19955f, 0.20485f, 0.24840f, 0.19955f, 0.20485f, 0.24840f}),
    L22(22, new float[]{0.08680f, 0.05628f, 0.06720f, 0.05800f, 0.05280f, 0.12280f, 0.09640f, 0.12280f, 0.09640f, 0.12280f, 0.15665f, 0.17595f, 0.19955f, 0.20485f, 0.17595f, 0.19955f, 0.20485f, 0.24840f, 0.26095f, 0.20485f, 0.24840f}),
    L23(23, new float[]{0.08680f, 0.08040f, 0.06720f, 0.05800f, 0.05280f, 0.12280f, 0.15665f, 0.12280f, 0.09640f, 0.13455f, 0.12280f, 0.15665f, 0.17595f, 0.19955f, 0.20485f, 0.17595f, 0.26095f, 0.20485f, 0.24840f, 0.26095f, 0.20485f, 0.24840f}),
    L24(24, new float[]{0.08680f, 0.08040f, 0.06720f, 0.05800f, 0.05280f, 0.12280f, 0.15665f, 0.13455f, 0.12280f, 0.15665f, 0.13455f, 0.19955f, 0.15665f, 0.17595f, 0.19955f, 0.20485f, 0.24840f, 0.26095f, 0.20485f, 0.24840f, 0.26095f, 0.20485f, 0.24840f}),
    L25(25, new float[]{0.08680f, 0.08040f, 0.06720f, 0.05800f, 0.08580f, 0.12280f, 0.15665f, 0.13455f, 0.12280f, 0.15665f, 0.13455f, 0.19955f, 0.15665f, 0.17595f, 0.19955f, 0.20485f, 0.24840f, 0.26095f, 0.20485f, 0.24840f, 0.26095f, 0.28920f, 0.24840f, 0.22800f}),
    L26(26, new float[]{0.08680f, 0.08040f, 0.06720f, 0.09425f, 0.08580f, 0.12280f, 0.15665f, 0.13455f, 0.12280f, 0.15665f, 0.13455f, 0.19955f, 0.20485f, 0.17595f, 0.19955f, 0.20485f, 0.24840f, 0.26095f, 0.20485f, 0.24840f, 0.22800f, 0.26095f, 0.20485f, 0.24840f, 0.22800f}),
    L27(27, new float[]{0.08680f, 0.08040f, 0.06720f, 0.09425f, 0.08580f, 0.12280f, 0.15665f, 0.17595f, 0.12280f, 0.15665f, 0.17595f, 0.19955f, 0.20485f, 0.17595f, 0.19955f, 0.20485f, 0.17595f, 0.22800f, 0.26095f, 0.20485f, 0.24840f, 0.22800f, 0.26095f, 0.20485f, 0.24840f, 0.22800f}),
    L28(28, new float[]{0.12400f, 0.08040f, 0.06720f, 0.09425f, 0.08580f, 0.12280f, 0.15665f, 0.17595f, 0.12280f, 0.15665f, 0.17595f, 0.19955f, 0.20485f, 0.17595f, 0.16150f, 0.19955f, 0.20485f, 0.24840f, 0.22800f, 0.26095f, 0.20485f, 0.24840f, 0.22800f, 0.26095f, 0.28920f, 0.24840f, 0.22800f}),
    L29(29, new float[]{0.12400f, 0.08040f, 0.06720f, 0.09425f, 0.08580f, 0.19955f, 0.15665f, 0.17595f, 0.12280f, 0.15665f, 0.17595f, 0.16150f, 0.19955f, 0.20485f, 0.17595f, 0.16150f, 0.19955f, 0.20485f, 0.24840f, 0.22800f, 0.26095f, 0.28920f, 0.24840f, 0.22800f, 0.26095f, 0.28920f, 0.24840f, 0.22800f}),
    L30(30, new float[]{0.12400f, 0.08040f, 0.06720f, 0.09425f, 0.08580f, 0.19955f, 0.15665f, 0.17595f, 0.16150f, 0.19955f, 0.15665f, 0.17595f, 0.16150f, 0.19955f, 0.20485f, 0.17595f, 0.22800f, 0.19955f, 0.20485f, 0.24840f, 0.22800f, 0.26095f, 0.28920f, 0.24840f, 0.22800f, 0.26095f, 0.28920f, 0.24840f, 0.22800f}),
    L31(31, new float[]{0.12400f, 0.08040f, 0.10920f, 0.09425f, 0.08580f, 0.19955f, 0.15665f, 0.17595f, 0.16150f, 0.19955f, 0.15665f, 0.17595f, 0.16150f, 0.19955f, 0.20485f, 0.17595f, 0.22800f, 0.26095f, 0.20485f, 0.24840f, 0.22800f, 0.26095f, 0.28920f, 0.24840f, 0.22800f, 0.26095f, 0.28920f, 0.24840f, 0.22800f, 0.21480f}),
    L32(32, new float[]{0.12400f, 0.08040f, 0.10920f, 0.09425f, 0.08580f, 0.19955f, 0.15665f, 0.17595f, 0.16150f, 0.19955f, 0.15665f, 0.17595f, 0.16150f, 0.19955f, 0.20485f, 0.17595f, 0.22800f, 0.26095f, 0.20485f, 0.24840f, 0.22800f, 0.26095f, 0.28920f, 0.24840f, 0.22800f, 0.21480f, 0.26095f, 0.28920f, 0.24840f, 0.22800f, 0.00000f}),
    L33(33, new float[]{0.12400f, 0.13065f, 0.10920f, 0.09425f, 0.08580f, 0.19955f, 0.15665f, 0.17595f, 0.16150f, 0.19955f, 0.15665f, 0.17595f, 0.16150f, 0.19955f, 0.20485f, 0.24840f, 0.22800f, 0.26095f, 0.20485f, 0.24840f, 0.22800f, 0.21480f, 0.26095f, 0.28920f, 0.24840f, 0.22800f, 0.21480f, 0.26095f, 0.28920f, 0.24840f, 0.22800f, 0.21480f}),
    L34(34, new float[]{0.12400f, 0.13065f, 0.10920f, 0.09425f, 0.08580f, 0.19955f, 0.20485f, 0.17595f, 0.16150f, 0.19955f, 0.20485f, 0.17595f, 0.16150f, 0.19955f, 0.20485f, 0.17595f, 0.22800f, 0.21480f, 0.26095f, 0.20485f, 0.24840f, 0.22800f, 0.21480f, 0.26095f, 0.28920f, 0.24840f, 0.22800f, 0.21480f, 0.26095f, 0.28920f, 0.24840f, 0.22800f, 0.00000f}),
    L35(35, new float[]{0.12400f, 0.13065f, 0.10920f, 0.09425f, 0.08580f, 0.19955f, 0.20485f, 0.17595f, 0.16150f, 0.19955f, 0.20485f, 0.17595f, 0.16150f, 0.15215f, 0.19955f, 0.20485f, 0.24840f, 0.22800f, 0.21480f, 0.26095f, 0.20485f, 0.24840f, 0.22800f, 0.21480f, 0.26095f, 0.28920f, 0.24840f, 0.22800f, 0.00000f, 0.26095f, 0.28920f, 0.24840f, 0.22800f, 0.00000f}),
    L36(36, new float[]{0.12400f, 0.13065f, 0.10920f, 0.09425f, 0.08580f, 0.19955f, 0.20485f, 0.17595f, 0.16150f, 0.21480f, 0.19955f, 0.20485f, 0.17595f, 0.16150f, 0.21480f, 0.19955f, 0.20485f, 0.24840f, 0.22800f, 0.21480f, 0.26095f, 0.20485f, 0.24840f, 0.22800f, 0.21480f, 0.26095f, 0.28920f, 0.24840f, 0.22800f, 0.00000f, 0.26095f, 0.28920f, 0.24840f, 0.22800f, 0.00000f}),
    L37(37, new float[]{0.12400f, 0.13065f, 0.10920f, 0.09425f, 0.11220f, 0.19955f, 0.20485f, 0.17595f, 0.16150f, 0.21480f, 0.19955f, 0.20485f, 0.17595f, 0.16150f, 0.15215f, 0.19955f, 0.20485f, 0.24840f, 0.22800f, 0.21480f, 0.26095f, 0.28920f, 0.24840f, 0.22800f, 0.21480f, 0.26095f, 0.28920f, 0.24840f, 0.22800f, 0.00000f, 0.26095f, 0.28920f, 0.24840f, 0.22800f, 0.21480f, 0.00000f}),
    L38(38, new float[]{0.12400f, 0.13065f, 0.10920f, 0.09425f, 0.11220f, 0.19955f, 0.20485f, 0.17595f, 0.16150f, 0.21480f, 0.19955f, 0.20485f, 0.17595f, 0.16150f, 0.21480f, 0.19955f, 0.20485f, 0.24840f, 0.22800f, 0.21480f, 0.26095f, 0.28920f, 0.24840f, 0.22800f, 0.21480f, 0.26095f, 0.28920f, 0.24840f, 0.22800f, 0.21480f, 0.00000f, 0.26095f, 0.28920f, 0.24840f, 0.22800f, 0.21480f, 0.00000f}),
    L39(39, new float[]{0.12400f, 0.13065f, 0.10920f, 0.09425f, 0.11220f, 0.19955f, 0.20485f, 0.17595f, 0.16150f, 0.21480f, 0.19955f, 0.20485f, 0.17595f, 0.16150f, 0.21480f, 0.19955f, 0.20485f, 0.24840f, 0.22800f, 0.21480f, 0.26095f, 0.20485f, 0.24840f, 0.22800f, 0.21480f, 0.00000f, 0.26095f, 0.28920f, 0.24840f, 0.22800f, 0.21480f, 0.00000f, 0.26095f, 0.28920f, 0.24840f, 0.22800f, 0.00000f, 0.00000f}),
    L40(40, new float[]{0.12400f, 0.13065f, 0.10920f, 0.09425f, 0.11220f, 0.19955f, 0.20485f, 0.17595f, 0.16150f, 0.21480f, 0.19955f, 0.20485f, 0.17595f, 0.22800f, 0.21480f, 0.19955f, 0.20485f, 0.24840f, 0.22800f, 0.21480f, 0.20760f, 0.26095f, 0.20485f, 0.24840f, 0.22800f, 0.21480f, 0.00000f, 0.26095f, 0.28920f, 0.24840f, 0.22800f, 0.21480f, 0.00000f, 0.26095f, 0.28920f, 0.24840f, 0.22800f, 0.00000f, 0.00000f}),
    L41(41, new float[]{0.12400f, 0.13065f, 0.10920f, 0.12325f, 0.11220f, 0.19955f, 0.20485f, 0.17595f, 0.22800f, 0.21480f, 0.19955f, 0.20485f, 0.17595f, 0.16150f, 0.21480f, 0.20760f, 0.19955f, 0.20485f, 0.24840f, 0.22800f, 0.21480f, 0.20760f, 0.26095f, 0.20485f, 0.24840f, 0.22800f, 0.21480f, 0.00000f, 0.26095f, 0.28920f, 0.24840f, 0.22800f, 0.21480f, 0.00000f, 0.26095f, 0.28920f, 0.24840f, 0.22800f, 0.00000f, 0.00000f}),
    L42(42, new float[]{0.12400f, 0.13065f, 0.10920f, 0.12325f, 0.11220f, 0.19955f, 0.20485f, 0.17595f, 0.16150f, 0.21480f, 0.20760f, 0.19955f, 0.20485f, 0.17595f, 0.16150f, 0.21480f, 0.20760f, 0.26095f, 0.20485f, 0.24840f, 0.22800f, 0.21480f, 0.20760f, 0.26095f, 0.20485f, 0.24840f, 0.22800f, 0.21480f, 0.00000f, 0.26095f, 0.28920f, 0.24840f, 0.22800f, 0.00000f, 0.00000f, 0.26095f, 0.28920f, 0.24840f, 0.22800f, 0.00000f, 0.00000f}),
    L43(43, new float[]{0.12400f, 0.13065f, 0.10920f, 0.12325f, 0.11220f, 0.19955f, 0.20485f, 0.17595f, 0.16150f, 0.21480f, 0.20760f, 0.19955f, 0.20485f, 0.17595f, 0.16150f, 0.21480f, 0.20760f, 0.26095f, 0.20485f, 0.24840f, 0.22800f, 0.21480f, 0.20760f, 0.26095f, 0.28920f, 0.24840f, 0.22800f, 0.21480f, 0.00000f, 0.26095f, 0.28920f, 0.24840f, 0.22800f, 0.00000f, 0.00000f, 0.26095f, 0.28920f, 0.24840f, 0.22800f, 0.21480f, 0.00000f, 0.00000f}),
    L44(44, new float[]{0.12400f, 0.13065f, 0.10920f, 0.12325f, 0.11220f, 0.19955f, 0.20485f, 0.17595f, 0.22800f, 0.21480f, 0.20760f, 0.19955f, 0.20485f, 0.17595f, 0.16150f, 0.21480f, 0.20760f, 0.26095f, 0.20485f, 0.24840f, 0.22800f, 0.21480f, 0.20760f, 0.26095f, 0.28920f, 0.24840f, 0.22800f, 0.21480f, 0.00000f, 0.26095f, 0.28920f, 0.24840f, 0.22800f, 0.21480f, 0.00000f, 0.00000f, 0.26095f, 0.28920f, 0.24840f, 0.22800f, 0.21480f, 0.00000f, 0.00000f}),
    L45(45, new float[]{0.12400f, 0.13065f, 0.10920f, 0.12325f, 0.11220f, 0.19955f, 0.20485f, 0.17595f, 0.22800f, 0.21480f, 0.20760f, 0.19955f, 0.20485f, 0.17595f, 0.22800f, 0.21480f, 0.20760f, 0.26095f, 0.20485f, 0.24840f, 0.22800f, 0.21480f, 0.20760f, 0.26095f, 0.20485f, 0.24840f, 0.22800f, 0.21480f, 0.00000f, 0.00000f, 0.26095f, 0.28920f, 0.24840f, 0.22800f, 0.21480f, 0.00000f, 0.00000f, 0.26095f, 0.28920f, 0.24840f, 0.22800f, 0.21480f, 0.00000f, 0.00000f}),
    L46(46, new float[]{0.20150f, 0.13065f, 0.10920f, 0.12325f, 0.11220f, 0.19955f, 0.20485f, 0.17595f, 0.22800f, 0.21480f, 0.20760f, 0.19955f, 0.20485f, 0.17595f, 0.22800f, 0.21480f, 0.20760f, 0.26095f, 0.20485f, 0.24840f, 0.22800f, 0.21480f, 0.20760f, 0.19800f, 0.26095f, 0.20485f, 0.24840f, 0.22800f, 0.21480f, 0.00000f, 0.00000f, 0.26095f, 0.28920f, 0.24840f, 0.22800f, 0.21480f, 0.00000f, 0.00000f, 0.26095f, 0.28920f, 0.24840f, 0.22800f, 0.21480f, 0.00000f, 0.00000f}),
    L47(47, new float[]{0.20150f, 0.13065f, 0.10920f, 0.12325f, 0.11220f, 0.19955f, 0.20485f, 0.17595f, 0.22800f, 0.21480f, 0.20760f, 0.19955f, 0.20485f, 0.17595f, 0.22800f, 0.21480f, 0.20760f, 0.19800f, 0.26095f, 0.20485f, 0.24840f, 0.22800f, 0.21480f, 0.20760f, 0.19800f, 0.26095f, 0.20485f, 0.24840f, 0.22800f, 0.21480f, 0.00000f, 0.00000f, 0.26095f, 0.28920f, 0.24840f, 0.22800f, 0.21480f, 0.00000f, 0.00000f, 0.26095f, 0.28920f, 0.24840f, 0.22800f, 0.00000f, 0.00000f, 0.00000f}),
    L48(48, new float[]{0.20150f, 0.13065f, 0.10920f, 0.12325f, 0.11220f, 0.19955f, 0.20485f, 0.17595f, 0.22800f, 0.21480f, 0.20760f, 0.19800f, 0.19955f, 0.20485f, 0.17595f, 0.22800f, 0.21480f, 0.20760f, 0.19800f, 0.26095f, 0.20485f, 0.24840f, 0.22800f, 0.21480f, 0.20760f, 0.19800f, 0.26095f, 0.20485f, 0.24840f, 0.22800f, 0.21480f, 0.00000f, 0.00000f, 0.26095f, 0.28920f, 0.24840f, 0.22800f, 0.00000f, 0.00000f, 0.00000f, 0.26095f, 0.28920f, 0.24840f, 0.22800f, 0.00000f, 0.00000f, 0.00000f}),
    L49(49, new float[]{0.20150f, 0.13065f, 0.10920f, 0.12325f, 0.11220f, 0.19955f, 0.20485f, 0.17595f, 0.22800f, 0.21480f, 0.20760f, 0.19800f, 0.19955f, 0.20485f, 0.17595f, 0.22800f, 0.21480f, 0.20760f, 0.19800f, 0.26095f, 0.20485f, 0.24840f, 0.22800f, 0.21480f, 0.20760f, 0.00000f, 0.26095f, 0.20485f, 0.24840f, 0.22800f, 0.21480f, 0.00000f, 0.00000f, 0.26095f, 0.28920f, 0.24840f, 0.22800f, 0.00000f, 0.00000f, 0.00000f, 0.26095f, 0.28920f, 0.24840f, 0.22800f, 0.21480f, 0.00000f, 0.00000f, 0.00000f}),
    L50(50, new float[]{0.20150f, 0.13065f, 0.10920f, 0.12325f, 0.11220f, 0.19955f, 0.20485f, 0.17595f, 0.22800f, 0.21480f, 0.20760f, 0.19800f, 0.19955f, 0.20485f, 0.17595f, 0.22800f, 0.21480f, 0.20760f, 0.19800f, 0.26095f, 0.20485f, 0.24840f, 0.22800f, 0.21480f, 0.20760f, 0.00000f, 0.26095f, 0.20485f, 0.24840f, 0.22800f, 0.21480f, 0.00000f, 0.00000f, 0.26095f, 0.28920f, 0.24840f, 0.22800f, 0.21480f, 0.00000f, 0.00000f, 0.00000f, 0.26095f, 0.28920f, 0.24840f, 0.22800f, 0.00000f, 0.00000f, 0.00000f, 0.00000f}),
    L51(51, new float[]{0.20150f, 0.13065f, 0.10920f, 0.12325f, 0.11220f, 0.19955f, 0.20485f, 0.17595f, 0.22800f, 0.21480f, 0.20760f, 0.19800f, 0.19955f, 0.20485f, 0.24840f, 0.22800f, 0.21480f, 0.20760f, 0.19800f, 0.26095f, 0.20485f, 0.24840f, 0.22800f, 0.21480f, 0.20760f, 0.00000f, 0.26095f, 0.20485f, 0.24840f, 0.22800f, 0.21480f, 0.00000f, 0.00000f, 0.00000f, 0.26095f, 0.28920f, 0.24840f, 0.22800f, 0.21480f, 0.00000f, 0.00000f, 0.00000f, 0.26095f, 0.28920f, 0.24840f, 0.22800f, 0.21480f, 0.00000f, 0.00000f, 0.00000f}),
    L52(52, new float[]{0.20150f, 0.13065f, 0.14280f, 0.12325f, 0.11220f, 0.19955f, 0.20485f, 0.24840f, 0.22800f, 0.21480f, 0.20760f, 0.19800f, 0.19955f, 0.20485f, 0.17595f, 0.22800f, 0.21480f, 0.20760f, 0.19800f, 0.26095f, 0.20485f, 0.24840f, 0.22800f, 0.21480f, 0.20760f, 0.19800f, 0.00000f, 0.26095f, 0.20485f, 0.24840f, 0.22800f, 0.21480f, 0.00000f, 0.00000f, 0.00000f, 0.26095f, 0.28920f, 0.24840f, 0.22800f, 0.21480f, 0.00000f, 0.00000f, 0.00000f, 0.26095f, 0.28920f, 0.24840f, 0.22800f, 0.21480f, 0.00000f, 0.00000f, 0.00000f}),
    L53(53, new float[]{0.20150f, 0.13065f, 0.14280f, 0.12325f, 0.11220f, 0.19955f, 0.20485f, 0.24840f, 0.22800f, 0.21480f, 0.20760f, 0.19800f, 0.19955f, 0.20485f, 0.17595f, 0.22800f, 0.21480f, 0.20760f, 0.19800f, 0.20400f, 0.26095f, 0.20485f, 0.24840f, 0.22800f, 0.21480f, 0.20760f, 0.19800f, 0.00000f, 0.26095f, 0.20485f, 0.24840f, 0.22800f, 0.21480f, 0.00000f, 0.00000f, 0.00000f, 0.26095f, 0.28920f, 0.24840f, 0.22800f, 0.21480f, 0.00000f, 0.00000f, 0.00000f, 0.26095f, 0.28920f, 0.24840f, 0.22800f, 0.00000f, 0.00000f, 0.00000f, 0.00000f}),
    L54(54, new float[]{0.20150f, 0.13065f, 0.14280f, 0.12325f, 0.11220f, 0.19955f, 0.20485f, 0.17595f, 0.22800f, 0.21480f, 0.20760f, 0.19800f, 0.00000f, 0.19955f, 0.20485f, 0.17595f, 0.22800f, 0.21480f, 0.20760f, 0.19800f, 0.00000f, 0.26095f, 0.20485f, 0.24840f, 0.22800f, 0.21480f, 0.20760f, 0.19800f, 0.00000f, 0.26095f, 0.20485f, 0.24840f, 0.22800f, 0.21480f, 0.00000f, 0.00000f, 0.00000f, 0.26095f, 0.28920f, 0.24840f, 0.22800f, 0.21480f, 0.00000f, 0.00000f, 0.00000f, 0.26095f, 0.28920f, 0.24840f, 0.22800f, 0.00000f, 0.00000f, 0.00000f, 0.00000f}),
    L55(55, new float[]{0.20150f, 0.13065f, 0.14280f, 0.12325f, 0.11220f, 0.19955f, 0.20485f, 0.17595f, 0.22800f, 0.21480f, 0.20760f, 0.19800f, 0.00000f, 0.19955f, 0.20485f, 0.17595f, 0.22800f, 0.21480f, 0.20760f, 0.19800f, 0.00000f, 0.26095f, 0.20485f, 0.24840f, 0.22800f, 0.21480f, 0.20760f, 0.19800f, 0.00000f, 0.26095f, 0.20485f, 0.24840f, 0.22800f, 0.21480f, 0.00000f, 0.00000f, 0.00000f, 0.26095f, 0.28920f, 0.24840f, 0.22800f, 0.00000f, 0.00000f, 0.00000f, 0.00000f, 0.26095f, 0.28920f, 0.24840f, 0.22800f, 0.21480f, 0.00000f, 0.00000f, 0.00000f, 0.00000f}),
    L56(56, new float[]{0.20150f, 0.13065f, 0.14280f, 0.12325f, 0.11220f, 0.19955f, 0.20485f, 0.17595f, 0.22800f, 0.21480f, 0.20760f, 0.19800f, 0.00000f, 0.19955f, 0.20485f, 0.17595f, 0.22800f, 0.21480f, 0.20760f, 0.19800f, 0.00000f, 0.26095f, 0.20485f, 0.24840f, 0.22800f, 0.21480f, 0.20760f, 0.19800f, 0.00000f, 0.26095f, 0.20485f, 0.24840f, 0.22800f, 0.21480f, 0.00000f, 0.00000f, 0.00000f, 0.26095f, 0.28920f, 0.24840f, 0.22800f, 0.21480f, 0.00000f, 0.00000f, 0.00000f, 0.00000f, 0.26095f, 0.28920f, 0.24840f, 0.22800f, 0.00000f, 0.00000f, 0.00000f, 0.00000f, 0.00000f});

    private int mL;
    private float[] mStepSizes;
    private static Map<Integer,StepSizes> sLOOKUP_MAP = new TreeMap<>();

    static
    {
        for(StepSizes stepSizes : StepSizes.values())
        {
            sLOOKUP_MAP.put(stepSizes.mL, stepSizes);
        }
    }

    StepSizes(int L, float[] stepSizes)
    {
        mL = L;
        mStepSizes = stepSizes;
    }

    /**
     * Step sizes for coefficients b3 through b(L + 1) in a zero-based array
     */
    public float[] getStepSizes()
    {
        return mStepSizes;
    }


    /**
     * Lookup step sizes from value L frequency bands
     */
    public static StepSizes fromL(int L)
    {
        return sLOOKUP_MAP.get(L);
    }
}

