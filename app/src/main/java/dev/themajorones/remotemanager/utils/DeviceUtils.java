package dev.themajorones.remotemanager.utils;

import android.content.res.Resources;
import androidx.window.layout.FoldingFeature;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import dev.themajorones.remotemanager.entity.SecureShell;

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

    public static String getOSName(SecureShell shell){
        String uname = shell.runCommand("uname -s").trim().toLowerCase();
        return switch (uname.toLowerCase().trim()) {
            case "linux" -> "Linux";
            case "darwin" -> "macOS";
            case "windows" -> "Windows";
            default -> "Unknown OS";
        };
    }

    public static String getMacAddress(SecureShell shell, String interfaceAddress) {
        String os = getOSName(shell);
        return switch (os) {
            case "Linux" -> getMacFromLinuxIPA(shell.runCommand("ip a"), interfaceAddress);
            case "macOS" -> getMacFromMacIfconfig(shell.runCommand("ifconfig -a"), interfaceAddress);
            case "Windows" -> shell.runCommand("getmac /v /fo csv").split(",")[0].replaceAll("\"", "");
            default -> throw new UnsupportedOperationException("Unsupported OS: " + os);
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
        return null;
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
