package dev.themajorones.remotemanager.service;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import dev.themajorones.remotemanager.entity.Device;
import dev.themajorones.remotemanager.entity.SecureShell;
import dev.themajorones.remotemanager.utils.ViewUtils;

public class SSHService {
    private static final SSHService instance = new SSHService();
    private final Map<Device, SecureShell> connections = new HashMap<>();

    public static SSHService get() {
        return instance;
    }

    public SecureShell getConnection(Device device) throws Exception {
        SecureShell shell;
        if (connectionExists(device)) {
            shell = connections.get(device);
            if (!Objects.requireNonNull(shell).isSessionAlive()) {
                shell.connect(device);
            }
        } else {
            shell = new SecureShell();
            shell.connect(device);
            connections.put(device, shell);
        }
        return shell;
    }

    public SecureShell getStandaloneConnection(Device device) {
        try {
            SecureShell shell = new SecureShell();
            shell.connect(device);
            return shell;
        } catch (Exception e) {
            ViewUtils.throwNotify("Failed: ", e);
            return null;
        }
    }

    public Device sshFillInfo(String host, String username, String password) { // ssh into the device to get info (os,
        return null;
    }

    private boolean connectionExists(Device device) {
        return connections.containsKey(device);
    }
}