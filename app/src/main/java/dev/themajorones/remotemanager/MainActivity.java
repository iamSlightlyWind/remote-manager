package dev.themajorones.remotemanager;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.window.layout.FoldingFeature;

import dev.themajorones.remotemanager.fragment.AddDeviceFragment;
import dev.themajorones.remotemanager.fragment.DeviceItemFragment;
import dev.themajorones.remotemanager.entity.Device;
import dev.themajorones.remotemanager.utils.Preload;
import dev.themajorones.remotemanager.utils.ViewUtils;

public class MainActivity extends AppCompatActivity {

    private final Handler handler = new Handler(Looper.getMainLooper());
    private TextView deviceInfoTextView;
    private FoldingFeature foldingFeature;

    @SuppressLint("MissingInflatedId") @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        Preload.load(this);
        setContentView(R.layout.activity_main);

        ViewUtils.replaceViewWithLayout(findViewById(R.id.mainContent), R.layout.view_list_detail);
        setupButtonTriggers(savedInstanceState);
        testFragment();
    }

    private void setupButtonTriggers(Bundle savedInstanceState) { // TODO: use viewutils.replaceElement
        Button addDeviceButton = findViewById(R.id.addDeviceButton);
        addDeviceButton.setOnClickListener(v -> {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.detailPane, new AddDeviceFragment())
                    .addToBackStack(null)
                    .commit();
        });
    }

    private void testFragment() {
        Device d1 = new Device().setHost("Device 1");
        Device d2 = new Device().setHost("Device 2");
        Device d3 = new Device().setHost("Device 3");
        Device d4 = new Device().setHost("Device 4");
        Device d5 = new Device().setHost("Device 5");
        Device d6 = new Device().setHost("Device 6");
        Device d7 = new Device().setHost("Device 7");
        Device d8 = new Device().setHost("Device 8");
        Device d9 = new Device().setHost("Device 9");
        Device d10 = new Device().setHost("Device 10");

        d1.addManagedDevice(d1);
        d1.addManagedDevice(d2);
        d1.addManagedDevice(d3);
        d1.addManagedDevice(d4);
        d1.addManagedDevice(d5);

        ListView listView = findViewById(R.id.listView);

        Device[] devices = { d1, d2, d3, d4, d5, d6, d7, d8, d9, d10 };
        DeviceItemFragment adapter = new DeviceItemFragment(this, java.util.Arrays.asList(devices));
        listView.setAdapter(adapter);
    }
}