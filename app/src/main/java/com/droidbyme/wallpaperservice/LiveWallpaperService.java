package com.droidbyme.wallpaperservice;

import android.app.WallpaperManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Build;
import android.service.wallpaper.WallpaperService;
import android.util.Log;
import android.view.SurfaceHolder;
import android.widget.Toast;

import java.io.IOException;

public class LiveWallpaperService extends WallpaperService {

    private DatabaseHandler handler;

    @Override
    public Engine onCreateEngine() {
        return new WallpaperEngine(this);
    }

    private class WallpaperEngine extends Engine implements SurfaceHolder.Callback {

        MediaPlayer mediaPlayer = null;
        SurfaceHolder holder = null;

        boolean wc = false;
        boolean bfz = false;
        String dz = "";
        int i = 0, ij = 0;

        public WallpaperEngine(LiveWallpaperService liveWallpaperService) {
            handler = new DatabaseHandler(getApplicationContext());
        }

        @Override
        public void onCreate(SurfaceHolder surfaceHolder) {
            super.onCreate(surfaceHolder);
            setTouchEventsEnabled(false);
            surfaceHolder.addCallback(this);
        }

        @Override
        public void onDestroy() {
            if (mediaPlayer != null) {
                mediaPlayer.reset();
                mediaPlayer.release();
                mediaPlayer = null;
            }
            wc = false;
            super.onDestroy();
        }

        @Override
        public void surfaceCreated(SurfaceHolder surfaceHolder) {
            holder = surfaceHolder;
            super.onSurfaceCreated(surfaceHolder);
            start();
        }

        public void start() {
            if (mediaPlayer != null) {
                mediaPlayer.reset();
                mediaPlayer.release();
                mediaPlayer = null;
                wc = false;
                bfz = false;
            }

            mediaPlayer = new MediaPlayer();
            mediaPlayer.setAudioStreamType(3);
            mediaPlayer.setLooping(true);
            mediaPlayer.setSurface(holder.getSurface());

            try {
                //dz = PrefUtils.getPath(getApplicationContext());
                dz = handler.getPath();
                Log.e("dz_path", dz);
                mediaPlayer.setDataSource(dz);
                mediaPlayer.setVolume(0f, 0f);
                mediaPlayer.prepare();
                mediaPlayer.start();

                wc = true;
                bfz = true;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
            super.onSurfaceDestroyed(surfaceHolder);
        }

        @Override
        public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {

        }


        @Override
        public void onVisibilityChanged(boolean visible) {
            super.onVisibilityChanged(visible);
            if (visible)
            //如果可见
            {
                if (wc && !bfz) {
                    if (i == 1) {
                        start();
                        i = 0;
                    } else {
                        bfz = true;
                        mediaPlayer.start();
                    }
                }
                //String xr2 = PrefUtils.getPath(getApplicationContext());
                String xr2 = handler.getPath();
                if (!xr2.equals(dz)) {
                    start();
                    Toast.makeText(LiveWallpaperService.this, "Success", Toast.LENGTH_SHORT).show();
                    PrefUtils.setPath(getApplicationContext(), "");
                }
                //
            } else {
                if (wc && bfz) {
                    bfz = false;
                    mediaPlayer.pause();
                }
            }
        }
    }


    public static void setToWallpaper(Context context) {
        Intent intent = new Intent(WallpaperManager.ACTION_CHANGE_LIVE_WALLPAPER);
        intent.putExtra(WallpaperManager.EXTRA_LIVE_WALLPAPER_COMPONENT, new ComponentName(context, LiveWallpaperService.class));
        intent.putExtra("SET_LOCKSCREEN_WALLPAPER", true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            intent.addFlags(WallpaperManager.FLAG_LOCK);
        }
        try {
            context.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
            intent = new Intent();
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setAction(WallpaperManager.ACTION_LIVE_WALLPAPER_CHOOSER);
            try {
                context.startActivity(intent);
            } catch (Exception e1) {
                e1.printStackTrace();
                Toast.makeText(context, "wallpaper component missing", Toast.LENGTH_LONG).show();
            }
        }
    }
}
