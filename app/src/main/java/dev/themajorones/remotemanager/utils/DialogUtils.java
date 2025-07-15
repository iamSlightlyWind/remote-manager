package dev.themajorones.remotemanager.utils;

import android.app.AlertDialog;
import android.content.Context;

import androidx.annotation.NonNull;

import java.util.List;

import dev.themajorones.remotemanager.entity.Device;

public class DialogUtils {
    public interface OnDeviceSelectedListener {
        void onDeviceSelected(Device device);
    }

    public static void showDeviceChoiceDialog(
            @NonNull Context context,
            @NonNull List<Device> devices,
            @NonNull OnDeviceSelectedListener listener
    ) {
        CharSequence[] names = new CharSequence[devices.size()];
        for (int i = 0; i < devices.size(); i++) {
            names[i] = devices.get(i).getName();
        }

        new AlertDialog.Builder(context)
                .setTitle("Select a device")
                .setItems(names, (dialog, which) -> {
                    listener.onDeviceSelected(devices.get(which));
                })
                .show();
    }
}
