package com.droidbyme.wallpaperservice;

import android.content.Context;
import android.support.annotation.NonNull;

import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

public class Functions {

    public static void setPermission(final Context context, @NonNull String[] permissions, PermissionListener permissionListener) {

        TedPermission.with(context)
                .setPermissionListener(permissionListener)
                .setDeniedMessage("If you reject permission,you can not use this service\n\nPlease turn on permissions at [Setting] > [Permission]")
                .setPermissions(permissions)
                .check();
    }
}
