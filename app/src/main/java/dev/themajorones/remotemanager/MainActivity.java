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
import java.util.ArrayList;
import java.util.List;
import dev.themajorones.remotemanager.fragment.AddDeviceFragment;
import dev.themajorones.remotemanager.entity.Device;
import dev.themajorones.remotemanager.fragment.DeviceItemAdapter;
import dev.themajorones.remotemanager.service.PersistentStorageService;
import dev.themajorones.remotemanager.service.development.DataLoader;
import dev.themajorones.remotemanager.utils.Preload;
import dev.themajorones.remotemanager.utils.ViewUtils;
import dev.themajorones.remotemanager.utils.button.VerticalMaterialButton;

public class MainActivity extends AppCompatActivity {

    private final Handler handler = new Handler(Looper.getMainLooper());
    private TextView deviceInfoTextView;
    private FoldingFeature foldingFeature;
    private List<Device> currentDevices = new ArrayList<>();

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        Preload.load(this);
        setContentView(R.layout.activity_main);

        ViewUtils.replaceElement(findViewById(R.id.mainContent), R.layout.detail_list);
        setupButtonTriggers(savedInstanceState);
        handler.post(deviceListUpdater);
    }

    private void setupButtonTriggers(Bundle savedInstanceState) {
        Button addDeviceButton = findViewById(R.id.addDeviceButton);
        addDeviceButton.setOnClickListener(v -> spawnAddDeviceFragment());
        addDeviceButton.setOnLongClickListener( v -> DataLoader.loadData());

        Button button1 = findViewById(R.id.button1);
        if (button1 instanceof VerticalMaterialButton) {
            VerticalMaterialButton vButton1 = (VerticalMaterialButton) button1;
            vButton1.setOnClickListener(v -> ViewUtils.notify("Button 1 clicked"));
        } else if (button1 != null) {
            button1.setOnClickListener(v -> ViewUtils.notify("Button 1 clicked"));
        }

        Button button2 = findViewById(R.id.button2);
        if (button2 instanceof VerticalMaterialButton) {
            VerticalMaterialButton vButton2 = (VerticalMaterialButton) button2;
            vButton2.setOnClickListener(v -> ViewUtils.notify("Button 2 clicked"));
        } else if (button2 != null) {
            button2.setOnClickListener(v -> ViewUtils.notify("Button 2 clicked"));
        }
        
        Button settingsButton = findViewById(R.id.settingsButton);
        if (settingsButton instanceof VerticalMaterialButton) {
            VerticalMaterialButton vSettingsButton = (VerticalMaterialButton) settingsButton;
            vSettingsButton.setOnClickListener(v -> ViewUtils.notify("Settings button clicked"));
        } else if (settingsButton != null) {
            settingsButton.setOnClickListener(v -> ViewUtils.notify("Settings button clicked"));
        }
    }

    public void spawnAddDeviceFragment() {
        ViewUtils.replaceFragment(this, R.id.detailPane, new AddDeviceFragment());
    }

    private final Runnable deviceListUpdater = new Runnable() {
        @Override
        public void run() {
            fillDeviceList();
            handler.postDelayed(this, 250);
        }
    };

    private void fillDeviceList() {
        ListView listView = findViewById(R.id.listView);
        List<Device> devices = PersistentStorageService.get().findAll();

        if (!devices.equals(currentDevices)) {
            currentDevices = devices;
            DeviceItemAdapter adapter = new DeviceItemAdapter(this, currentDevices);
            ViewUtils.fillListView(listView, adapter);
        }
    }
}