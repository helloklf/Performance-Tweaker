package com.omarea.kernel.utils;

import android.content.Context;
import android.widget.Toast;

import com.omarea.kernel.R;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;

public class CpuSetUtils {
    public static Boolean isSupported() {
        return new File(Constants.cpuset_background_cpus).exists() && new File(Constants.cpuset_cpus).exists();
    }

    public static int[] getCpuSet() {
        String value = SysUtils.readOutputFromFile(Constants.cpuset_cpus).trim();
        String[] cpus = value.split("-");
        int min = Integer.parseInt(cpus[0]);
        int max = Integer.parseInt(cpus[1]);
        return new int[]{
            min, max
        };
    }

    public static String getCpuBackgroundSet(){
        return SysUtils.readOutputFromFile(Constants.cpuset_background_cpus).trim();
    }

    public static String getCpuSystemBackgroundSet(){
        return SysUtils.readOutputFromFile(Constants.cpuset_system_background_cpus).trim();
    }

    public static String getCpuForegroundSet(){
        return SysUtils.readOutputFromFile(Constants.cpuset_foreground_cpus).trim();
    }

    public static String getCpuForegroundBoostSet(){
        return SysUtils.readOutputFromFile(Constants.cpuset_foreground_boost_cpus).trim();
    }


    public static void setCpuSystemBackgroundSet(String cpus, Context context) {
        setProp(cpus, Constants.cpuset_system_background_cpus, context);
    }

    public static void setCpuBackgroundSet(String cpus, Context context) {
        setProp(cpus, Constants.cpuset_background_cpus, context);
    }

    private static void setProp(String cpus, String path, Context context) {
        ArrayList<String> commands = new ArrayList<>();
        commands.add("chmod 0664 " + path);
        commands.add("echo " + cpus + " > " + path);

        boolean success = SysUtils.executeRootCommand(commands);
        if (success) {
            String msg = context.getString(R.string.ok_message);
            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
        }
    }
}
