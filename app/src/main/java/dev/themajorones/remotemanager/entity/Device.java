package dev.themajorones.remotemanager.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import androidx.annotation.NonNull;
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

    public Long managerId;

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
                java.util.Objects.equals(macAddress, device.macAddress);
    }
}