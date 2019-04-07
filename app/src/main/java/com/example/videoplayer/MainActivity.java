package com.example.videoplayer;

import android.content.Intent;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.koushikdutta.ion.Ion;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    RecyclerView recyclerView;
    ImageView share;
    ImageView delete;
    ImageView uplink;
    ConstraintLayout constraintLayout;
    MyAdapter myAdapter;
    int pos = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        recyclerView = findViewById(R.id.recyclerView);
        delete = findViewById(R.id.delete);
        share = findViewById(R.id.share);
        uplink = findViewById(R.id.uplink);
        constraintLayout = findViewById(R.id.constraintLayout2);

        delete.setOnClickListener(this);
        share.setOnClickListener(this);
        uplink.setOnClickListener(this);

        myAdapter = new MyAdapter(getApplicationContext(), SplashScreen.videos, new CustomItemClickListner() {
            @Override
            public void onItemClick(View v, int position) {
                Intent intent = new Intent(MainActivity.this, Player.class);
                intent.putExtra("position", position);
                startActivity(intent);
            }

            @Override
            public void onItemLongClick(int position) {
                if (constraintLayout.getVisibility() == View.VISIBLE) {
                    constraintLayout.setVisibility(View.GONE);
                } else {
                    constraintLayout.setVisibility(View.VISIBLE);
                }

                pos = position;
            }
        });

        LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(myAdapter);
    }

    @Override
    public void onClick(View v) {
        try {
            int id = v.getId();
            switch (id) {
                case R.id.share: {
                    Intent sendIntent = new Intent(Intent.ACTION_SEND);
                    sendIntent.setType("video/*");
                    sendIntent.putExtra(Intent.EXTRA_SUBJECT, "Video");
                    sendIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse(SplashScreen.videos.get(pos).getPath()));
                    sendIntent.putExtra(Intent.EXTRA_TEXT, "Enjoy the Video");
                    startActivity(Intent.createChooser(sendIntent, "Share"));
                    break;
                }
                case R.id.delete: {
                    File file = new File(SplashScreen.videos.get(pos).getPath());
                    if (file.exists()) {
                        file.delete();
                    }
                    MediaScannerConnection.scanFile(getApplicationContext(), new String[]{SplashScreen.videos.get(pos).getPath()}, null, null);
                    SplashScreen.videos.remove(pos);
                    myAdapter.updateData(SplashScreen.videos);
                    break;
                }
                case R.id.uplink: {
                    File file = new File(SplashScreen.videos.get(pos).getPath());
                    doFileUpload("/storage/self/primary/Download/fish (1).mp4");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void doFileUpload(String existingFilename) {
        Ion.with(this)
                .load("http://10.0.2.2:8080/uploadfile")
                .setMultipartFile("uploadfile", "multipart/form-data", new File(existingFilename))
                .asJsonObject();
    }

    @Override
    public void onBackPressed() {
        if (constraintLayout.getVisibility() == View.VISIBLE) {
            constraintLayout.setVisibility(View.GONE);
        } else {
            super.onBackPressed();
        }

    }
}
