package dev.themajorones.remotemanager.service.development;

import java.util.ArrayList;
import java.util.List;

import dev.themajorones.remotemanager.entity.Device;
import dev.themajorones.remotemanager.service.PersistentStorageService;
import dev.themajorones.remotemanager.service.SSHService;
import dev.themajorones.remotemanager.utils.ViewUtils;

public class DataLoader {

    public static boolean loadData() {
        PersistentStorageService.get().deleteAll();
        List<Device> devices = new ArrayList<>();
        Device bigscreen = Device.builder()
                .name("Bigscreen")
                .host("192.168.50.100")
                .username("slightlywind")
                .port(22)
                .os("Linux")
                .build();

        Device windstation = Device.builder()
                .name("Windstation")
                .host("192.168.50.168")
                .username("slightlywind")
                .port(22)
                .os("Linux")
                .build();

        Device macVM = Device.builder()
                .name("Mac VM")
                .host("192.168.50.252")
                .username("slightlywind")
                .port(22)
                .os("macOS")
                .build();

        Device asusRouter = Device.builder()
                .name("ASUS Router")
                .host("192.168.50.1")
                .username("slightlywind")
                .port(22)
                .os("Linux")
                .build();

        Device gamingPC = Device.builder()
                .name("Gaming PC [DD]")
                .os("Windows")
                .build();

        Device macbook = Device.builder()
                .name("MacBook Pro [DD]")
                .os("macOS")
                .build();

        Device ddManager = Device.builder()
                .name("DD Manager [DD]")
                .os("macOS")
                .build();

        devices.add(bigscreen);
        devices.add(windstation);
        devices.add(macVM);
        devices.add(asusRouter);
        devices.add(gamingPC);
        devices.add(macbook);
        devices.add(ddManager);

        PersistentStorageService.get().saveAll(devices);

        List<Device> savedDevices = PersistentStorageService.get().findAll();
        Device savedBigscreen = findDeviceByName(savedDevices, "Bigscreen");
        Device savedWindstation = findDeviceByName(savedDevices, "Windstation");
        Device savedMacVM = findDeviceByName(savedDevices, "Mac VM");
        Device savedAsusRouter = findDeviceByName(savedDevices, "ASUS Router");
        Device savedGamingPC = findDeviceByName(savedDevices, "Gaming PC [DD]");
        Device savedMacbook = findDeviceByName(savedDevices, "MacBook Pro [DD]");
        Device savedDdManager = findDeviceByName(savedDevices, "DD Manager [DD]");
        
        savedBigscreen.addManagingDevice(savedAsusRouter);
        savedWindstation.addManagingDevice(savedAsusRouter);

        savedMacVM.addManagingDevice(savedAsusRouter);
        savedMacVM.addManagingDevice(savedBigscreen);

        savedGamingPC.addManagingDevice(savedDdManager);
        savedMacbook.addManagingDevice(savedDdManager);

        ViewUtils.notify("DEBUG: Deleted all devices and added dummy devices");

        List<Device> devicesWithRelationships = new ArrayList<>();
        devicesWithRelationships.add(savedBigscreen);
        devicesWithRelationships.add(savedWindstation);
        devicesWithRelationships.add(savedMacVM);
        devicesWithRelationships.add(savedAsusRouter);
        devicesWithRelationships.add(savedGamingPC);
        devicesWithRelationships.add(savedMacbook);
        devicesWithRelationships.add(savedDdManager);
        
        PersistentStorageService.get().saveAll(devicesWithRelationships);
        return true;
    }
    
    private static Device findDeviceByName(List<Device> devices, String name) {
        for (Device device : devices) {
            if (device.getName().equals(name)) {
                return device;
            }
        }
        return null;
    }
}