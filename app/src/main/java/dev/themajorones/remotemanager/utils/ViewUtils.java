package dev.themajorones.remotemanager.utils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ListAdapter;
import android.widget.Toast;

import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import java.util.List;

public class ViewUtils {

    public static <T> void fillListView(Context context, ListView listView, List<T> items) {
        ArrayAdapter<T> adapter = new ArrayAdapter<>(
                context,
                android.R.layout.simple_list_item_1,
                items);
        listView.setAdapter(adapter);
    }

    public static void fillListView(ListView listView, ListAdapter adapter) {
        listView.setAdapter(adapter);
    }

    public static void replaceElement(ViewGroup container, int layoutResId) {
        container.removeAllViews();
        LayoutInflater.from(container.getContext()).inflate(layoutResId, container, true);
    }

    public static void notify(String message) {
        Toast.makeText(Preload.getContext(), message, Toast.LENGTH_SHORT).show();
    }

    public static void throwNotify(String message, Throwable e) {
        String exName = e.getClass().getSimpleName();
        String msg = e.getMessage();
        if (msg != null && msg.contains(":")) {
            msg = msg.substring(msg.indexOf(':') + 1).trim();
        }
        String formatted = exName + (msg == null || msg.isEmpty() ? "" : ": " + msg);
        notify(message + formatted);
    }

    public static void replaceFragment(@NonNull FragmentActivity host, @IdRes int containerId, @NonNull Fragment frag) {
        try {
            host.getSupportFragmentManager()
                    .beginTransaction()
                    .replace(containerId, frag)
                    .commit(); // Use commit() instead of commitNow() for better performance
        } catch (Exception e) {
            throwNotify("Failed to replace fragment: ", e);
        }
    }
}