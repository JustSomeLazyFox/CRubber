package com.foppykitty.debugging;

public class Debugger {
    public static boolean isProduction = false;

    public static boolean keyCodeLoggingEnabled = false;
    public static boolean mouseButtonCodeLoggingEnabled = false;

    public static boolean shouldTraceLog = false;
    public static boolean shouldTraceInfo = false;
    public static boolean shouldTraceWarn = true;
    public static boolean shouldTraceError = true;
    public static boolean shouldTraceCheck = false;
    public static boolean shouldTraceSuccess = false;

    public static void enableDebugging() {
        isProduction = false;
    }

    public static void disableDebugging() {
        isProduction = true;
    }

    public static boolean isEnabled() {
        return !isProduction;
    }

    public static boolean isKeyCodeLoggingEnabled() {
        return keyCodeLoggingEnabled;
    }

    public static void setKeyCodeLoggingEnabled(boolean keyCodeLoggingEnabled) {
        Debugger.keyCodeLoggingEnabled = keyCodeLoggingEnabled;
    }

    public static boolean isMouseButtonCodeLoggingEnabled() {
        return mouseButtonCodeLoggingEnabled;
    }

    public static void setMouseButtonCodeLoggingEnabled(boolean mouseButtonCodeLoggingEnabled) {
        Debugger.mouseButtonCodeLoggingEnabled = mouseButtonCodeLoggingEnabled;
    }

}
