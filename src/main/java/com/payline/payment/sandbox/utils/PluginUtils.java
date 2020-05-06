package com.payline.payment.sandbox.utils;

public class PluginUtils {

    /* Static utility class : no need to instantiate it (Sonar bug fix) */
    private PluginUtils(){}

    /**
     * Truncate the given string with the given length, if necessary.
     *
     * @param value The string to truncate
     * @param length The maximum allowed length
     * @return The truncated string
     */
    public static String truncate(String value, int length) {
        if (value != null && value.length() > length) {
            value = value.substring(0, length);
        }
        return value;
    }

}