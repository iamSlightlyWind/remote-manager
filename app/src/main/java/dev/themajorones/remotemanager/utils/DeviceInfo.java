package dev.themajorones.remotemanager.utils;

import android.content.res.Resources;
import androidx.window.layout.FoldingFeature;

public class DeviceInfo {

    public static String getDeviceType(Resources resources) {
        int sw = resources.getConfiguration().smallestScreenWidthDp;
        return sw >= 600 ? "Tablet" : "Phone";
    }

    public static String getFoldingState(FoldingFeature foldingFeature) {
        if (foldingFeature == null) {
            return "Not Foldable";
        }
        if (isFoldedHalfway(foldingFeature)) {
            return "Folded Halfway";
        } else if (isFolded(foldingFeature)) {
            return "Folded";
        } else {
            return "Not Folded";
        }
    }

    public static boolean isFolded(FoldingFeature foldingFeature) {
        return isFoldable(foldingFeature) && foldingFeature.getState() != FoldingFeature.State.FLAT && foldingFeature.getState() != FoldingFeature.State.HALF_OPENED;
    }

    public static boolean isFoldedHalfway(FoldingFeature foldingFeature) {
        return isFoldable(foldingFeature) && foldingFeature.getState() == FoldingFeature.State.HALF_OPENED;
    }

    public static boolean isFoldable(FoldingFeature foldingFeature) {
        return foldingFeature != null;
    }

    public static boolean isUnfolded(FoldingFeature foldingFeature) {
        return isFoldable(foldingFeature) && foldingFeature.getState() == FoldingFeature.State.FLAT;
    }
}
