package dev.themajorones.remotemanager;

import static androidx.window.layout.FoldingFeature.State.FLAT;
import static androidx.window.layout.FoldingFeature.State.HALF_OPENED;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
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
        String deviceType = getDeviceType();
        boolean foldable = isFoldable();
        boolean unfolded = isUnfolded();
        boolean folded = isFolded();
        boolean foldedHalfway = isFoldedHalfway();
        String info = "Device Type: " + deviceType + "\n" +
                      "Is Foldable: " + foldable + "\n" +
                      "Is Unfolded: " + unfolded + "\n" +
                      "Is Folded: " + folded + "\n" +
                      "Is Folded Halfway: " + foldedHalfway;
        deviceInfoTextView.setText(info);
    }

    private String getDeviceType() {
        int sw = getResources().getConfiguration().smallestScreenWidthDp;
        return sw >= 600 ? "Tablet" : "Phone";
    }

    private String getFoldingState() {
        if (foldingFeature == null) {
            return "Not Foldable";
        }
        if (isFoldedHalfway()) {
            return "Folded Halfway";
        } else if (isFolded()) {
            return "Folded";
        } else {
            return "Not Folded";
        }
    }

    private boolean isFolded() {
        return isFoldable() && foldingFeature.getState() != FLAT && foldingFeature.getState() != HALF_OPENED;
    }

    private boolean isFoldedHalfway() {
        return isFoldable() && foldingFeature.getState() == HALF_OPENED;
    }

    private boolean isFoldable() {
        return foldingFeature != null;
    }

    private boolean isUnfolded() {
        return isFoldable() && foldingFeature.getState() == FLAT;
    }
}