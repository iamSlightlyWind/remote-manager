package dev.themajorones.remotemanager.fragment;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.fragment.app.Fragment;
import androidx.annotation.NonNull;
import com.google.android.material.textfield.TextInputEditText;
import org.w3c.dom.Text;
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

    public AddDeviceFragment() {
        super(R.layout.add_device);
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

        super.onViewCreated(view, savedInstanceState);
        setupButtonTrigger(view, savedInstanceState);
    }


    private void setupButtonTrigger(@NonNull View view, Bundle savedInstanceState) {
        Button resetButton = view.findViewById(R.id.reset_button);
        resetButton.setOnClickListener(v -> onResetButtonClick());

        Button sshFillButton = view.findViewById(R.id.ssh_fill_button);
        sshFillButton.setOnClickListener(v -> onSshFillButtonClick());

        Button saveButton = view.findViewById(R.id.save_button);
        saveButton.setOnClickListener(v -> onSaveButtonClick());
    }

    private void onSaveButtonClick() {
        String name = Objects.requireNonNull(nameInput.getText()).toString();
        String host = Objects.requireNonNull(hostInput.getText()).toString();
        String username = Objects.requireNonNull(usernameInput.getText()).toString();
        String password = Objects.requireNonNull(passwordInput.getText()).toString();
        int port = Integer.parseInt(Objects.requireNonNull(portInput.getText()).toString());
        String os = Objects.requireNonNull(osInput.getText()).toString();
        String macAddress = Objects.requireNonNull(macAddressInput.getText()).toString();

        Device newDevice = Device.builder()
                .name(name)
                .host(host)
                .username(username)
                .password(password)
                .port(port)
                .os(os)
                .macAddress(macAddress)
                .build();

        Device id = PersistentStorageService.get().save(newDevice);
        if (id != null) {
            ViewUtils.Notify(getContext(), "Device saved successfully");
        } else {
            ViewUtils.Notify(getContext(), "Failed to save device");
        }
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
            ViewUtils.Notify(getContext(), e.getMessage());
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
        ViewUtils.Notify(getContext(), "Form reset");
    }
}