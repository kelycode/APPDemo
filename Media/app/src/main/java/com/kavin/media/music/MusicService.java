package com.kavin.media.music;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Message;

import java.util.Timer;
import java.util.TimerTask;

public class MusicService extends Service {

    MediaPlayer player;
    private Timer timer;

    public MusicService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        player = new MediaPlayer();
    }

    public void onDestroy()
    {
        super.onDestroy();
        player.stop();
        player.release();
        player = null;
        if(timer != null)
        {
            timer.cancel();
            timer = null;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return new MusicController();
    }

    class MusicController extends Binder implements MyMusicInterface{

        @Override
        public void play() {
            MusicService.this.player();
        }

        @Override
        public void pause() {
            MusicService.this.pause();
        }

        @Override
        public void continuePlay() {
            MusicService.this.continuePlay();
        }

        @Override
        public void seekTo(int progress) {
            MusicService.this.seekTo(progress);
        }
    }

    public void player()
    {
        //重置
        player.reset();
        try {
            //加载多媒体文件
            player.setDataSource("sdcard/123.mp3");
//			player.setDataSource("http://192.168.13.119:8080/bzj.mp3");
//			player.prepare();
            player.prepareAsync();
//			player.start();
            player.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                //准备完毕时，此方法调用
                @Override
                public void onPrepared(MediaPlayer mp) {
                    player.start();
                    addTimer();
                }
            });
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    public void continuePlay()
    {
        player.start();
    }
    public void pause()
    {
        player.pause();
    }
    public void seekTo(int progress)
    {
        player.seekTo(progress);
    }
    public void addTimer()
    {
        if(timer == null){
            timer = new Timer();
            timer.schedule(new TimerTask() {

                @Override
                public void run() {
                    //获取歌曲总时长
                    int duration = player.getDuration();
                    //获取歌曲当前播放进度
                    int currentPosition= player.getCurrentPosition();

                    Message msg = Activity_music.handler.obtainMessage();
                    //把进度封装至消息对象中
                    Bundle bundle = new Bundle();
                    bundle.putInt("duration", duration);
                    bundle.putInt("currentPosition", currentPosition);
                    msg.setData(bundle);
                    Activity_music.handler.sendMessage(msg);

                }
                //开始计时任务后的5毫秒，第一次执行run方法，以后每500毫秒执行一次
            }, 5, 500);
        }
    }




}