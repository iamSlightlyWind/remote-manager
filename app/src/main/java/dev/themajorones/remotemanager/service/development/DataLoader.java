package dev.themajorones.remotemanager.service.development;

import dev.themajorones.remotemanager.entity.Device;
import dev.themajorones.remotemanager.service.SSHService;

public class DataLoader {
    private static final SSHService sshService = SSHService.get();

    public static void loadData(Device device) {
        Device bigscreen = new Device()
                .setHost("192.168.50.100")
                .setUsername("slightlywind")
                .setPassword("301203")
                .setKeyPath(null);

        Device windstation = new Device()
                .setHost("192.168.50.168")
                .setUsername("slightlywind")
                .setPassword("301203")
                .setKeyPath(null);

        sshService.getConnection(bigscreen);
        sshService.getConnection(windstation);
    }
}
