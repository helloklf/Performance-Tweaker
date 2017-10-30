package com.omarea.kernel.ui.fragments;

import android.content.Context;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.SwitchPreference;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.omarea.kernel.R;
import com.omarea.kernel.utils.CoreControlUtils;
import com.omarea.kernel.utils.CpuFrequencyUtils;
import com.omarea.kernel.utils.ThermalControlUtils;


public class ThermalFragment extends PreferenceFragment
        implements Preference.OnPreferenceChangeListener {

    Context context;
    ProgressBar progressBar;
    SwitchPreference ThermalVDDRestriction,ThermalParameters,TheramlCoreControl;

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
        addPreferencesFromResource(R.xml.thermal_preference);

        ThermalVDDRestriction = getSwitchPreference("thermal_vdd_restriction");
        TheramlCoreControl = getSwitchPreference("thermal_core_control");
        ThermalParameters = getSwitchPreference("thermal_parameters");
    }

    private SwitchPreference getSwitchPreference(String key) {
        SwitchPreference cb = (SwitchPreference) findPreference(key);
        if (cb!=null) {
            cb.setOnPreferenceChangeListener(this);
        }
        return cb;
    }

    @Override
    public void onResume() {
        super.onResume();
        updatePreferences();
        progressBar.setVisibility(View.GONE);
    }

    public void updatePreferences() {
        String vddState = ThermalControlUtils.getVDDRestrictionState();
        if (vddState.length() == 0) {
            ThermalVDDRestriction.setEnabled(false);
        } else {
            ThermalVDDRestriction.setChecked(vddState.equals("1"));
        }

        String coreControlState = ThermalControlUtils.getCoreControlState();
        if (coreControlState.length() == 0) {
            TheramlCoreControl.setEnabled(false);
        } else {
            TheramlCoreControl.setChecked(coreControlState.equals("1"));
        }


        String thermalControlState = ThermalControlUtils.getTheramlState();
        if (thermalControlState.length() == 0) {
            ThermalParameters.setEnabled(false);
        } else {
            ThermalParameters.setChecked(thermalControlState.equals("Y"));
        }
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object o) {
        String key = preference.getKey();
        Boolean val = (Boolean)o;
        switch (key) {
            case "thermal_vdd_restriction": {
                ThermalControlUtils.setVDDRestrictionState(val,context);
                break;
            }
            case "thermal_core_control": {
                ThermalControlUtils.setCoreControlState(val,context);
                break;
            }
            case "thermal_parameters": {
                ThermalControlUtils.setTheramlState(val,context);
                break;
            }
        }

        updatePreferences();
        return true;
    }
}
