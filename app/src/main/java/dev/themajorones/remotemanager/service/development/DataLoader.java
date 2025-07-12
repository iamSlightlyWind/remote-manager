package dev.themajorones.remotemanager.service.development;

import java.util.ArrayList;
import java.util.List;
import dev.themajorones.remotemanager.entity.Device;
import dev.themajorones.remotemanager.service.PersistentStorageService;
import dev.themajorones.remotemanager.service.SSHService;

public class DataLoader {
    private static final SSHService sshService = SSHService.get();

    public static void loadData() {
        if(PersistentStorageService.get().count() > 0) {
            return; // Data already exists, no need to load again
        }

        List<Device> devices = new ArrayList<>();

        Device bigscreen = Device.builder()
                .name("Big Screen")
                .host("192.168.50.100")
                .username("slightlywind")
                .password("301203")
                .os("Linux")
                .keyPath(null)
                .build();

        Device windstation = Device.builder()
                .name("Windstation")
                .host("192.168.50.168")
                .username("slightlywind")
                .password("301203")
                .os("Linux")
                .keyPath(null)
                .build();

        devices.add(bigscreen);
        devices.add(windstation);

        PersistentStorageService.get().saveAll(devices);
    }
}