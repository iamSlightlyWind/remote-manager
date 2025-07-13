package dev.themajorones.remotemanager.fragment;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.fragment.app.Fragment;
import androidx.annotation.NonNull;

import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import dev.themajorones.remotemanager.R;
import dev.themajorones.remotemanager.entity.Device;
import dev.themajorones.remotemanager.entity.SecureShell;
import dev.themajorones.remotemanager.service.PersistentStorageService;
import dev.themajorones.remotemanager.service.SSHService;
import dev.themajorones.remotemanager.utils.DeviceUtils;
import dev.themajorones.remotemanager.utils.ViewUtils;

public class AddDeviceFragment extends Fragment {

    TextInputEditText nameInput;
    TextInputEditText hostInput;
    TextInputEditText usernameInput;
    TextInputEditText passwordInput;
    TextInputEditText portInput;
    TextInputEditText osInput;
    TextInputEditText macAddressInput;

    private Device savedDevice;
    private View savedView;
    private static AddDeviceFragment savedInstance;

    public AddDeviceFragment() {
        super(R.layout.add_device);
    }

    public static AddDeviceFragment getInstance() {
        return savedInstance;
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        nameInput = view.findViewById(R.id.name_input);
        hostInput = view.findViewById(R.id.host_input);
        usernameInput = view.findViewById(R.id.username_input);
        passwordInput = view.findViewById(R.id.password_input);
        portInput = view.findViewById(R.id.port_input);
        osInput = view.findViewById(R.id.os_input);
        macAddressInput = view.findViewById(R.id.mac_address_input);
        savedView = view;

        super.onViewCreated(view, savedInstanceState);
        setupButtonTrigger(view, savedInstanceState);
        savedInstance = this;
    }

    public void editDevice(Device device) {
        savedDevice = null;
        savedDevice = device;
        nameInput.setText(device.getName());
        hostInput.setText(device.getHost());
        usernameInput.setText(device.getUsername());
        passwordInput.setText(device.getPassword());
        portInput.setText(String.valueOf(device.getPort()));
        osInput.setText(device.getOs());
        macAddressInput.setText(device.getMacAddress());
        Button deleteButton = savedView.findViewById(R.id.delete_button);
        deleteButton.setVisibility(View.VISIBLE);
    }

    private void setupButtonTrigger(@NonNull View view, Bundle savedInstanceState) {
        Button resetButton = view.findViewById(R.id.reset_button);
        resetButton.setOnClickListener(v -> onResetButtonClick());

        Button sshFillButton = view.findViewById(R.id.ssh_fill_button);
        sshFillButton.setOnClickListener(v -> onSshFillButtonClick());

        Button saveButton = view.findViewById(R.id.save_button);
        saveButton.setOnClickListener(v -> onSaveButtonClick());

        Button deleteButton = view.findViewById(R.id.delete_button);
        deleteButton.setOnClickListener(v -> onDeleteButtonClick());
        deleteButton.setVisibility(View.GONE);
    }

    private void onDeleteButtonClick() {
        String deviceName = Objects.requireNonNull(nameInput.getText()).toString();
        Device device = PersistentStorageService.get().findByName(deviceName);
        List<Device> managedDevices = new ArrayList<>();

        if (device != null) {
            managedDevices = PersistentStorageService.get().findManagedDevices(device);

            PersistentStorageService.get().delete(device);
            ViewUtils.notify("Device deleted successfully");
            onResetButtonClick();
        } else {
            managedDevices = PersistentStorageService.get().findManagedDevices(savedDevice);
            PersistentStorageService.get().delete(savedDevice);
        }

        for (Device managedDevice : managedDevices) {
            managedDevice.removesManagingDevice(device);
            PersistentStorageService.get().save(managedDevice);
        }

        onResetButtonClick();
    }

    private void onSaveButtonClick() {
        String name, host, username, password, os, macAddress;
        List<Device> managedDevices = PersistentStorageService.get().findManagedDevices(savedDevice);
        List<Device> managingDevices = savedDevice != null ? savedDevice.getManagingDevices() : new ArrayList<>();
        int port;

        try {
            name = Objects.requireNonNull(nameInput.getText()).toString();

            if (name.isEmpty()) {
                ViewUtils.notify("Name cannot be empty");
                return;
            }

            if (savedDevice == null) {
                Device existingDevice = PersistentStorageService.get().findByName(name);
                if (existingDevice != null) {
                    ViewUtils.notify("Device with this name already exists");
                    return;
                }
            }

            host = Objects.requireNonNull(hostInput.getText()).toString();
            username = Objects.requireNonNull(usernameInput.getText()).toString();
            password = Objects.requireNonNull(passwordInput.getText()).toString();
            port = Integer.parseInt(Objects.requireNonNull(portInput.getText()).toString());
            os = Objects.requireNonNull(osInput.getText()).toString();
            macAddress = Objects.requireNonNull(macAddressInput.getText()).toString();
        } catch (NumberFormatException e) {
            ViewUtils.notify("Port must be a number");
            return;
        } catch (Exception e) {
            ViewUtils.throwNotify("Failed: ", e);
            return;
        }

        Device newDevice = Device.builder()
                .name(name)
                .host(host)
                .username(username)
                .password(password)
                .port(port)
                .os(os)
                .macAddress(macAddress)
                .managingDevices(managingDevices)
                .build();

        if (savedDevice != null) {
            if (!newDevice.getName().equals(savedDevice.getName())) {
                PersistentStorageService.get().delete(savedDevice);
            }
        }

        for (Device managedDevice : managedDevices) {
            managedDevice.removesManagingDevice(savedDevice);
            managedDevice.addManagingDevice(newDevice);
            PersistentStorageService.get().save(managedDevice);
        }

        newDevice = PersistentStorageService.get().save(newDevice);

        if (newDevice != null) {
            ViewUtils.notify("Device saved successfully");
        } else {
            ViewUtils.notify("Failed to save device");
        }

        onResetButtonClick();
    }

    private void onSshFillButtonClick() {
        Device newDevice = Device.builder()
                .name(Objects.requireNonNull(nameInput.getText()).toString())
                .host(Objects.requireNonNull(hostInput.getText()).toString())
                .username(Objects.requireNonNull(usernameInput.getText()).toString())
                .password(Objects.requireNonNull(passwordInput.getText()).toString())
                .port(Integer.parseInt(Objects.requireNonNull(portInput.getText()).toString()))
                .build();

        try {
            SecureShell standaloneShell = SSHService.get().getStandaloneConnection(newDevice);
            newDevice.setOs(DeviceUtils.getOSName(standaloneShell));
            newDevice.setMacAddress(DeviceUtils.getMacAddress(standaloneShell, newDevice.getHost()));
            osInput.setText(newDevice.getOs());
            macAddressInput.setText(newDevice.getMacAddress());
        } catch (Exception e) {
            ViewUtils.throwNotify("Failed: ", e);
        }
    }

    private void onResetButtonClick() {
        nameInput.setText("");
        hostInput.setText("");
        usernameInput.setText("");
        passwordInput.setText("");
        portInput.setText("");
        osInput.setText("");
        macAddressInput.setText("");
        savedDevice = null;
        Button deleteButton = savedView.findViewById(R.id.delete_button);
        deleteButton.setVisibility(View.GONE);
    }
}