package dev.themajorones.remotemanager.utils;

import android.content.res.Resources;

import androidx.window.layout.FoldingFeature;

import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import dev.themajorones.remotemanager.entity.Device;
import dev.themajorones.remotemanager.service.SSHService;

public class DeviceUtils {

    public static String getDeviceType(Resources resources) {
        int sw = resources.getConfiguration().smallestScreenWidthDp;
        return sw >= 600 ? "Tablet" : "Phone";
    }

    public static String getFoldingState(FoldingFeature foldingFeature) {
        if (foldingFeature == null) {
            return "Not Foldable";
        }
        if (isFoldedHalfway(foldingFeature)) {
            return "Folded Halfway";
        } else if (isFolded(foldingFeature)) {
            return "Folded";
        } else {
            return "Not Folded";
        }
    }

    private static String runCommandOnDevice(Device device, String command) {
        try {
            return SSHService.runCommand(device, command.trim());
        } catch (Exception e) {
            if (!(e instanceof ExecutionException)) {
                ViewUtils.throwNotify("Failed: ", e);
            }
            return "";
        }
    }

    public static void shutdownUnix(Device device) {
        String command = "sudo shutdown -h now";
        runCommandOnDevice(device, command);
    }

    public static String getOSName(Device device) {
        String command = "uname -s";
        String uname = runCommandOnDevice(device, command);

        return switch (uname.toLowerCase().trim()) {
            case "linux" -> "Linux";
            case "darwin" -> "macOS";
            case "windows" -> "Windows";
            default -> "Unknown OS";
        };
    }

    public static String getMacAddress(Device device) {
        String os = device.getOs().isEmpty() ? getOSName(device) : device.getOs();

        if (os.equals("Windows") || os.equals("Unknown OS")) {
            return "Unsupported OS";
        }

        String command = switch (os) {
            case "Linux" -> "ip a";
            case "macOS" -> "ifconfig -a";
            default -> "";
        };

        String output = runCommandOnDevice(device, command);

        return switch (os) {
            case "Linux" -> getMacFromLinuxIPA(output, device.getHost());
            case "macOS" -> getMacFromMacIfconfig(output, device.getHost());
            default -> "";
        };
    }

    public static String getMacFromLinuxIPA(String ipaOutput, String interfaceAddress) {
        String[] lines = ipaOutput.split("\\R");
        String currentMac = null;
        Pattern macPattern = Pattern.compile("\\s+link/ether\\s+([0-9a-f:]+)\\s+");
        Pattern inetPattern = Pattern.compile("\\s+inet\\s+([0-9.]+)/\\d+");

        for (String line : lines) {
            Matcher macMatcher = macPattern.matcher(line);
            if (macMatcher.find()) {
                currentMac = macMatcher.group(1);
            }

            Matcher inetMatcher = inetPattern.matcher(line);
            if (inetMatcher.find()) {
                String ip = inetMatcher.group(1);
                if (Objects.requireNonNull(ip).equals(interfaceAddress)) {
                    return currentMac;
                }
            }
        }
        return "Can't determine MAC address";
    }

    public static String getMacFromMacIfconfig(String ifconfigOutput, String interfaceAddress) {
        String[] lines = ifconfigOutput.split("\\R");
        String currentMac = null;
        Pattern macPattern = Pattern.compile("\\s+ether\\s+([0-9a-f:]+)");
        Pattern inetPattern = Pattern.compile("\\s+inet\\s+([0-9.]+)\\s");

        for (String line : lines) {
            if (line.matches("^\\S+: flags=.*")) {
                currentMac = null;
            }
            Matcher macMatcher = macPattern.matcher(line);
            if (macMatcher.find()) {
                currentMac = macMatcher.group(1);
            }
            Matcher inetMatcher = inetPattern.matcher(line);
            if (inetMatcher.find()) {
                String ip = inetMatcher.group(1);
                if (Objects.requireNonNull(ip).equals(interfaceAddress)) {
                    return currentMac;
                }
            }
        }
        return null;
    }

    public static void wakeOnLanLocally(Device device) {
        String macAddress = device.getMacAddress();
        if (macAddress == null || macAddress.isEmpty()) {
            ViewUtils.notify("MAC address is required for Wake on LAN");
            return;
        }
        WakeOnLanUtils.wake(macAddress);
    }

    public static boolean isFolded(FoldingFeature foldingFeature) {
        return isFoldable(foldingFeature) && foldingFeature.getState() != FoldingFeature.State.FLAT && foldingFeature.getState() != FoldingFeature.State.HALF_OPENED;
    }

    public static boolean isFoldedHalfway(FoldingFeature foldingFeature) {
        return isFoldable(foldingFeature) && foldingFeature.getState() == FoldingFeature.State.HALF_OPENED;
    }

    public static boolean isFoldable(FoldingFeature foldingFeature) {
        return foldingFeature != null;
    }

    public static boolean isUnfolded(FoldingFeature foldingFeature) {
        return isFoldable(foldingFeature) && foldingFeature.getState() == FoldingFeature.State.FLAT;
    }
}
