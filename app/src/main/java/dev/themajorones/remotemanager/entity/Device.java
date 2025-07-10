package dev.themajorones.remotemanager.entity;

import androidx.room.Entity;

import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

@Entity(tableName = "Device")
@Getter
@Setter
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
public class Device {
    private List<OS> osList;

    private List<Device> managedDevices = new ArrayList<>();

    private String host;

    private String username;

    private String password;

    private String keyPath;

    private String macAddress;

    public Device addManagedDevice(Device device) {
        if (device != null && !isManaged(device)) {
            managedDevices.add(device);
        }
        return this;
    }

    public boolean isManaged(Device device) {
        for (Device managedDevice : managedDevices) {
            if (managedDevice.getHost().equals(device.getHost())) {
                return true;
            }
        }
        return false;
    }

}