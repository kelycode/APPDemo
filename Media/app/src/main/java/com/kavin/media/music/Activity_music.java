package com.kavin.media.music;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.view.View;
import android.widget.SeekBar;
import com.kavin.jutils.utils.permission.PermissionRequest;

import com.kavin.media.R;

public class Activity_music extends AppCompatActivity {
    static Handler handler = new Handler(){
        public void handleMessage(android.os.Message msg) {
            Bundle bundle = msg.getData();
            int duration = bundle.getInt("duration");
            int currentPostition = bundle.getInt("currentPosition");
            //刷新进度条的进度
            sb.setMax(duration);
            sb.setProgress(currentPostition);
        }
    };
    MyMusicInterface mi;
    private MyserviceConn conn;
    private Intent intent;
    private static SeekBar sb;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music);

        requestPremission();

        sb = (SeekBar) findViewById(R.id.sb);
        sb.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                //根据拖动的进度改变音乐播放进度
                int progress = seekBar.getProgress();
                //改变播放进度
                mi.seekTo(progress);
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress,
                                          boolean fromUser) {
            }
        });


        intent = new Intent(this, MusicService.class);
        startService(intent);
        conn = new MyserviceConn();
        bindService(intent, conn, BIND_AUTO_CREATE);
    }

    class MyserviceConn implements ServiceConnection {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mi = (MyMusicInterface) service;

        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            // TODO Auto-generated method stub

        }

    }

    public void play(View v){
        mi.play();
    }
    public void continuePlay(View v){
        mi.continuePlay();
    }
    public void pause(View v){
        mi.pause();
    }
    public void exit(View v){
        unbindService(conn);
        stopService(intent);
    }

    private void requestPremission() {
        PermissionRequest permissRequest;
        String premission[] = {
                Manifest.permission.CALL_PHONE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE,
        };
        permissRequest = new PermissionRequest(this);
        permissRequest.autoShowTip(true);
        permissRequest.permissions(premission);
        permissRequest.execute();
    }
}