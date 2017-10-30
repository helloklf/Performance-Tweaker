package com.omarea.kernel.ui.fragments;

import android.content.Context;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.SwitchPreference;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.omarea.kernel.R;
import com.omarea.kernel.utils.BatteryStatsUtils;
import com.omarea.kernel.utils.ChargerBoosterUtils;
import com.omarea.kernel.utils.CoreControlUtils;
import com.omarea.kernel.utils.CpuFrequencyUtils;


public class ChargeBoosterFragment extends PreferenceFragment
        implements Preference.OnPreferenceChangeListener {

    Context context;
    ProgressBar progressBar;
    String currentMaxLimitMa;
    String wramTempValue;
    String allowChargerValue;
    EditTextPreference limitMaxValue;
    EditTextPreference wramTemp;
    SwitchPreference allowCharger;

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
        addPreferencesFromResource(R.xml.charge_booster_preferences);

        limitMaxValue = (EditTextPreference) findPreference("limit_max_value");
        wramTemp = (EditTextPreference) findPreference("wram_temp_value");
        allowCharger = (SwitchPreference) findPreference("allow_charger");
        limitMaxValue.setOnPreferenceChangeListener(this);
        wramTemp.setOnPreferenceChangeListener(this);
        allowCharger.setOnPreferenceChangeListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        updateData();
        updatePreferences();
        progressBar.setVisibility(View.GONE);
    }

    public void updatePreferences() {
        limitMaxValue.setText(currentMaxLimitMa);
        limitMaxValue.setSummary(currentMaxLimitMa);
        wramTemp.setText(wramTempValue);
        wramTemp.setSummary(wramTempValue);
        allowCharger.setChecked(allowChargerValue.equals("1"));
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object o) {
        String val = o.toString();
        if (preference.getKey().equals("limit_max_value")) {
            if (val.length() >4) {
                Toast.makeText(context, R.string.value_to_lager, Toast.LENGTH_LONG).show();
                return true;
            }
            ChargerBoosterUtils.setLimitMaxMa(o.toString(), context);
        } else if (preference.getKey().equals("wram_temp_value")) {
            if (val.length() != 2) {
                Toast.makeText(context, R.string.value_to_lager, Toast.LENGTH_LONG).show();
                return true;
            }
            ChargerBoosterUtils.setWramTemepLimit(o.toString(), context);
        } else if (preference.getKey().equals("allow_charger")) {
            ChargerBoosterUtils.setAllowCharger((Boolean) o, context);
        }

        updateData();
        updatePreferences();
        return true;
    }

    public void updateData() {
        currentMaxLimitMa = ChargerBoosterUtils.getCurrentLimitMaxMa();
        wramTempValue = ChargerBoosterUtils.getWramTemp();
        allowChargerValue = ChargerBoosterUtils.getAllowCharger();
        if (currentMaxLimitMa.length() ==0) {
            limitMaxValue.setSummary(context.getString(R.string.unsupport_this_device));
            limitMaxValue.setEnabled(false);
        }
        if (allowChargerValue.length() ==0) {
            allowCharger.setSummary(context.getString(R.string.unsupport_this_device));
            allowCharger.setEnabled(false);
            allowChargerValue = "1";
        }
        if (wramTempValue.length() == 0) {
            wramTemp.setSummary(context.getString(R.string.unsupport_this_device));
            wramTemp.setText(context.getString(R.string.unsupport_this_device));
            wramTemp.setEnabled(false);
        }
    }
}
