package dev.themajorones.remotemanager.utils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ListAdapter;
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
                items
        );
        listView.setAdapter(adapter);
    }

    public static void fillListView(ListView listView, ListAdapter adapter) {
        listView.setAdapter(adapter);
    }

    public static void replaceElement(ViewGroup container, int layoutResId) {
        container.removeAllViews();
        LayoutInflater.from(container.getContext()).inflate(layoutResId, container, true);
    }

    public static void replaceFragment(@NonNull FragmentActivity host, @IdRes int containerId, @NonNull Fragment frag) {
        host.getSupportFragmentManager()
                .beginTransaction()
                .replace(containerId, frag)
                .addToBackStack(null)
                .commit();
    }
}