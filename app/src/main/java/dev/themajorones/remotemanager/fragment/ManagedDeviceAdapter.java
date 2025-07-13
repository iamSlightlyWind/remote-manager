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
import dev.themajorones.remotemanager.entity.Device;

public class ManagedDeviceAdapter extends ArrayAdapter<Device> {
    private final LayoutInflater inflater;

    public ManagedDeviceAdapter(Context context, List<Device> devices) {
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
        Device device = getItem(position);
        holder.tvName.setText(device.getName());
        return convertView;
    }

    static class ViewHolder {
        ImageView ivIcon;
        TextView tvName;
    }
}
