package com.omarea.kernel.ui.fragments;

import android.os.Bundle;
import android.preference.MultiSelectListPreference;
import android.preference.PreferenceFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.omarea.kernel.R;
import com.omarea.kernel.utils.GpuUtils;

import java.util.ArrayList;

public class SettingsFragment extends PreferenceFragment {

    MultiSelectListPreference mMultiSelectListPreference;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_pref_container, container, false);
    }

    @Override
    public void onCreate(Bundle paramBundle) {
        super.onCreate(paramBundle);
        addPreferencesFromResource(R.xml.settings_preferences);

        mMultiSelectListPreference = (MultiSelectListPreference) findPreference("select_apply_on_boot");
        ArrayList<CharSequence> entries = new ArrayList<>();
        entries.add(getString(R.string.cpu));
        if (GpuUtils.isGpuFrequencyScalingSupported()) {
            entries.add(getString(R.string.gpu));
        }
        entries.add(getString(R.string.io));
        entries.add(getString(R.string.build_prop));
        entries.add(getString(R.string.vm));

        CharSequence[] charSequences = new CharSequence[entries.size()];
        for (int i = 0; i < entries.size(); i++) {
            charSequences[i] = entries.get(i);
        }
        mMultiSelectListPreference.setEntries(charSequences);
        mMultiSelectListPreference.setEntryValues(charSequences);
    }

}
