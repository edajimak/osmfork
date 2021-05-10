package net.osmand.plus.lrrp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import net.osmand.plus.R;
import net.osmand.plus.helpers.AndroidUiHelper;
import net.osmand.plus.settings.fragments.BaseSettingsFragment;

import static net.osmand.plus.activities.PluginInfoFragment.PLUGIN_INFO;

public class LrrpSettingsFragment extends BaseSettingsFragment {

    boolean showSwitchProfile = false;

    @Override
    protected void setupPreferences() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle args = getArguments();
        if (args != null) {
            showSwitchProfile = args.getBoolean(PLUGIN_INFO, false);
        }
    }

    @Override
    protected void createToolbar(LayoutInflater inflater, View view) {
        super.createToolbar(inflater, view);

        View switchProfile = view.findViewById(R.id.profile_button);
        if (switchProfile != null) {
            AndroidUiHelper.updateVisibility(switchProfile, showSwitchProfile);
        }
    }

    @Override
    public Bundle buildArguments() {
        Bundle args = super.buildArguments();
        args.putBoolean(PLUGIN_INFO, showSwitchProfile);
        return args;
    }
}
