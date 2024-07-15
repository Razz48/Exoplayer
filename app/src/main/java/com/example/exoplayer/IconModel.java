package com.example.exoplayer;

public class IconModel {
    private int imageView;
    private String iconName;

    public IconModel(int imageView, String iconName) {
        this.imageView = imageView;
        this.iconName = iconName;
    }

    public int getImageView() {
        return imageView;
    }

    public void setImageView(int imageView) {
        this.imageView = imageView;
    }

    public String getIconName() {
        return iconName;
    }

    public void setIconName(String iconName) {
        this.iconName = iconName;
    }
}
