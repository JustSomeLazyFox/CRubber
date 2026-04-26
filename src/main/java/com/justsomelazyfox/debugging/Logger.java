package com.justsomelazyfox.debugging;

import manifold.rt.api.util.Pair;

import java.util.*;
import java.util.function.Function;

public class Logger extends Debugger {
//    private static final Map<String, LoggingFunctionContainer> customLoggingFunctions = new HashMap<>();
    private static final List<Pair<String, Boolean>> logQueues = new ArrayList<>();

    private static boolean rateLimitingEnabled = false;
    private static float rateLimitMilliSeconds = 1000f;
    private static long lastLogTime;

    private static boolean hasJustExitedGroupLoggingScope = false;
    private static boolean insideGroupLoggingScope = false;

    public static class AnsiConstructor {
        private String ansi = "";

        public static class ConsoleColors {
            public static final String COLOR_DEFAULT_YELLOW = "3";
            public static final String COLOR_DEFAULT_BLUE = "4";
            public static final String COLOR_DEFAULT_GREEN = "2";
            public static final String COLOR_DEFAULT_RED = "1";
            public static final String COLOR_DEFAULT_WHITE = "7";
            public static final String COLOR_DEFAULT_BLACK = "0";
            public static final String COLOR_DEFAULT_PURPLE = "8:2:180:128:255";
            public static final String COLOR_DEFAULT_CYAN = "6";
            public static final String COLOR_DEFAULT_PINK = "8:5:13";
        }

        public AnsiConstructor reset() {
            this.ansi += "\u001B[0m";
            return this;
        }

        public AnsiConstructor underline() {
            this.ansi += "\u001B[4m";
            return this;
        }

        public AnsiConstructor strikethrough() {
            this.ansi += "\u001B[9m";
            return this;
        }

        public AnsiConstructor bold() {
            this.ansi += "\u001B[1m";
            return this;
        }

        public AnsiConstructor italicize() {
            this.ansi += "\u001B[3m";
            return this;
        }

        public AnsiConstructor unbold() {
            this.ansi += "\u001B[22m";
            return this;
        }

        public AnsiConstructor unitalicize() {
            this.ansi += "\u001B[23m";
            return this;
        }

        public AnsiConstructor defaultTextColor() {
            this.ansi += "\u001B[39m";
            return this;
        }

        public AnsiConstructor defaultBackgroundColor() {
            this.ansi += "\u001B[49m";
            return this;
        }

        public AnsiConstructor invert() {
            this.ansi += "\u001B[7m";
            return this;
        }

        public AnsiConstructor text(String text) {
            this.ansi += text;
            return this;
        }

        public AnsiConstructor setPredefinedTextColor(String color) {
            this.ansi += "\u001B[3" + color + "m";
            return this;
        }

        public AnsiConstructor setTextColor(int r, int g, int b) {
            this.ansi += "\u001B[38:2:" + r + ":" + g + ":" + b + "m";
            return this;
        }

        public AnsiConstructor setPredefinedBackgroundColor(String color) {
            this.ansi += "\u001B[4" + color + "m";
            return this;
        }

        public AnsiConstructor setBackgroundColor(int r, int g, int b) {
            this.ansi += "\u001B[48:2:" + r + ":" + g + ":" + b + "m";
            return this;
        }

        public String getAnsi() {
            this.ansi += "\u001B[0m";
            return this.ansi;
        }
    }

    private static boolean shouldReturnDueToRateLimit(Boolean shouldRateLimit) {
        if (shouldRateLimit == null) {
            if (isRateLimitingEnabled()) {
                long timeDifference = System.currentTimeMillis() - lastLogTime;
                if (timeDifference < rateLimitMilliSeconds) {
                    return true;
                }
            }
        } else {
            if (shouldRateLimit) {
                long timeDifference = System.currentTimeMillis() - lastLogTime;
                if (timeDifference < rateLimitMilliSeconds) {
                    return true;
                }
            }
        }
        lastLogTime = System.currentTimeMillis();
        return false;
    }

    public static void startGroupLogging() {
        logQueues.clear();
        insideGroupLoggingScope = true;
        hasJustExitedGroupLoggingScope = false;
    }

    public static void endGroupLogging() {
        insideGroupLoggingScope = false;
        hasJustExitedGroupLoggingScope = true;
    }

    public static boolean isInsideGroupLoggingScope() {
        return insideGroupLoggingScope;
    }

    public static void queueLog(String message, Boolean shouldRateLimit = null) {
        if (isProduction) return;
        if (Boolean.FALSE.equals(shouldRateLimit)) {
            System.out.println(message);
        } else if (insideGroupLoggingScope) {
            logQueues.add(new Pair<>(message, shouldRateLimit));
        } else {
            if (shouldReturnDueToRateLimit(shouldRateLimit)) {
                return;
            }
//            System.out.println("Rate limited. Queue size: " + logQueues.size());
            if (hasJustExitedGroupLoggingScope) {
                for (Pair<String, Boolean> pair : logQueues) {
                    System.out.println(pair.getFirst());
                }
                hasJustExitedGroupLoggingScope = false;
            } else {
                System.out.println(message);
            }
//            System.out.println("==========================");
            logQueues.clear();
        }
    }

    public static void log(String message, boolean forceTrace = false, Boolean shouldRateLimit = null) {
        queueLog(new AnsiConstructor()
                .setPredefinedTextColor(AnsiConstructor.ConsoleColors.COLOR_DEFAULT_PINK)
                .bold()
                .text("[LOG]: ")
                .unbold()
                .text(message)
                .text(forceTrace || shouldTraceLog ? getStackTrace() : "")
                .getAnsi(), shouldRateLimit);
    }

    public static void error(String message, boolean forceTrace = false, Boolean shouldRateLimit = null) {
        queueLog(new AnsiConstructor()
                .setPredefinedTextColor(AnsiConstructor.ConsoleColors.COLOR_DEFAULT_RED)
                .bold()
                .text("[ERROR]: ")
                .unbold()
                .text(message)
                .text(forceTrace || shouldTraceError ? getStackTrace() : "")
                .getAnsi(), shouldRateLimit);
//        System.out.println(ANSI_RED + "\u001B[1m" + "[ERROR]: " + "\u001B[22m" + message + (shouldTraceError ? getStackTrace() : "") + ANSI_RESET);
    }

    public static void warn(String message, boolean forceTrace = false, Boolean shouldRateLimit = null) {
        queueLog(new AnsiConstructor()
                .setPredefinedTextColor(AnsiConstructor.ConsoleColors.COLOR_DEFAULT_YELLOW)
                .bold()
                .text("[WARNING]: ")
                .unbold()
                .text(message)
                .text(forceTrace || shouldTraceWarn ? getStackTrace() : "")
                .getAnsi(), shouldRateLimit);
//        System.out.println(ANSI_YELLOW + "\u001B[1m" + "[WARNING]: " + "\u001B[22m" + message + (shouldTraceWarn ? getStackTrace() : "") + ANSI_RESET);
    }

    public static void info(String message, boolean forceTrace = false, Boolean shouldRateLimit = null) {
        queueLog(new AnsiConstructor()
                .setPredefinedTextColor(AnsiConstructor.ConsoleColors.COLOR_DEFAULT_BLUE)
                .bold()
                .text("[INFO]: ")
                .unbold()
                .text(message)
                .text(forceTrace || shouldTraceInfo ? getStackTrace() : "")
                .getAnsi(), shouldRateLimit);
//        System.out.println(ANSI_BLUE + "\u001B[1m" + "[INFO]: " + "\u001B[22m" + message + (shouldTraceInfo ? getStackTrace() : "") + ANSI_RESET);
    }

    public static void check(String message, boolean forceTrace = false, Boolean shouldRateLimit = null) {
        queueLog(new AnsiConstructor()
                .setPredefinedTextColor(AnsiConstructor.ConsoleColors.COLOR_DEFAULT_PURPLE)
                .bold()
                .text("[CHECK]: ")
                .unbold()
                .text(message)
                .text(forceTrace || shouldTraceCheck ? getStackTrace() : "")
                .getAnsi(), shouldRateLimit);
//        System.out.println(ANSI_PURPLE + "\u001B[1m" + "[CHECK]: " + "\u001B[22m" + message + (shouldTraceCheck ? getStackTrace() : "") + ANSI_RESET);
    }

    public static void trace(String message, Boolean shouldRateLimit = null) {
        queueLog(new AnsiConstructor()
                .setPredefinedTextColor(AnsiConstructor.ConsoleColors.COLOR_DEFAULT_CYAN)
                .bold()
                .text("[TRACE]: ")
                .unbold()
                .text(message)
                .text(getStackTrace())
                .getAnsi(), shouldRateLimit);
//        System.out.println(ANSI_CYAN + "\u001B[1m" + "[TRACE]: " + "\u001B[22m" + message + getStackTrace() + ANSI_RESET);
    }

    public static void success(String message, boolean forceTrace = false, Boolean shouldRateLimit = null) {
        queueLog(new AnsiConstructor()
                .setPredefinedTextColor(AnsiConstructor.ConsoleColors.COLOR_DEFAULT_GREEN)
                .bold()
                .text("[SUCCESS]: ")
                .unbold()
                .text(message)
                .text(forceTrace || shouldTraceSuccess ? getStackTrace() : "")
                .getAnsi(), shouldRateLimit);
//        System.out.println(ANSI_GREEN + "\u001B[1m" + "[SUCCESS]: " + "\u001B[22m" + message + (shouldTraceSuccess ? getStackTrace() : "") + ANSI_RESET);
    }

    private static String getStackTrace() {
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        StringBuilder message = new StringBuilder();
        for (int i = 4, stackTraceLength = stackTrace.length; i < stackTraceLength; i++) {
            StackTraceElement stackTraceElement = stackTrace[i];
            message.append("\n\tAt ").append(stackTraceElement.getClassName()).append(".").append(stackTraceElement.getMethodName()).append("(").append(stackTraceElement.getFileName()).append(":").append(stackTraceElement.getLineNumber()).append(")");
        }
        return message.toString();
    }

//    public static void registerCustomLog(String functionIdentifier, Function<List<Object>, String> function, String startAdornment="", String endAdornment="", Color defaultTextColor = null) {
//        if (customLoggingFunctions.containsKey(functionIdentifier)) {
//            warn("Custom logging function with identifier " + functionIdentifier + " already exists! This operation will now override the already existing function.");
//        }
//        customLoggingFunctions.put(functionIdentifier, new LoggingFunctionContainer(function, startAdornment, endAdornment, defaultTextColor));
//    }
//
//    public static void customLog(String functionIdentifier, List<Object> args, Boolean shouldRateLimit = null) {
//        if (customLoggingFunctions.containsKey(functionIdentifier)) {
//            LoggingFunctionContainer functionContainer = customLoggingFunctions.get(functionIdentifier);
//            String startAdornment = functionContainer.startAdornment() == null || functionContainer.startAdornment().isBlank() ?
//                    "" : "\u001B[1m[" + functionContainer.startAdornment() + "]: \u001B[22m ";
//            Color textColor = functionContainer.defaultTextColor();
//            String message = new AnsiConstructor()
//                    .setTextColor(textColor)
//                    .text(startAdornment)
//                    .text(functionContainer.function().apply(args))
//                    .text(functionContainer.endAdornment())
//                    .getAnsi();
//            queueLog(message, shouldRateLimit);
//        } else {
//            warn("Custom logging function \"" + functionIdentifier + "\" does not exist!");
//        }
//    }

    public static void refresh() {
        Logger.log("");
    }

    public static boolean isRateLimitingEnabled() {
        return Logger.rateLimitingEnabled;
    }

    public static void setRateLimitingEnabled(boolean rateLimitingEnabled) {
        Logger.rateLimitingEnabled = rateLimitingEnabled;
    }

    public static float getRateLimitMilliSeconds() {
        return rateLimitMilliSeconds;
    }

    public static void setRateLimitMilliSeconds(float rateLimitMilliSeconds) {
        Logger.rateLimitMilliSeconds = rateLimitMilliSeconds;
    }

//    public record LoggingFunctionContainer(
//            Function<List<Object>, String> function,
//            String startAdornment,
//            String endAdornment,
//            Color defaultTextColor
//    ){}
}

