package dev.themajorones.remotemanager.fragment;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import androidx.fragment.app.Fragment;
import androidx.annotation.NonNull;
import com.google.android.material.textfield.TextInputEditText;
import dev.themajorones.remotemanager.R;
import dev.themajorones.remotemanager.entity.Device;
import dev.themajorones.remotemanager.entity.SecureShell;
import dev.themajorones.remotemanager.service.SSHService;
import dev.themajorones.remotemanager.utils.DeviceUtils;

public class AddDeviceFragment extends Fragment {

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
        // TODO: Save the device information
        Toast.makeText(getContext(), "Save", Toast.LENGTH_SHORT).show();
    }

    private void onSshFillButtonClick() {
        Device newDevice = new Device()
                .setHost(hostInput.getText().toString())
                .setUsername(usernameInput.getText().toString())
                .setPassword(passwordInput.getText().toString())
                .setPort(Integer.parseInt(portInput.getText().toString()));

        try {
            SecureShell standaloneShell = SSHService.get().getStandaloneConnection(newDevice);
            osInput.setText(DeviceUtils.getOSName(standaloneShell));
            macAddressInput.setText(DeviceUtils.getMacAddress(standaloneShell, newDevice.getHost()));
        } catch (Exception e) {
            Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void onResetButtonClick() {
        hostInput.setText("");
        usernameInput.setText("");
        passwordInput.setText("");
        portInput.setText("");
        osInput.setText("");
        macAddressInput.setText("");
        Toast.makeText(getContext(), "Form reset", Toast.LENGTH_SHORT).show();
    }
}