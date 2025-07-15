package dev.themajorones.remotemanager.service;

import dev.themajorones.remotemanager.entity.Device;
import dev.themajorones.remotemanager.utils.ViewUtils;

import net.schmizz.sshj.SSHClient;
import net.schmizz.sshj.connection.channel.direct.Session;
import net.schmizz.sshj.transport.verification.PromiscuousVerifier;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.security.Security;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;

import net.schmizz.sshj.connection.channel.direct.LocalPortForwarder;
import net.schmizz.sshj.connection.channel.direct.Parameters;

public class SSHService {

    private static final ExecutorService io = Executors.newSingleThreadExecutor();

    static {
        Security.removeProvider("BC");
        Security.insertProviderAt(new BouncyCastleProvider(), 1);
        java.util.logging.Logger.getLogger("net.schmizz.sshj").setLevel(Level.FINEST);
    }

    private static SSHClient connect(Device device) throws Exception {
        SSHClient ssh = new SSHClient();
        Future<Void> future = io.submit(() -> {
            ssh.addHostKeyVerifier(new PromiscuousVerifier());
            ssh.connect(device.getHost().trim(), device.getPort());
            ssh.authPassword(device.getUsername().trim(), device.getPassword().trim());
            return null;
        });
        try {
            future.get(5, TimeUnit.SECONDS);
            return ssh;
        } catch (TimeoutException e) {
            ssh.disconnect();
            ViewUtils.notify("Failed: Timed out after 5 seconds");
            return null;
        }
    }

    public static String runCommand(Device device, String command) throws Exception {
        SSHClient ssh = connect(device);
        if (ssh == null) {
            return "";
        }

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
            } finally {
                ssh.disconnect();
            }
        });

        try {
            return future.get(5, TimeUnit.SECONDS);
        } catch (TimeoutException e) {
            ssh.disconnect();
            ViewUtils.notify("Failed: Timed out after 5 seconds");
            return "";
        }
    }

    public static String runNestedCommand(Device managingDevice, Device managedDevice, String command) throws Exception {
        SSHClient routerSsh = connect(managingDevice);
        if (routerSsh == null) {
            return "";
        }

        String prepared;
        if (command.trim().startsWith("sudo")) {
            String withoutSudo = command.trim().substring("sudo".length()).trim();
            prepared = String.format("echo %s | sudo -S %s", managedDevice.getPassword(), withoutSudo);
        } else {
            prepared = command;
        }

        Future<String> future = io.submit(() -> {
            try {
                Parameters params = new Parameters("127.0.0.1", 0, managedDevice.getHost().trim(), managedDevice.getPort());
                ServerSocket serverSocket = new ServerSocket(params.getLocalPort(), 0, InetAddress.getByName(params.getLocalHost()));

                LocalPortForwarder forwarder = routerSsh.newLocalPortForwarder(params, serverSocket);

                Thread listener = new Thread(() -> {
                    try {
                        forwarder.listen();
                    } catch (Exception e) {

                    }
                }, "port-forward-listener");
                listener.setDaemon(true);
                listener.start();

                int localPort = serverSocket.getLocalPort();

                SSHClient pcSsh = new SSHClient();
                pcSsh.addHostKeyVerifier(new PromiscuousVerifier());
                pcSsh.connect("127.0.0.1", localPort);
                pcSsh.authPassword(managedDevice.getUsername().trim(), managedDevice.getPassword().trim());

                try (Session session = pcSsh.startSession()) {
                    Session.Command cmd = session.exec(prepared);
                    BufferedReader reader = new BufferedReader(new InputStreamReader(cmd.getInputStream()));
                    StringBuilder sb = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        sb.append(line).append("\n");
                    }
                    cmd.join();
                    return sb.toString();
                } finally {
                    pcSsh.disconnect();
                    forwarder.close();
                    routerSsh.disconnect();
                    serverSocket.close();
                }
            } catch (Exception e) {
                ViewUtils.throwNotify("Nested feature failed: ", e);
                return "";
            }
        });

        try {
            return future.get(10, TimeUnit.SECONDS);
        } catch (Exception e) {
            if (!(e instanceof ExecutionException)) {
                if (e instanceof TimeoutException) {
                    ViewUtils.notify("Failed: Nested command timed out after 10 seconds");
                } else {
                    ViewUtils.throwNotify("Failed: ", e);
                }
            }
            return "";
        }
    }

    public static boolean commandExists(Device device, String command) throws Exception{
        SSHClient ssh = connect(device);
        if (ssh == null) {
            return false;
        }

        String baseCommand = command.trim().split("\\s+")[0];
        String whichCommand = "which " + baseCommand;

        Future<Boolean> future = io.submit(() -> {
            try (Session session = ssh.startSession()) {
                Session.Command sessionCommand = session.exec(whichCommand);
                sessionCommand.join();
                return sessionCommand.getExitStatus() == 0;
            } finally {
                ssh.disconnect();
            }
        });

        try {
            return future.get(5, TimeUnit.SECONDS);
        } catch (Exception e) {
            if (e instanceof TimeoutException) {
                ViewUtils.notify("Failed: Timed out after 5 seconds");
            } else {
                ViewUtils.throwNotify("Failed: ", e);
            }
            return false;
        }
    }
}
