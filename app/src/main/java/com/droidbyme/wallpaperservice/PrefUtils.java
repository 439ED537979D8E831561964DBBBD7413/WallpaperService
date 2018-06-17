package com.droidbyme.wallpaperservice;

import android.content.Context;

public class PrefUtils {

    private static final String PATH = "path";

    public static void setPath(Context ctx, String value) {
        Prefs.with(ctx).save(PATH, value);
    }

    public static String getPath(Context ctx) {
        return Prefs.with(ctx).getString(PATH, "");
    }

}
