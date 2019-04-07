package com.example.videoplayer;

import android.graphics.Bitmap;

public class Video {
    String title;
    private String path;
    private Bitmap thumb;

    public Video(String title, String path, Bitmap thumb) {
        this.title = title;
        this.path = path;
        this.thumb = thumb;
    }

    String getPath() {
        return path;
    }

    Bitmap getThumb() {
        return thumb;
    }

}

