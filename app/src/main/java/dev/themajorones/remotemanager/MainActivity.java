package dev.themajorones.remotemanager;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.window.layout.FoldingFeature;

import java.util.ArrayList;
import java.util.List;

import dev.themajorones.remotemanager.fragment.AddDeviceFragment;
import dev.themajorones.remotemanager.entity.Device;
import dev.themajorones.remotemanager.fragment.DeviceItemAdapter;
import dev.themajorones.remotemanager.fragment.ManagingDeviceAdapter;
import dev.themajorones.remotemanager.fragment.SettingsFragment;
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
    private List<Device> currentManagingDevices = new ArrayList<>();

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SettingsFragment.applyLanguageSettings(this);
        SettingsFragment.applyThemeSettings(this);
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        Preload.load(this);
        setContentView(R.layout.activity_main);

        onPressDeviceManagerButton();
        setupButtonTriggers(savedInstanceState);
    }

    private void setupButtonTriggers(Bundle savedInstanceState) {
        Button deviceManagerButton = findViewById(R.id.button1);
        if (deviceManagerButton instanceof VerticalMaterialButton vButton1) {
            vButton1.setOnClickListener(v -> onPressDeviceManagerButton());
        } else if (deviceManagerButton != null) {
            deviceManagerButton.setOnClickListener(v -> onPressDeviceManagerButton());
        }

        Button deviceHierarchyButton = findViewById(R.id.button2);
        if (deviceHierarchyButton instanceof VerticalMaterialButton vButton2) {
            vButton2.setOnClickListener(v -> onPressDeviceHierarchyButton());
        } else if (deviceHierarchyButton != null) {
            deviceHierarchyButton.setOnClickListener(v -> onPressDeviceHierarchyButton());
        }

        Button settingsButton = findViewById(R.id.settingsButton);
        if (settingsButton instanceof VerticalMaterialButton vSettingsButton) {
            vSettingsButton.setOnClickListener(v -> onPressSettingsButton());
        } else if (settingsButton != null) {
            settingsButton.setOnClickListener(v -> onPressSettingsButton());
        }
    }

    private void onPressSettingsButton() {
        handler.removeCallbacks(deviceListUpdater);
        handler.removeCallbacks(managingDeviceListUpdater);
        preChangeTab();
        ViewUtils.replaceFragment(this, R.id.mainContent, new SettingsFragment());
    }

    private void setupDeviceManagerButtonTriggers(Bundle savedInstanceState) {
        Button addDeviceButton = findViewById(R.id.addDeviceButton);
        addDeviceButton.setOnClickListener(v -> spawnAddDeviceFragment());
        addDeviceButton.setOnLongClickListener(v -> DataLoader.loadData());
    }

    private void fillManagingDeviceList() {
        List<Device> managingDevices = PersistentStorageService.get().findAllManagingDevices();
        ListView listView = findViewById(R.id.listView);

        if (!managingDevices.equals(currentManagingDevices)) {
            currentManagingDevices = managingDevices;
            ManagingDeviceAdapter adapter = new ManagingDeviceAdapter(this, currentManagingDevices);
            ViewUtils.fillListView(listView, adapter);
        }
    }

    private final Runnable managingDeviceListUpdater = new Runnable() {
        @Override
        public void run() {
            fillManagingDeviceList();
            handler.postDelayed(this, 250);
        }
    };

    private void preChangeTab(){
        ((ViewGroup) findViewById(R.id.mainContent)).removeAllViews();
        AddDeviceFragment.removeInstance();
    }

    private void onPressDeviceHierarchyButton() {
        handler.removeCallbacks(deviceListUpdater);
        preChangeTab();
        ViewUtils.replaceElement(findViewById(R.id.mainContent), R.layout.device_hierarchy);
        currentManagingDevices = new ArrayList<>();

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.mainContent, new Fragment())
                .commitNow();
        
        handler.post(managingDeviceListUpdater);
    }

    public void spawnAddDeviceFragment() {
        ViewUtils.replaceFragment(this, R.id.detailPane, new AddDeviceFragment());
    }

    private void onPressDeviceManagerButton() {
        handler.removeCallbacks(managingDeviceListUpdater);
        preChangeTab();
        currentDevices = new ArrayList<>();

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.mainContent, new Fragment())
                .commitNow();
                
        ViewUtils.replaceElement(findViewById(R.id.mainContent), R.layout.device_manager);
        handler.post(deviceListUpdater);
        setupDeviceManagerButtonTriggers(this.getIntent().getExtras());
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