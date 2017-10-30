package com.omarea.kernel.ui.fragments;

import android.content.Context;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.MultiSelectListPreference;
import android.preference.Preference;
import android.preference.PreferenceCategory;
import android.preference.PreferenceFragment;
import android.preference.SwitchPreference;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.omarea.kernel.R;
import com.omarea.kernel.utils.ChargerBoosterUtils;
import com.omarea.kernel.utils.CpuSetUtils;

import java.util.HashSet;


public class CpusetFragment extends PreferenceFragment
        implements Preference.OnPreferenceChangeListener {

    Context context;
    ProgressBar progressBar;

    PreferenceCategory CpusetRanage;
    MultiSelectListPreference CpusetSystemBackgroundCpus;
    MultiSelectListPreference CpusetBackgroundCpus;
    MultiSelectListPreference CpusetForegroundCpus;
    MultiSelectListPreference CpusetForegroundBoostCpus;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        progressBar = getActivity().findViewById(R.id.loading_main);
        progressBar.setVisibility(View.VISIBLE);
        return inflater.inflate(R.layout.fragment_pref_container, container, false);
    }

    private HashSet<String> parseCpuSet(String value){
        String[] sets = value.split(",");
        HashSet<String> values = new HashSet<String>();
        for (int i=0;i<sets.length;i++) {
            String[] cpus = sets[i].split("-");
            if (cpus.length > 1) {
                int min = Integer.parseInt(cpus[0]);
                int max = Integer.parseInt(cpus[1]);
                for (int j = min; j <= max; j++) {
                    values.add(j + "");
                }
            } else {
                values.add(cpus[0]);
            }
        }
        return values;
    }

    private String joinHashSetString(HashSet<String> stringSet) {
        StringBuffer stringBuffer = new StringBuffer();
        for (String item:stringSet) {
            stringBuffer.append(item);
            stringBuffer.append(",");
        }
        return stringBuffer.subSequence(0, stringBuffer.length()- 1).toString();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        context = getActivity().getBaseContext();
        addPreferencesFromResource(R.xml.cpuset_preferences);

        CpusetRanage = (PreferenceCategory) findPreference("cpuset_ranage");
        CpusetSystemBackgroundCpus = (MultiSelectListPreference) findPreference("cpuset_system_background_cpus");
        CpusetBackgroundCpus = (MultiSelectListPreference) findPreference("cpuset_background_cpus");
        CpusetForegroundCpus = (MultiSelectListPreference) findPreference("cpuset_foreground_cpus");
        CpusetForegroundBoostCpus = (MultiSelectListPreference) findPreference("cpuset_foreground_boost_cpus");

        CpusetSystemBackgroundCpus.setOnPreferenceChangeListener(this);
        CpusetBackgroundCpus.setOnPreferenceChangeListener(this);

        int[] ranage = CpuSetUtils.getCpuSet();
        CpusetRanage.setTitle(context.getString(R.string.cpus_ranage)+ ranage[0] + "~" + ranage[1]);
        String[] arr = new String[ranage[1] + 1];
        String[] arr2 = new String[ranage[1] + 1];
        for (int i=0;i<= ranage[1];i++) {
            arr[i] = "CPU" + i;
            arr2[i] = "" + i;
        }

        CpusetSystemBackgroundCpus.setEntries(arr);
        CpusetSystemBackgroundCpus.setEntryValues(arr2);
        CpusetBackgroundCpus.setEntries(arr);
        CpusetBackgroundCpus.setEntryValues(arr2);
    }

    @Override
    public void onResume() {
        super.onResume();
        updatePreferences();
        progressBar.setVisibility(View.GONE);
    }

    public void updatePreferences() {
        String backgroundCpus = CpuSetUtils.getCpuBackgroundSet();
        String systemBackgroundCpus = CpuSetUtils.getCpuSystemBackgroundSet();
        String foregroundCpus = CpuSetUtils.getCpuForegroundSet();
        String foregroundBoostCpus = CpuSetUtils.getCpuForegroundBoostSet();

        CpusetBackgroundCpus.setTitle("Background Cpus："+ backgroundCpus);
        CpusetSystemBackgroundCpus.setTitle("System Background Cpus："+ systemBackgroundCpus);
        CpusetForegroundCpus.setTitle("Foreground Cpus："+ foregroundCpus);
        CpusetForegroundBoostCpus.setTitle("Foreground Boost Cpus："+ foregroundBoostCpus);

        CpusetBackgroundCpus.setValues(parseCpuSet(backgroundCpus));
        CpusetSystemBackgroundCpus.setValues(parseCpuSet(systemBackgroundCpus));
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object o) {
        HashSet<String> valus = (HashSet<String>) o;
        if (valus.size() == 0){
            Toast.makeText(context, R.string.invalid_cpuset, Toast.LENGTH_SHORT).show();
            return true;
        }

        if (preference.getKey().equals("cpuset_background_cpus")) {
            CpuSetUtils.setCpuBackgroundSet(joinHashSetString(valus), context);
        } else if(preference.getKey().equals("cpuset_system_background_cpus")) {
            CpuSetUtils.setCpuSystemBackgroundSet(joinHashSetString(valus), context);
        }
        updatePreferences();
        return true;
    }
}
