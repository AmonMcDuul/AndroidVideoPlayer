package com.example.videoplayer;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class SplashScreen extends AppCompatActivity implements View.OnClickListener {
    static ArrayList<Video> videos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);

            requestWindowFeature(Window.FEATURE_NO_TITLE);
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_splash_screen);

            requestPermissionForReadExtertalStorage();
            videos = new ArrayList<>();

            Button button = findViewById(R.id.local);
            Button buttontwo = findViewById(R.id.server);
            button.setOnClickListener(this);
            buttontwo.setOnClickListener(this);


        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        System.out.print("lel");
        finish();
    }

    public void requestPermissionForReadExtertalStorage() throws Exception {
        try {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    0x3);
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    @Override
    public void onClick(View v) {
        videos.clear();

        switch (v.getId()) {
            case R.id.local:
                try {
                    File uri = Environment.getExternalStorageDirectory();
                    String[] projection = {MediaStore.Video.VideoColumns.DATA, MediaStore.Video.Media.DISPLAY_NAME};
                    Cursor cursor = getContentResolver().query(Uri.fromFile(uri), projection, null, null, null);

                    if (cursor != null) {
                        cursor.moveToFirst();
                        do {
                            Bitmap bitmap = ThumbnailUtils.createVideoThumbnail(cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DATA)), MediaStore.Images.Thumbnails.MINI_KIND);
                            bitmap = ThumbnailUtils.extractThumbnail(bitmap, 96, 96);
                            if (bitmap != null)
                                videos.add(new Video(cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DISPLAY_NAME)), cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DATA)), bitmap));
                            Log.e("video info :", cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DATA)));
                        } while (cursor.moveToNext());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                startActivity(new Intent(SplashScreen.this, MainActivity.class));
                Toast.makeText(this, "Lokale data wordt geladen jeuh", Toast.LENGTH_LONG).show();
                break;

            case R.id.server:
                try {
                    JSONArray jsonObject = getJSONObjectFromURL("http://10.0.2.2:8080/files");
                    System.out.println(jsonObject);
                    for (int i = 0; i < jsonObject.length(); i++) {
                        JSONObject item = jsonObject.getJSONObject(i);
                        Object url = item.get("url");
                        Object filename = item.get("filename");
                        Bitmap bMap = ThumbnailUtils.createVideoThumbnail(url.toString(), MediaStore.Video.Thumbnails.MICRO_KIND);
                        Video video = new Video(filename.toString(), url.toString(), bMap);
                        if (!videos.contains(video)) {
                            videos.add(video);
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                startActivity(new Intent(SplashScreen.this, MainActivity.class));
                Toast.makeText(this, "Server data wordt geladen jeuh", Toast.LENGTH_LONG).show();
                break;

        }
    }

    public static JSONArray getJSONObjectFromURL(String urlString) throws IOException, JSONException {
        HttpURLConnection urlConnection = null;
        URL url = new URL(urlString);
        urlConnection = (HttpURLConnection) url.openConnection();
        urlConnection.setRequestMethod("GET");
        urlConnection.setReadTimeout(10000 /* milliseconds */);
        urlConnection.setConnectTimeout(15000 /* milliseconds */);
        urlConnection.setDoOutput(true);
        urlConnection.connect();

        BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream()));
        StringBuilder sb = new StringBuilder();

        String line;
        while ((line = br.readLine()) != null) {
            sb.append(line + "\n");
        }
        br.close();

        String jsonString = sb.toString();
        System.out.println("JSON: " + jsonString);

        return new JSONArray(jsonString);
    }
}
