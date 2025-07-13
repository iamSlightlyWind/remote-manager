package dev.themajorones.remotemanager.fragment;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;

import dev.themajorones.remotemanager.entity.Device;
import dev.themajorones.remotemanager.service.PersistentStorageService;

import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ListAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.util.List;
import java.util.Objects;

import dev.themajorones.remotemanager.R;
import dev.themajorones.remotemanager.utils.ViewUtils;

public class ManagingDeviceAdapter extends ArrayAdapter<Device> {
    private final LayoutInflater inflater;

    public ManagingDeviceAdapter(Context context, List<Device> devices) {
        super(context, 0, devices);
        inflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_managing_device, parent, false);
            holder = new ViewHolder();
            holder.manageingDevice = convertView.findViewById(R.id.managing_device_name);
            holder.expander = convertView.findViewById(R.id.expand_collapse);
            holder.addManagedDevice = convertView.findViewById(R.id.add_managed_device);
            holder.lvManaged = convertView.findViewById(R.id.lvManagedDevices);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Device device = getItem(position);
        holder.manageingDevice.setText(Objects.requireNonNull(device).getName());

        List<Device> managedDevices = PersistentStorageService.findManagedDevices(device);
        ManagedDeviceAdapter managedAdapter = new ManagedDeviceAdapter(getContext(), device, managedDevices);
        holder.lvManaged.setAdapter(managedAdapter);
        setListViewHeightBasedOnChildren(holder.lvManaged);

        boolean expanded = PersistentStorageService.isManagingDevice(device);
        holder.lvManaged.setVisibility(expanded ? View.VISIBLE : View.GONE);
        holder.expander.setImageResource(expanded
                ? android.R.drawable.arrow_up_float
                : android.R.drawable.arrow_down_float);

        View.OnClickListener toggleListener = v -> {
            if (holder.lvManaged.getVisibility() == View.VISIBLE) {
                holder.lvManaged.setVisibility(View.GONE);
                holder.expander.setImageResource(android.R.drawable.arrow_down_float);
            } else {
                holder.lvManaged.setVisibility(View.VISIBLE);
                holder.expander.setImageResource(android.R.drawable.arrow_up_float);
            }
        };

        holder.expander.setOnClickListener(toggleListener);
        holder.manageingDevice.setOnClickListener(toggleListener);
        holder.addManagedDevice.setOnClickListener(v -> ViewUtils.notify("Add Managed Device pressed for " + device.getName()));

        return convertView;
    }

    static class ViewHolder {
        TextView manageingDevice;
        ImageView expander;
        ListView lvManaged;
        Button addManagedDevice;
    }

    private static void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter adapter = listView.getAdapter();
        if (adapter == null) return;
        int totalHeight = 0;
        for (int i = 0; i < adapter.getCount(); i++) {
            View listItem = adapter.getView(i, null, listView);
            listItem.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
            totalHeight += listItem.getMeasuredHeight();
        }
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (adapter.getCount() - 1));
        listView.setLayoutParams(params);
    }
}