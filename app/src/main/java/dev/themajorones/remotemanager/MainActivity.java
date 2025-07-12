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
import dev.themajorones.remotemanager.utils.Preload;
import dev.themajorones.remotemanager.utils.ViewUtils;

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

        ViewUtils.replaceElement(findViewById(R.id.mainContent), R.layout.view_list_detail);
        setupButtonTriggers(savedInstanceState);
        handler.post(deviceListUpdater);
    }

    private void setupButtonTriggers(Bundle savedInstanceState) {
        Button addDeviceButton = findViewById(R.id.addDeviceButton);
        addDeviceButton.setOnClickListener(v -> {
            ViewUtils.replaceFragment(this, R.id.detailPane, new AddDeviceFragment());
        });
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
            ViewUtils.Notify(this, "Device list updated");
            ViewUtils.fillListView(listView, adapter);
        }
    }
}