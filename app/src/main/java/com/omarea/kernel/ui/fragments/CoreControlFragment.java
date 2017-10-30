package com.omarea.kernel.ui.fragments;

import android.app.FragmentTransaction;
import android.content.Context;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.omarea.kernel.R;
import com.omarea.kernel.utils.Constants;
import com.omarea.kernel.utils.CoreControlUtils;
import com.omarea.kernel.utils.CpuFrequencyUtils;

import java.lang.reflect.Array;


public class CoreControlFragment extends PreferenceFragment
        implements Preference.OnPreferenceChangeListener {

    CheckBoxPreference[] cores;
    Boolean[] onlineStates;
    Context context;
    ProgressBar progressBar;
    Boolean hasBigCores = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        progressBar = getActivity().findViewById(R.id.loading_main);
        progressBar.setVisibility(View.VISIBLE);
        return inflater.inflate(R.layout.fragment_pref_container, container, false);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        context = getActivity().getBaseContext();
        addPreferencesFromResource(R.xml.core_control_preference);


        hasBigCores = CpuFrequencyUtils.getCoreCount() > 4;
        cores = new CheckBoxPreference[8];
        for (int i = 0;i < (hasBigCores? 8:4); i++) {
            cores[i] = getCheckBoxPreference("core_control_core_" + i);
        }
        if (!hasBigCores) {
            getPreferenceScreen().removePreference(findPreference("core_control_bigcores"));
        }

    }

    private CheckBoxPreference getCheckBoxPreference(String key) {
        CheckBoxPreference cb = (CheckBoxPreference) findPreference(key);
        if (cb!=null) {
            cb.setOnPreferenceChangeListener(this);
        }
        return cb;
    }

    @Override
    public void onResume() {
        super.onResume();
        updateData();
        updatePreferences();
        progressBar.setVisibility(View.GONE);
    }

    public void updatePreferences() {
        if (cores == null || onlineStates == null) {
            return;
        }
        for (int i = 0;i < (hasBigCores? 8:4); i++) {
            cores[i].setChecked(onlineStates[i]);
        }
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object o) {
        for (int i = 0; i < 8; i++) {
            if (preference.getKey().endsWith("core_" + i)) {
                CoreControlUtils.setCoreOnlineState(i, (Boolean)o, context);
                break;
            }
        }
        updateData();
        updatePreferences();
        return true;
    }

    public void updateData() {
        onlineStates = new Boolean[8];
        for (int i = 0;i < (hasBigCores? 8:4); i++) {
            onlineStates[i] = CoreControlUtils.getCoreOnlineState(i);
        }
    }
}
