package com.taf.videomerge.common;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;

public class PermissionUtils {
    public static boolean hasPermission(Activity activity, String[] permissions){
        for (String permission:permissions){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (activity.checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED){
                    return false;
                }
            }
        }
        return true;
    }

    public static boolean checkGranted(int[] grantResults) {
        for (int grantResult:grantResults){
            if (grantResult!= PackageManager.PERMISSION_GRANTED){
                return false;
            }
        }
        return true;
    }
}
