package com.example.videoplayer;

import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;

import com.halilibo.bettervideoplayer.BetterVideoCallback;
import com.halilibo.bettervideoplayer.BetterVideoPlayer;

import java.io.File;

public class Player extends AppCompatActivity implements BetterVideoCallback {
    private BetterVideoPlayer player;
    ConstraintLayout constraintLayout;
    ImageView next;
    ImageView prev;
    ImageView rotate;

    int position = 0;
    boolean flag = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_player);

            next = findViewById(R.id.next);
            prev = findViewById(R.id.previous);
            rotate = findViewById(R.id.rotate);
            constraintLayout = findViewById(R.id.constraintLayout);

            player = findViewById(R.id.bvp);

            position = getIntent().getIntExtra("position", 0);

            player.setCallback(this);

            System.out.println(SplashScreen.videos.get(position).getPath());

            File file = new File(SplashScreen.videos.get(position).getPath());

            if(file.exists()){
                player.setSource(Uri.fromFile(new File(SplashScreen.videos.get(position).getPath())));
            } else {
                player.setSource(Uri.parse(SplashScreen.videos.get(position).getPath()));
            }

            player.setAutoPlay(true);
            player.enableSwipeGestures();
            player.setHideControlsOnPlay(false);
            player.showControls();
            player.enableControls();

            next.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    goNext();
                }
            });

            prev.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    goBack();
                }
            });

            rotate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (getRequestedOrientation() == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {

                        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                    } else {
                        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                    }
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        player.pause();
    }

    @Override
    public void onStarted(BetterVideoPlayer player) {
        flag = !flag;
    }

    @Override
    public void onPaused(BetterVideoPlayer player) {
    }

    @Override
    public void onPreparing(BetterVideoPlayer player) {
    }

    @Override
    public void onPrepared(BetterVideoPlayer player) {
        if (player.isPlaying()) {
            player.start();
            player.setHideControlsOnPlay(false);
        }
    }

    @Override
    public void onBuffering(int percent) {
    }

    @Override
    public void onError(BetterVideoPlayer player, Exception e) {
    }

    @Override
    public void onCompletion(BetterVideoPlayer player2) {
        if (flag) {
            if (position != SplashScreen.videos.size() - 1) {
                position++;
                player.reset();
                player.setSource(Uri.fromFile(new File(SplashScreen.videos.get(position).getPath())));
                player.setAutoPlay(true);
                player.setHideControlsOnPlay(false);
                player.showControls();
                player.start();

            } else {
                position = 0;
                player.reset();
                player.setSource(Uri.fromFile(new File(SplashScreen.videos.get(position).getPath())));
                player.setAutoPlay(true);
                player.setHideControlsOnPlay(false);
                player.showControls();
                player.start();
            }
            flag = false;
        }

    }

    @Override
    public void onToggleControls(BetterVideoPlayer player, boolean isShowing) {
        if (isShowing) {
            constraintLayout.setVisibility(View.VISIBLE);
        } else {
            constraintLayout.setVisibility(View.INVISIBLE);
        }

    }

    public void goNext() {
        if (position != (SplashScreen.videos.size() - 1)) {
            position++;
            player.reset();
            player.setSource(Uri.fromFile(new File(SplashScreen.videos.get(position).getPath())));
            player.setAutoPlay(true);
            player.start();
        } else {
            position = 0;
            player.reset();
            player.setSource(Uri.fromFile(new File(SplashScreen.videos.get(position).getPath())));
            player.setAutoPlay(true);
            player.start();
        }
    }

    public void goBack() {
        if (position != 0) {
            position--;
            player.reset();
            player.setSource(Uri.fromFile(new File(SplashScreen.videos.get(position).getPath())));
            player.setAutoPlay(true);
            player.start();
        } else {
            position = (SplashScreen.videos.size() - 1);
            player.reset();
            player.setSource(Uri.fromFile(new File(SplashScreen.videos.get(position).getPath())));
            player.setAutoPlay(true);
            player.start();
        }
    }
}
