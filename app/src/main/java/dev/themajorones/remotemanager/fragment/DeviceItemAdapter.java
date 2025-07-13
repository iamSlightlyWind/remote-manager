package dev.themajorones.remotemanager.fragment;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.widget.ArrayAdapter;
import dev.themajorones.remotemanager.MainActivity;
import dev.themajorones.remotemanager.R;
import dev.themajorones.remotemanager.entity.Device;
import dev.themajorones.remotemanager.utils.DeviceUtils;
import java.util.List;

public class DeviceItemAdapter extends ArrayAdapter<Device> {

    private MainActivity activity;

    public DeviceItemAdapter(@NonNull Context context, @NonNull List<Device> devices) {
        super(context, 0, devices);
        activity = (MainActivity) context;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.device_item, parent, false);
        }

        Device device = getItem(position);

        if (device != null) {
            ImageView logoImageView = convertView.findViewById(R.id.logo_image_view);
            TextView deviceInfoTextView = convertView.findViewById(R.id.deviceInfoTextView);
            Button actionButton = convertView.findViewById(R.id.button1);
            Button editButton = convertView.findViewById(R.id.button2);

            String os = device.getOs().toLowerCase();
            switch (os) {
                case "linux" -> logoImageView.setImageResource(R.drawable.linux);
                case "windows" -> logoImageView.setImageResource(R.drawable.windows);
                case "macos" -> logoImageView.setImageResource(R.drawable.macos);
                default -> logoImageView.setImageResource(R.drawable.windows);
            }
            
            deviceInfoTextView.setText(device.getName());
            actionButton.setOnClickListener(v -> DeviceUtils.wakeOnLanLocally(device));
            editButton.setOnClickListener(v -> DeviceUtils.shutdownUnix(device));
        }

        convertView.setOnClickListener(v -> {
            AddDeviceFragment adf = AddDeviceFragment.getInstance();
            if (adf == null) {
                activity.spawnAddDeviceFragment();
                adf = AddDeviceFragment.getInstance();
            }
            adf.editDevice(device);
        });


        return convertView;
    }
}