package dev.themajorones.remotemanager.utils;

import android.content.pm.ActivityInfo;
import android.content.res.Resources;
import androidx.appcompat.app.AppCompatActivity;

public class Preload {

    public static void load(AppCompatActivity activity) {
        preventRotation(activity);
        enableSSHJDebugLogging();
    }

    private static void preventRotation(AppCompatActivity activity) {
        Resources res = activity.getResources();
        boolean isTablet = DeviceInfo.getDeviceType(res).equals("Tablet");
        if (!isTablet) {
            activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
    }

    private static void enableSSHJDebugLogging() {
        System.setProperty("org.slf4j.simpleLogger.defaultLogLevel", "trace");
        System.setProperty("org.slf4j.simpleLogger.log.net.schmizz.sshj", "debug");
    }
}