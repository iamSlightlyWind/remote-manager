package dev.themajorones.remotemanager.entity;

import net.schmizz.sshj.SSHClient;
import net.schmizz.sshj.connection.channel.direct.Session;
import net.schmizz.sshj.transport.verification.PromiscuousVerifier;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.security.Security;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SecureShell {

    private final ExecutorService io = Executors.newSingleThreadExecutor();

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

    public void connect(String host, int port, String user, String password, String keyPath) throws IOException {
        try {
            io.submit(() -> {
                sshClient.connect(host, port);
                if (keyPath != null) {
                    sshClient.authPublickey(user, keyPath);
                } else {
                    sshClient.authPassword(user, password);
                }
                return null;
            }).get();
        } catch (Exception e) {
            throw new IOException("Failed to connect to SSH server", e);
        }
    }

    public void connect(Device device) throws IOException {
        connect(device.getHost(), 22, device.getUsername(), device.getPassword(), device.getKeyPath());
    }

    public String runCommand(String command) {
        try {
            return io.submit(() -> {
                try (Session session = sshClient.startSession()) {
                    Session.Command cmd = session.exec(command);
                    BufferedReader reader = new BufferedReader(new InputStreamReader(cmd.getInputStream()));
                    StringBuilder sb = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        sb.append(line).append("\n");
                    }
                    cmd.join();
                    return sb.toString();
                }
            }).get();
        } catch (Exception e) {
            e.printStackTrace();
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