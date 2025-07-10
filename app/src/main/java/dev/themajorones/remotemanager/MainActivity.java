package dev.themajorones.remotemanager;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.TextView;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.window.layout.FoldingFeature;
import dev.themajorones.remotemanager.utils.Preload;

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
}