package dev.themajorones.remotemanager.fragment;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import androidx.fragment.app.Fragment;
import androidx.annotation.NonNull;
import dev.themajorones.remotemanager.R;

public class AddDeviceFragment extends Fragment {
    public AddDeviceFragment() {
        super(R.layout.add_device);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupButtonTrigger(view, savedInstanceState);
    }

    private void setupButtonTrigger(@NonNull View view, Bundle savedInstanceState) {
        Button resetButton = view.findViewById(R.id.reset_button);
        resetButton.setOnClickListener(v -> {
            Toast.makeText(getContext(), "Reset button clicked", Toast.LENGTH_SHORT).show();
        });

        Button sshFillButton = view.findViewById(R.id.ssh_fill_button);
        sshFillButton.setOnClickListener(v -> {
            Toast.makeText(getContext(), "SSH Fill button clicked", Toast.LENGTH_SHORT).show();
        });

        Button saveButton = view.findViewById(R.id.save_button);
        saveButton.setOnClickListener(v -> {
            Toast.makeText(getContext(), "Save button clicked", Toast.LENGTH_SHORT).show();
        });
    }
}