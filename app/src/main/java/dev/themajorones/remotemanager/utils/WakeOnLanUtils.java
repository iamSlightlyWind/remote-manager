package dev.themajorones.remotemanager.utils;

import android.content.Context;
import android.net.DhcpInfo;
import android.net.wifi.WifiManager;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class WakeOnLanUtils {

    public static void wake(Context context, String macAddress) throws Exception {
        InetAddress broadcast = getBroadcastAddress(context);
        byte[] packet = buildMagicPacket(macAddress);
        sendPacket(broadcast, packet, 9);
    }

    private static InetAddress getBroadcastAddress(Context ctx) throws Exception {
        WifiManager wm = (WifiManager) ctx.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        DhcpInfo info = wm.getDhcpInfo();
        int ip = info.ipAddress;
        int mask = info.netmask;
        int bcast = (ip & mask) | ~mask;
        byte[] bytes = ByteBuffer.allocate(4)
                .order(ByteOrder.LITTLE_ENDIAN)
                .putInt(bcast)
                .array();
        return InetAddress.getByAddress(bytes);
    }

    private static byte[] buildMagicPacket(String mac) {
        String[] hex = mac.split("([:\\-])");
        if (hex.length != 6) {
            throw new IllegalArgumentException("Invalid MAC format");
        }

        byte[] macBytes = new byte[6];
        for (int i = 0; i < 6; i++) {
            macBytes[i] = (byte) Integer.parseInt(hex[i], 16);
        }
        byte[] packet = new byte[6 + 16 * macBytes.length];
        
        for (int i = 0; i < 6; i++) {
            packet[i] = (byte) 0xFF;
        }
        
        for (int i = 6; i < packet.length; i += macBytes.length) {
            System.arraycopy(macBytes, 0, packet, i, macBytes.length);
        }
        return packet;
    }

    private static void sendPacket(InetAddress dest, byte[] data, int port) throws Exception {
        DatagramPacket packet = new DatagramPacket(data, data.length, dest, port);
        try (DatagramSocket socket = new DatagramSocket()) {
            socket.setBroadcast(true);
            socket.send(packet);
        }
    }
}