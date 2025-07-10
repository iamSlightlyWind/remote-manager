package dev.themajorones.remotemanager.utils;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.IdRes;

public class ViewUtils {

    public static void replaceViewWithLayout(ViewGroup container, int layoutResId) {
        container.removeAllViews();
        LayoutInflater.from(container.getContext()).inflate(layoutResId, container, true);
    }

    public static void replaceElement(View root, @IdRes int oldViewId, View newView) {
        if (root == null) return;
        View old = root.findViewById(oldViewId);
        if (old == null) return;

        ViewGroup parent = (ViewGroup) old.getParent();
        if (parent == null) return;

        int idx = parent.indexOfChild(old);
        ViewGroup.LayoutParams lp = old.getLayoutParams();

        parent.removeViewAt(idx);
        parent.addView(newView, idx, lp);
    }

}