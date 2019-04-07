package com.example.videoplayer;

import android.content.Intent;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.koushikdutta.ion.Ion;

import java.io.File;

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
                        boolean delete = file.delete();
                        if (delete) {
                            Toast.makeText(this, "Video has been deleted", Toast.LENGTH_LONG).show();
                        }
                    }
                    MediaScannerConnection.scanFile(getApplicationContext(), new String[]{SplashScreen.videos.get(pos).getPath()}, null, null);
                    SplashScreen.videos.remove(pos);
                    myAdapter.updateData(SplashScreen.videos);
                    break;
                }
                case R.id.uplink: {
                    File file = new File(SplashScreen.videos.get(pos).getPath());
                    doFileUpload(file.getAbsolutePath());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Upload file with chosen file
     * @param existingFilename existing file name
     */
    private void doFileUpload(String existingFilename) {
        Toast.makeText(this, "Uploading video", Toast.LENGTH_LONG).show();

        Ion.with(this)
                .load("http://139.59.151.31:8080/uploadfile")
                .setMultipartFile("uploadfile", "multipart/form-data", new File(existingFilename))
                .asJsonObject();

        Toast.makeText(this, "Uploading done", Toast.LENGTH_LONG).show();
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
