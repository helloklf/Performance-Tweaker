package com.omarea.kernel.utils;

import com.omarea.kernel.R;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class CpuFrequencyUtils {

    public static String[] getAvailableFrequencies() {
        String[] frequencies;
        if (new File(Constants.scaling_available_freq).exists()) {
            frequencies = SysUtils.readOutputFromFile(Constants.scaling_available_freq).split(" ");
            return frequencies;
        } else if (new File(Constants.time_in_states).exists()) {
            ArrayList<CpuState> states;
            int i = 0;
            states = TimeInStateReader.TimeInStatesReader().getCpuStateTime(false, false);
            Collections.sort(states);
            frequencies = new String[states.size()];
            for (CpuState object : states) {
                frequencies[i] = String.valueOf(object.getFrequency());
                i++;
            }
            return frequencies;
        } else {
            return new String[]{};
        }
    }
    public static String[] getAvailableFrequenciesBigCore() {
        String[] frequencies;
        if (new File(Constants.scaling_available_freq_bigcore).exists()) {
            frequencies = SysUtils.readOutputFromFile(Constants.scaling_available_freq_bigcore).split(" ");
            return frequencies;
        } else if (new File(Constants.time_in_states).exists()) {
            ArrayList<CpuState> states;
            int i = 0;
            states = TimeInStateReader.TimeInStatesReader().getCpuStateTime(false, false);
            Collections.sort(states);
            frequencies = new String[states.size()];
            for (CpuState object : states) {
                frequencies[i] = String.valueOf(object.getFrequency());
                i++;
            }
            return frequencies;
        } else {
            return new String[]{};
        }
    }

    public static String getCurrentMaxFrequency() {
        return SysUtils.readOutputFromFile(Constants.scaling_max_freq);
    }
    public static String getCurrentMaxFrequencyBigCore() {
        return SysUtils.readOutputFromFile(Constants.scaling_max_freq_bigcore);
    }

    public static String getCurrentMinFrequency() {
        return SysUtils.readOutputFromFile(Constants.scaling_min_freq);
    }
    public static String getCurrentMinFrequencyBigCore() {
        return SysUtils.readOutputFromFile(Constants.scaling_min_freq_bigcore);
    }

    public static String[] getAvailableGovernors() {
        return SysUtils.readOutputFromFile(Constants.scaling_available_governors).split(" ");
    }
    public static String[] getAvailableGovernorsBigCore() {
        return SysUtils.readOutputFromFile(Constants.scaling_available_governors_bigcore).split(" ");
    }

    public static String getCurrentScalingGovernor() {
        return SysUtils.readOutputFromFile(Constants.scaling_governor);
    }
    public static String getCurrentScalingGovernorBigCore() {
        return SysUtils.readOutputFromFile(Constants.scaling_governor_bigcore);
    }

    public static void setMinFrequency(String minFrequency, Context context) {
        ArrayList<String> commands = new ArrayList<>();
        /*
         * prepare commands for each core
		 */
        if (minFrequency != null) {
            for (int i = 0; i < 3; i++) {
                commands.add("chmod 0664 " + Constants.scaling_min_freq.replace("cpu0", "cpu" + i));
                commands.add("echo " + minFrequency + " > " + Constants.scaling_min_freq.replace("cpu0", "cpu" + i));
            }

            boolean success = SysUtils.executeRootCommand(commands);
            if (success) {
                String msg = context.getString(R.string.ok_message);
                Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
            }
        }
    }
    public static void setMinFrequencyBigCore(String minFrequency, Context context) {
        ArrayList<String> commands = new ArrayList<>();
        /*
         * prepare commands for each core
		 */
        if (minFrequency != null) {
            for (int i = 4; i < getCoreCount(); i++) {
                commands.add("chmod 0664 " + Constants.scaling_min_freq_bigcore.replace("cpu0", "cpu" + i));
                commands.add("echo " + minFrequency + " > " + Constants.scaling_min_freq_bigcore.replace("cpu0", "cpu" + i));
            }

            boolean success = SysUtils.executeRootCommand(commands);
            if (success) {
                String msg = context.getString(R.string.ok_message);
                Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
            }
        }
    }

    public static void setMaxFrequency(String maxFrequency, Context context) {
        ArrayList<String> commands = new ArrayList<>();
        /*
         * prepare commands for each core
		 */
        if (maxFrequency != null) {
            for (int i = 0; i < 3; i++) {
                commands.add("chmod 0664 " + Constants.scaling_max_freq.replace("cpu0", "cpu" + i));
                commands.add("echo " + maxFrequency.replace("cpu0", "cpu" + i) + " > " + Constants.scaling_max_freq);
            }

            boolean success = SysUtils.executeRootCommand(commands);
            if (success) {
                String msg = context.getString(R.string.ok_message);
                Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
            }
        }
    }
    public static void setMaxFrequencyBigCore(String maxFrequency, Context context) {
        ArrayList<String> commands = new ArrayList<>();
        /*
         * prepare commands for each core
		 */
        if (maxFrequency != null) {
            for (int i = 3; i < getCoreCount(); i++) {
                commands.add("chmod 0664 " + Constants.scaling_max_freq_bigcore.replace("cpu0", "cpu" + i));
                commands.add("echo " + maxFrequency.replace("cpu0", "cpu" + i) + " > " + Constants.scaling_max_freq_bigcore);
            }

            boolean success = SysUtils.executeRootCommand(commands);
            if (success) {
                String msg = context.getString(R.string.ok_message);
                Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
            }
        }
    }

    public static void setGovernor(String governor, Context context) {
        ArrayList<String> commands = new ArrayList<>();
        /*
         * prepare commands for each core
		 */
        if (governor != null) {
            for (int i = 0; i < 3; i++) {
                commands.add("chmod 0644 " + Constants.scaling_governor.replace("cpu0", "cpu" + i));
                commands.add("echo " + governor + " > " + Constants.scaling_governor.replace("cpu0", "cpu" + i));
            }

            boolean success = SysUtils.executeRootCommand(commands);
            if (success) {
                String msg = context.getString(R.string.governor_applied);
                Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
            }
        }
    }
    public static void setGovernorBigCore(String governor, Context context) {
        ArrayList<String> commands = new ArrayList<>();
        /*
         * prepare commands for each core
		 */
        if (governor != null) {
            for (int i = 3; i < getCoreCount(); i++) {
                commands.add("chmod 0644 " + Constants.scaling_governor_bigcore.replace("cpu0", "cpu" + i));
                commands.add("echo " + governor + " > " + Constants.scaling_governor_bigcore.replace("cpu0", "cpu" + i));
            }

            boolean success = SysUtils.executeRootCommand(commands);
            if (success) {
                String msg = context.getString(R.string.governor_applied);
                Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
            }
        }
    }

    public static int getCoreCount() {
        int cores = 0;
        while (true) {
            File file = new File(Constants.cpufreq_sys_dir.replace("cpu0", "cpu" + cores));
            if (file.exists()) {
                cores++;
            } else {
                return cores;
            }
        }
    }

    public static GovernorProperty[] getGovernorProperties() {
        GovernorProperty[] governorProperties = null;
        String currentGovernor = getCurrentScalingGovernor();
        String filePath = Constants.governor_prop_dir + currentGovernor;
        File f = new File(filePath);

        if (f.exists() || (f = new File(Constants.cpufreq_sys_dir + currentGovernor)).exists()) {
            List<File> govProperties = new ArrayList<>();
            File[] files = f.listFiles();
            if (files == null) {
                //try reading as root
                govProperties = getGovernerPropAsRoot(filePath);
            } else {
                govProperties = Arrays.asList(files);
            }

            if (govProperties.size() != 0) {
                governorProperties = new GovernorProperty[govProperties.size()];

                for (int i = 0; i < governorProperties.length; i++) {
                    governorProperties[i] = new GovernorProperty(govProperties.get(i).getName(),
                            SysUtils.readOutputFromFile(govProperties.get(i).getAbsolutePath()));
                }
            }
        }
        return governorProperties;
    }

    public static ArrayList<File> getGovernerPropAsRoot(String path) {
        DataOutputStream dos;
        InputStream is;
        ArrayList<File> files = new ArrayList<>();
        try {
            Process process = Runtime.getRuntime().exec("su");
            if (process != null) {
                dos = new DataOutputStream(process.getOutputStream());
                dos.writeBytes("ls " + path + "\n");
                dos.writeBytes("exit \n");
                dos.flush();
                dos.close();
                if (process.waitFor() == 0) {
                    is = process.getInputStream();
                    BufferedReader br = new BufferedReader(new InputStreamReader(is));
                    String line;
                    while ((line = br.readLine()) != null) {
                        files.add(new File(path + "/" + line));
                    }
                } else {
                    is = process.getErrorStream();
                    BufferedReader br = new BufferedReader(new InputStreamReader(is));
                    String line;
                    while ((line = br.readLine()) != null) Log.d("error", line);
                }
            }

        } catch (IOException | InterruptedException | IllegalArgumentException e) {
            e.printStackTrace();
        }
        return files;
    }

    public static void setGovernorProperty(GovernorProperty property, Context context) {
        String currentGovernor = getCurrentScalingGovernor();

        String path = Constants.governor_prop_dir
                + currentGovernor
                + "/"
                + property.getGovernorProperty();
        if (!new File(path).exists()) {
            path = Constants.cpufreq_sys_dir
                    + currentGovernor
                    + "/"
                    + property.getGovernorProperty();
        }
        ArrayList<String> commands = new ArrayList<>();

        commands.add("chmod 0644 " + path);
        commands.add("echo " + property.getGovernorPropertyValue() + " > " + path);

        if (SysUtils.executeRootCommand(commands)) {
            String msg = context.getString(R.string.governor_applied);
            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
        }
    }

    public static String[] toMhz(String... values) {
        String[] frequency = new String[values.length];

        for (int i = 0; i < values.length; i++) {
            try {
                frequency[i] = (Integer.parseInt(values[i].trim()) / 1000) + " Mhz";
            } catch (NumberFormatException nfe) {
                nfe.printStackTrace();
            }
        }
        return frequency;
    }
}
