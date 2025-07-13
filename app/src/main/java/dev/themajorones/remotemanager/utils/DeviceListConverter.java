package dev.themajorones.remotemanager.utils;

import androidx.room.TypeConverter;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;

import dev.themajorones.remotemanager.entity.Device;

public class DeviceListConverter {
    private static final Gson gson = new Gson();

    @TypeConverter
    public static String fromDeviceList(List<Device> devices) {
        return devices == null ? null : gson.toJson(devices);
    }

    @TypeConverter
    public static List<Device> toDeviceList(String data) {
        if (data == null) return Collections.emptyList();
        Type listType = new TypeToken<List<Device>>() {}.getType();
        return gson.fromJson(data, listType);
    }
}
