package dev.themajorones.remotemanager.entity;

import net.schmizz.sshj.SSHClient;
import net.schmizz.sshj.connection.channel.direct.Session;
import net.schmizz.sshj.transport.TransportException;
import net.schmizz.sshj.transport.verification.PromiscuousVerifier;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.security.Security;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import dev.themajorones.remotemanager.utils.ViewUtils;

public class SecureShell {

    private final ExecutorService io = Executors.newSingleThreadExecutor();
    private String devicePassword = "";

    static {
        Security.removeProvider("BC");
        Security.insertProviderAt(new BouncyCastleProvider(), 1);
    }

    static void init() {
        // intentional empty method to ensure BouncyCastle is initialized
    }

    private final SSHClient sshClient;

    public SecureShell() {
        Security.removeProvider("BC");
        Security.insertProviderAt(new BouncyCastleProvider(), 1);
        this.sshClient = new SSHClient();
        this.sshClient.addHostKeyVerifier(new PromiscuousVerifier());
    }

    public void connect(String host, int port, String user, String password, String keyPath) throws Exception {
        devicePassword = password;
        io.submit(() -> {
            sshClient.connect(host, port);
            if (keyPath != null) {
                sshClient.authPublickey(user, keyPath);
            } else {
                sshClient.authPassword(user, password);
            }
            return null;
        }).get();
    }

    public void connect(Device device) throws Exception {
        if (!device.isSshAble()) {
            ViewUtils.notify("Missing device details");
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
            // wait up to 2 seconds
            return future.get(3, TimeUnit.SECONDS);
        } catch (TimeoutException e) {
            future.cancel(true);
            ViewUtils.notify("Command timed out");
            return null;
        } catch (Exception e) {
            if (!(e instanceof TransportException)) {
                ViewUtils.throwNotify("Failed: ", e);
            }
            return null;
        }
    }


    public boolean isSessionAlive() {
        return sshClient.isConnected() && sshClient.isAuthenticated();
    }

    public void disconnect() throws IOException {
        if (sshClient.isConnected()) {
            sshClient.disconnect();
        }
    }
}