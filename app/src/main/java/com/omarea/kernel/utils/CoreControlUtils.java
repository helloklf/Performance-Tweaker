package com.omarea.kernel.utils;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.omarea.kernel.R;

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

public class CoreControlUtils {
    public static Boolean isSupported () {
        return new File(Constants.core_control_online).exists();
    }

    public static Boolean getCoreOnlineState(int cpuNo) {
        return SysUtils.readOutputFromFile(Constants.core_control_online.replace("0", String.valueOf(cpuNo))).trim().equals("1");
    }

    public static void setCoreOnlineState(int cpuNo, Boolean online, Context context) {
        String number = "cpu" + String.valueOf(cpuNo);
        String val = online ? "1" : "0";
        ArrayList<String> commands = new ArrayList<>();
        String path = (Constants.core_control_online.replace("cpu0", number));
        commands.add("chmod 0664 " + path);
        commands.add("echo " + val + " > " + path);

        boolean success = SysUtils.executeRootCommand(commands);
        if (success) {
            String msg = context.getString(R.string.ok_message);
            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
        }
    }
}
