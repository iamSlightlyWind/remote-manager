package dev.themajorones.remotemanager;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.window.layout.FoldingFeature;
import dev.themajorones.remotemanager.adapter.DeviceAdapter;
import dev.themajorones.remotemanager.entity.Device;
import dev.themajorones.remotemanager.utils.Preload;
import dev.themajorones.remotemanager.utils.ViewUtils;

public class MainActivity extends AppCompatActivity {

    private final Handler handler = new Handler(Looper.getMainLooper());
    private TextView deviceInfoTextView;
    private FoldingFeature foldingFeature;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        Preload.load(this);
        setContentView(R.layout.activity_main);
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

        Device[] devices = {d1, d2, d3, d4, d5, d6, d7, d8, d9, d10};
        DeviceAdapter adapter = new DeviceAdapter(this, java.util.Arrays.asList(devices));
        listView.setAdapter(adapter);
    }

    private void testReplace() {
        LinearLayout detailPane = findViewById(R.id.detailPane);
        ViewUtils.replaceViewWithLayout(detailPane, R.layout.add_device);
    }
}