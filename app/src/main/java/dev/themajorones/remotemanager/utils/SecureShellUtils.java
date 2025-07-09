package dev.themajorones.remotemanager.utils;

import net.schmizz.sshj.SSHClient;
import net.schmizz.sshj.connection.channel.direct.Session;
import net.schmizz.sshj.transport.verification.PromiscuousVerifier;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.security.Security;

public class SecureShellUtils {

    static {
        Security.removeProvider("BC");
        Security.insertProviderAt(new BouncyCastleProvider(), 1);
    }

    static void init() {
        // intentional empty method to ensure BouncyCastle is initialized
    }

    private final SSHClient sshClient;

    public SecureShellUtils() {
        Security.removeProvider("BC");
        Security.insertProviderAt(new BouncyCastleProvider(), 1);
        this.sshClient = new SSHClient();
        this.sshClient.addHostKeyVerifier(new PromiscuousVerifier());
    }

    public void connect(String host, int port, String user, String password, String keyPath) throws IOException {
        sshClient.connect(host, port);
        if (keyPath != null) {
            sshClient.authPublickey(user, keyPath);
        } else {
            sshClient.authPassword(user, password);
        }
    }

    public String runCommand(String command) throws IOException {
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