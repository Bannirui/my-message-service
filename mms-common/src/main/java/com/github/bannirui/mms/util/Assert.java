package com.github.bannirui.mms.util;

public class Assert {

    public static void that(boolean test, String message) {
        if (!test) {
            throw new IllegalArgumentException(message);
        }
    }
}

