package dev.themajorones.remotemanager.fragment;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.List;
import dev.themajorones.remotemanager.R;

public class ManagedDeviceAdapter extends ArrayAdapter<ManagedDeviceAdapter.ManagedDevice> {
    private final LayoutInflater inflater;

    public ManagedDeviceAdapter(Context context, List<ManagedDevice> devices) {
        super(context, 0, devices);
        inflater = LayoutInflater.from(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_managed_device, parent, false);
            holder = new ViewHolder();
            holder.ivIcon = convertView.findViewById(R.id.ivManagedDeviceIcon);
            holder.tvName = convertView.findViewById(R.id.tvManagedDeviceName);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        ManagedDevice device = getItem(position);
        holder.tvName.setText(device.name);
        // Optionally set icon here if you have different icons
        return convertView;
    }

    static class ViewHolder {
        ImageView ivIcon;
        TextView tvName;
    }

    public static class ManagedDevice {
        public String name;
        public ManagedDevice(String name) {
            this.name = name;
        }
    }
}
