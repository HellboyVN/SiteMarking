package com.lab411.com.sitemarking;

import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.provider.Settings;
import android.support.annotation.Nullable;

/**
 * Created by callo_000 on 6/1/2017.
 */

public class MyService extends Service {
    MediaPlayer player;
    @Nullable
    @Override

    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        player.stop();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        AudioManager mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        ComponentName rec = new ComponentName(getPackageName(),
                ShakeListener.class.getName());
        mAudioManager.registerMediaButtonEventReceiver(rec);
        player= MediaPlayer.create(this, Settings.System.DEFAULT_NOTIFICATION_URI);
        player.setLooping(true);
        player.start();
        return START_STICKY;
    }
}
