package dev.themajorones.remotemanager.entity;

import net.schmizz.sshj.SSHClient;
import net.schmizz.sshj.connection.channel.direct.Session;
import net.schmizz.sshj.transport.verification.PromiscuousVerifier;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.security.Security;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import dev.themajorones.remotemanager.utils.ViewUtils;

public class SecureShell {
    private final ExecutorService io = Executors.newSingleThreadExecutor();
    private String devicePassword = "";
    private final SSHClient sshClient;

    static {
        Security.removeProvider("BC");
        Security.insertProviderAt(new BouncyCastleProvider(), 1);
    }

    public SecureShell() {
        this.sshClient = new SSHClient();
        this.sshClient.addHostKeyVerifier(new PromiscuousVerifier());
    }

    public void connect(String host, int port, String user, String password, String keyPath) throws Exception {
        devicePassword = password;
        io.submit(() -> {
            sshClient.connect(host, port);
            sshClient.authPassword(user, password);
            return null;
        }).get();
    }

    public void connect(Device device) throws Exception {
        if (!device.isSshAble()) {
            ViewUtils.notify("Missing device details");
            return;
        }

        connect(device.getHost(), 22, device.getUsername(), device.getPassword(), device.getKeyPath());
    }

    public String runCommand(String command) {
        Future<String> future = io.submit(() -> {
            String cmdToExec = command;
            if (command.trim().startsWith("sudo")) {
                String withoutSudo = command.trim().substring("sudo".length()).trim();
                cmdToExec = String.format("echo %s | sudo -S %s", devicePassword, withoutSudo);
            }
            try (Session session = sshClient.startSession()) {
                Session.Command cmd = session.exec(cmdToExec);
                BufferedReader reader = new BufferedReader(new InputStreamReader(cmd.getInputStream()));
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    sb.append(line).append("\n");
                }
                cmd.join();
                return sb.toString();
            }
        });

        try {
            return future.get(3, TimeUnit.SECONDS);
        } catch (Exception e) {
            future.cancel(true);
            ViewUtils.notify("Command timed out or failed");
            return null;
        }
    }

    public boolean isSessionAlive() {
        return sshClient.isConnected() && sshClient.isAuthenticated();
    }
}