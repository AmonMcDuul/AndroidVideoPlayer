package com.example.videoplayer;

import android.view.View;

public interface CustomItemClickListner {
    void onItemClick(View v, int position);
    void onItemLongClick(int position);

}
