package com.omarea.kernel.utils;

public class SystemAppManagementException extends Exception {

    public SystemAppManagementException(String msg) {
        super(msg);
    }

    public SystemAppManagementException(String msg, Throwable e) {
        super(msg, e);
    }
}