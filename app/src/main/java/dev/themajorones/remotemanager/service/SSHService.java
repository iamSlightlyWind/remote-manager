package dev.themajorones.remotemanager.service;

import net.schmizz.sshj.SSHClient;
import net.schmizz.sshj.connection.channel.direct.Session;
import net.schmizz.sshj.transport.verification.PromiscuousVerifier;

import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

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
        if (connections.containsKey(device)) {
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

    public boolean connectToSecondDevice(Device via, Device target) {
        try {
            SecureShell shell = getConnection(via);
            String cmd = String.format("ssh -tt %s@%s exit", target.getUsername(), target.getHost());
            String result = shell.execWithInput(cmd, target.getPassword());
            return result != null;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isSecondDeviceAlive(Device via, Device target) {
        return connectToSecondDevice(via, target);
    }

    public boolean shutdownSecondDevice(Device via, Device target) {
        try {
            if (!isSecondDeviceAlive(via, target)) {
                ViewUtils.notify("Target device is not reachable via " + via.getHost());
                return false;
            }
            SecureShell shell = getConnection(via);
            String nested = String.format(
                    "ssh -tt %s@%s \"echo %s | sudo -S shutdown -h now\"",
                    target.getUsername(), target.getHost(), target.getPassword()
            );
            String output = shell.runPtyCommand(nested);
            return output != null;
        } catch (Exception e) {
            ViewUtils.throwNotify("Shutdown failed: ", e);
            return false;
        }
    }
}