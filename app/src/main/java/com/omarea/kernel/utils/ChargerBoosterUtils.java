package com.omarea.kernel.utils;

import android.content.Context;
import android.widget.Toast;

import com.omarea.kernel.R;

import java.io.File;
import java.util.ArrayList;

public class ChargerBoosterUtils {
    public static Boolean isSupported() {
        return new File(Constants.constant_charge_current_max).exists();
    }

    public static String getCurrentLimitMaxMa() {
        String value = SysUtils.readOutputFromFile(Constants.constant_charge_current_max).trim();
        if (value.length() > 3) {
            value = value.substring(0, value.length() - 3);
        }
        return value;
    }

    private static int tempLength = 3;

    public static String getWramTemp() {
        String value = SysUtils.readOutputFromFile(Constants.constant_charge_warm_temp).trim();
        tempLength = value.length();
        if (tempLength > 2) {
            value = value.substring(0, 2);
        }
        return value;
    }

    public static String getAllowCharger() {
        String path;
        if (new File(Constants.charge_allow0).exists()) {
            return SysUtils.readOutputFromFile(Constants.charge_allow0).trim();
        } else if (new File(Constants.charge_allow1).exists()) {
            return SysUtils.readOutputFromFile(Constants.charge_allow1).trim();
        } else if (new File(Constants.charge_disabled).exists()) {
            String value = SysUtils.readOutputFromFile(Constants.charge_disabled).trim();
            return value.equals("1") ? "0" : (value.equals("0") ? "1" : value);
        } else {
            return "";
        }
    }

    public static void setLimitMaxMa(String max, Context context) {
        ArrayList<String> commands = new ArrayList<>();
        commands.add("chmod 0664 " + Constants.constant_charge_current_max);
        commands.add("echo " + max + "000 > " + Constants.constant_charge_current_max);

        boolean success = SysUtils.executeRootCommand(commands);
        if (success) {
            String msg = context.getString(R.string.ok_message);
            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
        }
    }

    public static void setWramTemepLimit(String max, Context context) {
        ArrayList<String> commands = new ArrayList<>();

        int zeroLength = tempLength - 2;
        String val = max;
        //补0 确保温度单位和读取到的一致
        for (int i = 0; i < zeroLength; i++) {
            val = (val + "0");
        }
        commands.add("chmod 0664 " + Constants.constant_charge_warm_temp);
        commands.add("echo " + max + "000 > " + Constants.constant_charge_warm_temp);

        boolean success = SysUtils.executeRootCommand(commands);
        if (success) {
            String msg = context.getString(R.string.ok_message);
            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
        }
    }

    public static void setAllowCharger(Boolean allowCharger, Context context) {
        ArrayList<String> commands = new ArrayList<>();
        if (new File(Constants.charge_allow0).exists()) {
            commands.add("chmod 0664 " + Constants.charge_allow0);
            commands.add("echo " + (allowCharger ? 1 : 0) + " > " + Constants.charge_allow0);
        } else if (new File(Constants.charge_allow1).exists()) {
            commands.add("chmod 0664 " + Constants.charge_allow1);
            commands.add("echo " + (allowCharger ? 1 : 0) + " > " + Constants.charge_allow1);
        } else if (new File(Constants.charge_disabled).exists()) {
            commands.add("chmod 0664 " + Constants.charge_disabled);
            commands.add("echo " + (allowCharger ? 0 : 1) + " > " + Constants.charge_disabled);
        } else {
            return;
        }

        boolean success = SysUtils.executeRootCommand(commands);
        if (success) {
            String msg = context.getString(R.string.ok_message);
            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
        }
    }
}
