package dev.themajorones.remotemanager.service;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import dev.themajorones.remotemanager.entity.Device;
import dev.themajorones.remotemanager.entity.SecureShell;

public class SSHService {
    private static final SSHService instance = new SSHService();
    private final Map<Device, SecureShell> connections = new HashMap<>();

    public static SSHService get() {
        return instance;
    }

    public SecureShell getConnection(Device device) {
        try {
            if (connectionExists(device)) {
                SecureShell shell = connections.get(device);
                if (shell.isSessionAlive()) {
                    return shell;
                } else {
                    shell.connect(device);
                }
            } else {
                SecureShell shell = new SecureShell();
                shell.connect(device);
                connections.put(device, shell);
                return shell;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private boolean connectionExists(Device device) {
        return connections.containsKey(device);
    }
}