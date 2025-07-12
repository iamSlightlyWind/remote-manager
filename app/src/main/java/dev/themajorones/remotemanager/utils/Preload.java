package dev.themajorones.remotemanager.utils;

import android.annotation.SuppressLint;
import android.content.pm.ActivityInfo;
import android.content.res.Resources;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.WindowCompat;
import dev.themajorones.remotemanager.service.PersistentStorageService;
import dev.themajorones.remotemanager.service.development.DataLoader;

public class Preload {

    public static void load(AppCompatActivity activity) {
        PersistentStorageService.init(activity);
        DataLoader.loadData();
        preventRotation(activity);
        enableSSHJDebugLogging();
        preventOverDraw(activity);
    }

    @SuppressLint("SourceLockedOrientationActivity")
    private static void preventRotation(AppCompatActivity activity) {
        Resources res = activity.getResources();
        boolean isTablet = DeviceUtils.getDeviceType(res).equals("Tablet");
        if (!isTablet) {
            activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
    }

    private static void enableSSHJDebugLogging() {
        System.setProperty("org.slf4j.simpleLogger.defaultLogLevel", "trace");
        System.setProperty("org.slf4j.simpleLogger.log.net.schmizz.sshj", "debug");
    }

    private static void preventOverDraw(AppCompatActivity activity){
        WindowCompat.setDecorFitsSystemWindows(activity.getWindow(), true);
    }
}