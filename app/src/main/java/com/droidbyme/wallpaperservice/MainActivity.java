package com.droidbyme.wallpaperservice;

import android.Manifest;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.VideoView;

import com.gun0912.tedpermission.PermissionListener;

import java.io.File;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int REQUEST_TAKE_GALLERY_VIDEO = 1000;
    private String path;
    private String videoPath;
    private android.widget.Button btnPick;
    private android.widget.Button btnSet;
    private android.widget.VideoView videoView;

    private DatabaseHandler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.videoView = (VideoView) findViewById(R.id.videoView);
        this.btnSet = (Button) findViewById(R.id.btnSet);
        this.btnPick = (Button) findViewById(R.id.btnPick);

        btnPick.setOnClickListener(this);
        btnSet.setOnClickListener(this);

        handler = new DatabaseHandler(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnPick:
                PrefUtils.setPath(getApplicationContext(), "");
                videoPath = "";
                Functions.setPermission(MainActivity.this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                Manifest.permission.SET_WALLPAPER},
                        new PermissionListener() {
                            @Override
                            public void onPermissionGranted() {
                                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
                                intent.setType("video/*");
                                startActivityForResult(intent, REQUEST_TAKE_GALLERY_VIDEO);
                            }

                            @Override
                            public void onPermissionDenied(ArrayList<String> deniedPermissions) {

                            }
                        });
                break;

            case R.id.btnSet:
                Log.e("click_path", PrefUtils.getPath(getApplicationContext()));
                Log.e("db_path", handler.getPath());
                LiveWallpaperService.setToWallpaper(MainActivity.this);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            Uri selectedMediaUri = data.getData();
            //  Log.e("path", selectedMediaUri.toString());

            File file = new File(selectedMediaUri.getPath());
            //Log.e("file", file.getPath());
            //  Log.e("file_ab", file.getAbsolutePath());
            Log.e("getRealPathFromURI", getRealPathFromURI(selectedMediaUri));

            videoPath = getRealPathFromURI(selectedMediaUri);

            videoView.setVideoURI(selectedMediaUri);
            videoView.start();

            PrefUtils.setPath(getApplicationContext(), videoPath);

            handler.savePath(videoPath);

        }
    }

    public String getRealPathFromURI(Uri contentUri) {
        Cursor cursor = null;
        try {
            String[] proj = {MediaStore.Video.Media.DATA};
            cursor = getContentResolver().query(contentUri, proj, null, null, null);
            assert cursor != null;
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } catch (Exception e) {
            e.printStackTrace();
            return "null";
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

}
