package com.example.videoplayer;

import android.Manifest;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
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

            requestPermissionForReadExternalStorage();
            videos = new ArrayList<>();

            Button button = findViewById(R.id.local);
            Button buttonTwo = findViewById(R.id.server);
            button.setOnClickListener(this);
            buttonTwo.setOnClickListener(this);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    /**
     * Ask permission from user to use specific storage
     *
     * @throws Exception when permission has been denied
     */
    public void requestPermissionForReadExternalStorage() throws Exception {
        try {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    0x3);
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    /**
     * On click of SplashScreen view
     * Clears set videos for refresh
     *
     * @param v SplashScreen view
     */
    @Override
    public void onClick(View v) {
        // clear videos we want newest videos
        videos.clear();

        switch (v.getId()) {
            case R.id.local:
                try {
                    Uri uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                    String[] projection = {MediaStore.Video.VideoColumns.DATA, MediaStore.Video.Media.DISPLAY_NAME};
                    Cursor cursor = getContentResolver().query(uri, projection, null, null, null);

                    if (cursor != null) {
                        cursor.moveToFirst();
                        do {
                            Bitmap bitmap = ThumbnailUtils.createVideoThumbnail(cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DATA)), MediaStore.Images.Thumbnails.MINI_KIND);
                            bitmap = ThumbnailUtils.extractThumbnail(bitmap, 96, 96);
                            if (bitmap != null)
                                videos.add(new Video(cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DISPLAY_NAME)), cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DATA)), bitmap));
                        } while (cursor.moveToNext());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                startActivity(new Intent(SplashScreen.this, MainActivity.class));
                Toast.makeText(this, "Loading local videos", Toast.LENGTH_LONG).show();
                break;

            case R.id.server:
                try {
                    JSONArray jsonObject = getJSONObjectFromURL("http://139.59.151.31:8080/files");
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
                Toast.makeText(this, "Loading server videos", Toast.LENGTH_LONG).show();
                break;
        }
    }

    /**
     * Get JSON object from URL
     *
     * @param urlString URL to get JSON from
     * @return JSONArray
     * @throws IOException   If object does not exist throw IO exception
     * @throws JSONException If json can not be parsed throw JSON exception
     */
    public static JSONArray getJSONObjectFromURL(String urlString) throws IOException, JSONException {
        HttpURLConnection urlConnection;
        URL url = new URL(urlString);
        urlConnection = (HttpURLConnection) url.openConnection();
        urlConnection.setRequestMethod("GET");
        urlConnection.setReadTimeout(10000);
        urlConnection.setConnectTimeout(15000);
        urlConnection.setDoOutput(true);
        urlConnection.connect();

        BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream()));
        StringBuilder sb = new StringBuilder();

        String line;
        while ((line = br.readLine()) != null) {
            sb.append(line).append("\n");
        }
        br.close();

        String jsonString = sb.toString();
        System.out.println("JSON: " + jsonString);

        return new JSONArray(jsonString);
    }
}
