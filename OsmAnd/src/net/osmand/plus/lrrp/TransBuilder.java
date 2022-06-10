package net.osmand.plus.lrrp;

import org.json.JSONObject;

import java.util.ArrayList;

public class TransBuilder {

    private final JSONObject mTrans;
    private final ArrayList<String> mStr = new ArrayList<>();

    public TransBuilder(JSONObject trans) {
        mTrans = trans;
    }

    public TransBuilder add(String str) {
        mStr.add(str);
        return this;
    }

    public String trans() {
        StringBuilder res = new StringBuilder();
        for (int i = 0; i < mStr.size(); i++) {
            String value = mTrans != null ? mTrans.optString(mStr.get(i)) : mStr.get(i);
            if (value.isEmpty()) {
                value = mStr.get(i);
            }

            res.append(" ").append(value);
        }

        return res.toString();
    }
}
