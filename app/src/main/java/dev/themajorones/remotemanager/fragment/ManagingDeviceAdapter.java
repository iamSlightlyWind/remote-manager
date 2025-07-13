package dev.themajorones.remotemanager.fragment;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import java.util.List;
import dev.themajorones.remotemanager.R;

public class ManagingDeviceAdapter extends ArrayAdapter<ManagingDeviceAdapter.ManagingDevice> {
    private final LayoutInflater inflater;

    public ManagingDeviceAdapter(Context context, List<ManagingDevice> devices) {
        super(context, 0, devices);
        inflater = LayoutInflater.from(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_managing_device, parent, false);
            holder = new ViewHolder();
            holder.tvName = convertView.findViewById(R.id.tvManagingDeviceName);
            holder.ivExpand = convertView.findViewById(R.id.ivExpandCollapse);
            holder.lvManaged = convertView.findViewById(R.id.lvManagedDevices);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        ManagingDevice device = getItem(position);
        holder.tvName.setText(device.name);
        ManagedDeviceAdapter managedAdapter = new ManagedDeviceAdapter(getContext(), device.managedDevices);
        holder.lvManaged.setAdapter(managedAdapter);
        holder.lvManaged.setVisibility(device.expanded ? View.VISIBLE : View.GONE);
        holder.ivExpand.setImageResource(device.expanded ? android.R.drawable.arrow_up_float : android.R.drawable.arrow_down_float);
        holder.ivExpand.setOnClickListener(v -> {
            device.expanded = !device.expanded;
            notifyDataSetChanged();
        });
        return convertView;
    }

    static class ViewHolder {
        TextView tvName;
        ImageView ivExpand;
        ListView lvManaged;
    }

    public static class ManagingDevice {
        public String name;
        public List<ManagedDeviceAdapter.ManagedDevice> managedDevices;
        public boolean expanded = false;
        public ManagingDevice(String name, List<ManagedDeviceAdapter.ManagedDevice> managedDevices) {
            this.name = name;
            this.managedDevices = managedDevices;
        }
    }
}
