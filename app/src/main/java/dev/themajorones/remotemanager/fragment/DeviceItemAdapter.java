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
import dev.themajorones.remotemanager.R;
import dev.themajorones.remotemanager.entity.Device;
import dev.themajorones.remotemanager.utils.ViewUtils;

import java.util.List;

public class DeviceItemAdapter extends ArrayAdapter<Device> {

    public DeviceItemAdapter(@NonNull Context context, @NonNull List<Device> devices) {
        super(context, 0, devices);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.device_item, parent, false);
        }

        Device device = getItem(position);

        if (device != null) {
            ImageView logoImageView = convertView.findViewById(R.id.logoImageView);
            TextView deviceInfoTextView = convertView.findViewById(R.id.deviceInfoTextView);
            Button actionButton = convertView.findViewById(R.id.actionButton);
            Button editButton = convertView.findViewById(R.id.editButton);

            deviceInfoTextView.setText(device.getName());
            actionButton.setOnClickListener(v -> ViewUtils.Notify(getContext(), "Action for " + device.getName()));
            editButton.setOnClickListener(v -> ViewUtils.Notify(getContext(), "Edit for " + device.getName()));
        }

        return convertView;
    }
}