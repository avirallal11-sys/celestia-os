package com.celestia.launcher;

import android.graphics.drawable.Drawable;

public class AppItem {
    private String label;
    private Drawable icon;
    private String packageName;

    public AppItem(String label, Drawable icon, String packageName) {
        this.label = label;
        this.icon = icon;
        this.packageName = packageName;
    }

    public String getLabel() { return label; }
    public Drawable getIcon() { return icon; }
    public String getPackageName() { return packageName; }
}
