package dev.themajorones.remotemanager.service;

import dev.themajorones.remotemanager.entity.Device;

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

public class SSHService {

    private static final ExecutorService io = Executors.newSingleThreadExecutor();

    static {
        Security.removeProvider("BC");
        Security.insertProviderAt(new BouncyCastleProvider(), 1);
    }

    private static SSHClient connect(Device device) throws Exception {
        SSHClient ssh = new SSHClient();
        Future<Void> future = io.submit(() -> {
            ssh.addHostKeyVerifier(new PromiscuousVerifier());
            ssh.connect(device.getHost().trim(), device.getPort());
            ssh.authPassword(device.getUsername().trim(), device.getPassword().trim());
            return null;
        });
        future.get();
        return ssh;
    }

    public static String runCommand(Device device, String command) throws Exception {
        SSHClient ssh = connect(device);
        String preparedCommand;

        if (command.trim().startsWith("sudo")) {
            String withoutSudo = command.trim().substring("sudo".length()).trim();
            preparedCommand = String.format("echo %s | sudo -S %s", device.getPassword(), withoutSudo);
        } else {
            preparedCommand = command;
        }

        Future<String> future = io.submit(() -> {
            try (Session session = ssh.startSession()) {
                Session.Command sessionCommand = session.exec(preparedCommand);
                BufferedReader reader = new BufferedReader(new InputStreamReader(sessionCommand.getInputStream()));
                StringBuilder stringBuilder = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    stringBuilder.append(line).append("\n");
                }
                sessionCommand.join();
                return stringBuilder.toString();
            }

        });
        return future.get();
    }
}
