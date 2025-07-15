package dev.themajorones.remotemanager.fragment;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Button;

import androidx.annotation.NonNull;

import java.util.List;
import java.util.Objects;

import dev.themajorones.remotemanager.service.PersistentStorageService;
import dev.themajorones.remotemanager.utils.DeviceUtils;
import dev.themajorones.remotemanager.utils.ViewUtils;
import dev.themajorones.remotemanager.R;
import dev.themajorones.remotemanager.entity.Device;

public class ManagedDeviceAdapter extends ArrayAdapter<Device> {
    private final LayoutInflater inflater;
    private final Device parentDevice;

    public ManagedDeviceAdapter(Context context, Device parentDevice, List<Device> devices) {
        super(context, 0, devices);
        this.parentDevice = parentDevice;
        inflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_managed_device, parent, false);
            holder = new ViewHolder();
            holder.ivIcon = convertView.findViewById(R.id.ivManagedDeviceIcon);
            holder.tvName = convertView.findViewById(R.id.tvManagedDeviceName);
            holder.btnBoot = convertView.findViewById(R.id.btnBoot);
            holder.btnShutdown = convertView.findViewById(R.id.btnShutdown);
            holder.btnRemove = convertView.findViewById(R.id.btnRemove);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        Device device = getItem(position);
        holder.tvName.setText(Objects.requireNonNull(device).getName());
        String childName = device.getName();
        String parentName = parentDevice.getName();
        holder.btnBoot.setOnClickListener(v -> ViewUtils.notify("Boot pressed for " + childName + " managed by " + parentName));
        holder.btnShutdown.setOnClickListener(v -> DeviceUtils.shutdownManagedDevice(
                PersistentStorageService.get().findByName(parentName),
                PersistentStorageService.get().findByName(childName)));
        holder.btnRemove.setOnClickListener(v -> {
            device.removesManagingDevice(parentDevice);
            PersistentStorageService.get().save(device);
        });
        return convertView;
    }

    static class ViewHolder {
        ImageView ivIcon;
        TextView tvName;
        Button btnBoot;
        Button btnShutdown;
        Button btnRemove;
    }
}
