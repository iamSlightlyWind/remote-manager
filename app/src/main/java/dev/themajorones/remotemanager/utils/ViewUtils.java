package dev.themajorones.remotemanager.utils;

import android.view.LayoutInflater;
import android.view.ViewGroup;

public class ViewUtils {

    public static void replaceViewWithLayout(ViewGroup container, int layoutResId) {
        container.removeAllViews();
        LayoutInflater.from(container.getContext()).inflate(layoutResId, container, true);
    }
}
