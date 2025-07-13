package dev.themajorones.remotemanager.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.res.Resources;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.WindowCompat;

import com.google.android.material.color.DynamicColors;

import dev.themajorones.remotemanager.service.PersistentStorageService;
import dev.themajorones.remotemanager.service.development.DataLoader;
import lombok.Getter;

public class Preload {

    @Getter
    @SuppressLint("StaticFieldLeak")
    private static Context context;

    public static void load(AppCompatActivity activity) {
        context = activity.getApplicationContext();

        PersistentStorageService.init(activity);
        DataLoader.loadData();
        preventRotation(activity);
        enableSSHJDebugLogging();
        preventOverDraw(activity);
        useMaterialYou(activity);
    }

    private static void useMaterialYou(AppCompatActivity activity) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
            DynamicColors.applyToActivitiesIfAvailable(activity.getApplication());
        }
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