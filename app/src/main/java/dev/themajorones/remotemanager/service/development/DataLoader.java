package dev.themajorones.remotemanager.service.development;

import java.util.ArrayList;
import java.util.List;
import dev.themajorones.remotemanager.entity.Device;
import dev.themajorones.remotemanager.service.PersistentStorageService;
import dev.themajorones.remotemanager.service.SSHService;
import dev.themajorones.remotemanager.utils.ViewUtils;

public class DataLoader {
    private static final SSHService sshService = SSHService.get();

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

        bigscreen.addManagingDevice(asusRouter);
        windstation.addManagingDevice(asusRouter);
        macVM.addManagingDevice(asusRouter);

        gamingPC.addManagingDevice(ddManager);
        macbook.addManagingDevice(ddManager);

        devices.add(bigscreen);
        devices.add(windstation);
        devices.add(macVM);
        devices.add(asusRouter);
        devices.add(gamingPC);
        devices.add(macbook);
        devices.add(ddManager);

        ViewUtils.notify("DEBUG: Deleted all devices and added dummy devices");

        PersistentStorageService.get().saveAll(devices);
        return true;
    }
}