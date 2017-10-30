package com.omarea.kernel.utils;

import java.util.ArrayList;

public class CPUHotplugUtils implements Constants {

    public static void activateMpdecision(boolean active) {
        ArrayList<String> cmd = new ArrayList<>();
        if (active) {
            cmd.add("start " + HOTPLUG_MPDEC);
        } else {
            cmd.add("stop " + HOTPLUG_MPDEC);
        }
        SysUtils.executeRootCommand(cmd);
    }

    public static boolean isMpdecisionActive() {
        return SysUtils.isPropActive(HOTPLUG_MPDEC);
    }

    public static boolean hasMpdecision() {
        return SysUtils.executeCommandWithOutput(true, "getprop | grep mpdecision \n").length() > 1;
    }

    public static boolean hasCpuHotplug() {
        return hasMpdecision();
    }
}