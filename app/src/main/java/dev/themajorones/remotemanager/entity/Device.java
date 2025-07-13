package dev.themajorones.remotemanager.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import dev.themajorones.remotemanager.utils.DeviceListConverter;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import androidx.annotation.NonNull;
import androidx.room.TypeConverters;

import java.util.ArrayList;
import java.util.List;

import lombok.Setter;

@Entity(tableName = "Device")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Device {

    @PrimaryKey
    @NonNull
    public String name;

    public String os;

    @TypeConverters(DeviceListConverter.class)
    public List<Device> managingDevices;

    public String host;

    public String username;

    public String password;

    public int port;

    public String keyPath;

    public String macAddress;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Device device = (Device) o;
        return port == device.port &&
                java.util.Objects.equals(name, device.name) &&
                java.util.Objects.equals(os, device.os) &&
                java.util.Objects.equals(host, device.host) &&
                java.util.Objects.equals(username, device.username) &&
                java.util.Objects.equals(password, device.password) &&
                java.util.Objects.equals(keyPath, device.keyPath) &&
                java.util.Objects.equals(macAddress, device.macAddress) &&
                java.util.Objects.equals(managingDevices, device.managingDevices);
    }

    public boolean isSshAble() {
        if (host == null || host.isEmpty()) return false;
        if (username == null || username.isEmpty()) return false;
        if (password == null || password.isEmpty()) return false;
        return port > 0;
    }

    public boolean addManagingDevice(Device device) {
        if (managingDevices == null) {
            managingDevices = new ArrayList<>();
        }

        if (!managingDevices.contains(device)) {
            managingDevices.add(device);
            return true;
        }

        return false;
    }

    public boolean removesManagingDevice(Device device) {
        if (managingDevices != null && managingDevices.contains(device)) {
            managingDevices.remove(device);
            return true;
        }
        return false;
    }
}