package com.celestia.settings;

public class SettingsItem {
    private String title;
    private String subtitle;
    private int iconRes;
    private String key;

    public SettingsItem(String title, String subtitle, int iconRes, String key) {
        this.title = title;
        this.subtitle = subtitle;
        this.iconRes = iconRes;
        this.key = key;
    }

    public String getTitle() { return title; }
    public String getSubtitle() { return subtitle; }
    public int getIconRes() { return iconRes; }
    public String getKey() { return key; }
}
