package com.omarea.kernel.ui.fragments;

import android.app.FragmentTransaction;
import android.content.Context;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.omarea.kernel.R;
import com.omarea.kernel.utils.Constants;
import com.omarea.kernel.utils.CpuFrequencyUtils;


public class CpuFrequencyFragment extends PreferenceFragment
        implements Preference.OnPreferenceChangeListener {

    String[] availablefreq;
    String[] availableGovernors;
    String maxFrequency, minFrequency, currentGovernor;
    ListPreference CpuMaxFreqPreference;
    ListPreference CpuMinFreqPreference;
    ListPreference GovernorPreference;
    Preference preference;
    Context context;
    ProgressBar progressBar;

    Boolean hasBigCores = false;
    String[] availablefreqBigCore;
    String[] availableGovernorsBigCore;
    String maxFrequencyBigCore, minFrequencyBigCore, currentGovernorBigCore;
    ListPreference CpuMaxFreqPreferenceBigCore;
    ListPreference CpuMinFreqPreferenceBigCore;
    ListPreference GovernorPreferenceBigCore;
    Preference preferenceBigCore;

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
        addPreferencesFromResource(R.xml.cpu_freq_preference);

        CpuMaxFreqPreference = (ListPreference) findPreference(Constants.PREF_CPU_MAX_FREQ);
        CpuMinFreqPreference = (ListPreference) findPreference(Constants.PREF_CPU_MIN_FREQ);
        GovernorPreference = (ListPreference) findPreference(Constants.PREF_CPU_GOV);
        preference = findPreference("governor_tune_pref");

        availablefreq = CpuFrequencyUtils.getAvailableFrequencies();
        availableGovernors = CpuFrequencyUtils.getAvailableGovernors();
        populatePreferences();

        CpuMaxFreqPreference.setOnPreferenceChangeListener(this);
        CpuMinFreqPreference.setOnPreferenceChangeListener(this);
        GovernorPreference.setOnPreferenceChangeListener(this);
        preference.setOnPreferenceChangeListener(this);
        preference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                fragmentTransaction.setCustomAnimations(R.animator.enter_anim, R.animator.exit_animation);
                fragmentTransaction.replace(R.id.main_content, new GovernorTuningFragment(),
                        GovernorTuningFragment.TAG).commit();
                return true;
            }
        });
        if (CpuFrequencyUtils.getCoreCount() > 4) {
            hasBigCores = true;
            addPreferencesFromResource(R.xml.cpu_freq_preference_cluster2);

            CpuMaxFreqPreferenceBigCore = (ListPreference) findPreference(Constants.PREF_CPU_MAX_FREQ + "2");
            CpuMinFreqPreferenceBigCore = (ListPreference) findPreference(Constants.PREF_CPU_MIN_FREQ + "2");
            GovernorPreferenceBigCore = (ListPreference) findPreference(Constants.PREF_CPU_GOV + "2");
            preferenceBigCore = findPreference("governor_tune_pref" + "2");

            availablefreqBigCore = CpuFrequencyUtils.getAvailableFrequenciesBigCore();
            availableGovernorsBigCore = CpuFrequencyUtils.getAvailableGovernorsBigCore();
            //TODO: what is this
            populatePreferences();

            CpuMaxFreqPreferenceBigCore.setOnPreferenceChangeListener(this);
            CpuMinFreqPreferenceBigCore.setOnPreferenceChangeListener(this);
            GovernorPreferenceBigCore.setOnPreferenceChangeListener(this);
            preferenceBigCore.setOnPreferenceChangeListener(this);
            preferenceBigCore.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                    fragmentTransaction.setCustomAnimations(R.animator.enter_anim, R.animator.exit_animation);
                    fragmentTransaction.replace(R.id.main_content, new GovernorTuningFragment(),
                            GovernorTuningFragment.TAG).commit();
                    return true;
                }
            });
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        updateData();
        updatePreferences();
        progressBar.setVisibility(View.GONE);
    }

    public void populatePreferences() {
        updateData();

        if (availablefreq != null) {
            CpuMaxFreqPreference.setEntries(CpuFrequencyUtils.toMhz(availablefreq));
            CpuMaxFreqPreference.setEntryValues(availablefreq);
            CpuMinFreqPreference.setEntries(CpuFrequencyUtils.toMhz(availablefreq));
            CpuMinFreqPreference.setEntryValues(availablefreq);
        }
        if (availableGovernors != null) {
            GovernorPreference.setEntries(availableGovernors);
            GovernorPreference.setEntryValues(availableGovernors);
        }
        if (maxFrequency != null && minFrequency != null && currentGovernor != null) {
            updatePreferences();
        }

        if (hasBigCores) {
            if (availablefreqBigCore != null) {
                CpuMaxFreqPreferenceBigCore.setEntries(CpuFrequencyUtils.toMhz(availablefreqBigCore));
                CpuMaxFreqPreferenceBigCore.setEntryValues(availablefreqBigCore);
                CpuMinFreqPreferenceBigCore.setEntries(CpuFrequencyUtils.toMhz(availablefreqBigCore));
                CpuMinFreqPreferenceBigCore.setEntryValues(availablefreqBigCore);
            }
            if (availableGovernorsBigCore != null) {
                GovernorPreferenceBigCore.setEntries(availableGovernorsBigCore);
                GovernorPreferenceBigCore.setEntryValues(availableGovernorsBigCore);
            }
            if (maxFrequencyBigCore != null && minFrequencyBigCore != null && currentGovernorBigCore != null) {
                updatePreferences();
            }
        }
    }

    public void updatePreferences() {
        CpuMaxFreqPreference.setValue(maxFrequency);
        CpuMinFreqPreference.setValue(minFrequency);
        GovernorPreference.setValue(currentGovernor);

        CpuMinFreqPreference.setSummary(CpuFrequencyUtils.toMhz(minFrequency)[0]);
        CpuMaxFreqPreference.setSummary(CpuFrequencyUtils.toMhz(maxFrequency)[0]);
        GovernorPreference.setSummary(currentGovernor);

        if (hasBigCores) {
            CpuMaxFreqPreferenceBigCore.setValue(maxFrequencyBigCore);
            CpuMinFreqPreferenceBigCore.setValue(minFrequencyBigCore);
            GovernorPreferenceBigCore.setValue(currentGovernorBigCore);

            CpuMinFreqPreferenceBigCore.setSummary(CpuFrequencyUtils.toMhz(minFrequencyBigCore)[0]);
            CpuMaxFreqPreferenceBigCore.setSummary(CpuFrequencyUtils.toMhz(maxFrequencyBigCore)[0]);
            GovernorPreferenceBigCore.setSummary(currentGovernorBigCore);
        }
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object o) {
        if (preference.getKey().equals(Constants.PREF_CPU_MIN_FREQ)) {
            CpuFrequencyUtils.setMinFrequency(o.toString(), context);
        }
        else if (preference.getKey().equals(Constants.PREF_CPU_MAX_FREQ)) {
            CpuFrequencyUtils.setMaxFrequency(o.toString(), context);
        }
        if (preference.getKey().equals(Constants.PREF_CPU_GOV)) {
            CpuFrequencyUtils.setGovernor(o.toString(), context);
        }
        else if (hasBigCores) {
            if (preference.getKey().equals(Constants.PREF_CPU_MIN_FREQ + "2")) {
                CpuFrequencyUtils.setMinFrequencyBigCore(o.toString(), context);
            }
            else if (preference.getKey().equals(Constants.PREF_CPU_MAX_FREQ + "2")) {
                CpuFrequencyUtils.setMaxFrequencyBigCore(o.toString(), context);
            }
            if (preference.getKey().equals(Constants.PREF_CPU_GOV + "2")) {
                CpuFrequencyUtils.setGovernorBigCore(o.toString(), context);
            }
        }
        updateData();
        updatePreferences();
        return true;
    }

    public void updateData() {
        currentGovernor = CpuFrequencyUtils.getCurrentScalingGovernor();
        maxFrequency = CpuFrequencyUtils.getCurrentMaxFrequency();
        minFrequency = CpuFrequencyUtils.getCurrentMinFrequency();

        if (hasBigCores) {
            currentGovernorBigCore = CpuFrequencyUtils.getCurrentScalingGovernorBigCore();
            maxFrequencyBigCore = CpuFrequencyUtils.getCurrentMaxFrequencyBigCore();
            minFrequencyBigCore = CpuFrequencyUtils.getCurrentMinFrequencyBigCore();
        }
    }
}
