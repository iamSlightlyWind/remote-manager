package dev.themajorones.remotemanager;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.Button;
import android.widget.TextView;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.window.layout.DisplayFeature;
import androidx.window.layout.FoldingFeature;
import androidx.window.layout.WindowInfoTracker;
import androidx.window.layout.WindowLayoutInfo;
import androidx.window.java.layout.WindowInfoTrackerCallbackAdapter;
import androidx.core.util.Consumer;
import dev.themajorones.remotemanager.utils.DeviceInfo;
import dev.themajorones.remotemanager.utils.SecureShellUtils;

public class MainActivity extends AppCompatActivity {

    private final Handler handler = new Handler(Looper.getMainLooper());
    private TextView deviceInfoTextView;
    private FoldingFeature foldingFeature;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        deviceInfoTextView = findViewById(R.id.deviceInfoTextView);
        TextView sshValueTextView = findViewById(R.id.sshValue);
        Button sshButton = findViewById(R.id.sshButton);

        sshButton.setOnClickListener(v -> {
            new Thread(() -> {
                try {
                    SecureShellUtils sshUtils = new SecureShellUtils();
                    sshUtils.connect("windstation.themajorones.dev", 22, "slightlywind", "301203", null);
                    String result = sshUtils.runCommand("uname -a");
                    sshUtils.disconnect();
                    runOnUiThread(() -> sshValueTextView.setText(result));
                } catch (Exception e) {
                    runOnUiThread(() -> sshValueTextView.setText(e.getMessage()));
                }
            }).start();
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        WindowInfoTrackerCallbackAdapter adapter = new WindowInfoTrackerCallbackAdapter(WindowInfoTracker.getOrCreate(this));

        adapter.addWindowLayoutInfoListener(
                this,
                getMainExecutor(),
                new Consumer<WindowLayoutInfo>() {
                    @Override
                    public void accept(WindowLayoutInfo info) {
                        foldingFeature = null;
                        for (DisplayFeature feature : info.getDisplayFeatures()) {
                            if (feature instanceof FoldingFeature) {
                                foldingFeature = (FoldingFeature) feature;
                                break;
                            }
                        }
                        updateDeviceInfo();
                    }
                });

        startDeviceInfoUpdates();
    }

    private void startDeviceInfoUpdates() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                updateDeviceInfo();
                handler.postDelayed(this, 500);
            }
        }, 500);
    }

    private void updateDeviceInfo() {
        String deviceType = DeviceInfo.getDeviceType(getResources());
        boolean foldable = DeviceInfo.isFoldable(foldingFeature);
        boolean unfolded = DeviceInfo.isUnfolded(foldingFeature);
        boolean folded = DeviceInfo.isFolded(foldingFeature);
        boolean foldedHalfway = DeviceInfo.isFoldedHalfway(foldingFeature);
        String info = "Device Type: " + deviceType + "\n" +
                      "Is Foldable: " + foldable + "\n" +
                      "Is Unfolded: " + unfolded + "\n" +
                      "Is Folded: " + folded + "\n" +
                      "Is Folded Halfway: " + foldedHalfway;
        deviceInfoTextView.setText(info);
    }
}