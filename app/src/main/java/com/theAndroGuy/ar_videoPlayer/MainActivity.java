package com.theAndroGuy.ar_videoPlayer;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.ar.sceneform.AnchorNode;
import com.google.ar.sceneform.math.Vector3;
import com.google.ar.sceneform.rendering.Color;
import com.google.ar.sceneform.rendering.ExternalTexture;
import com.google.ar.sceneform.rendering.ModelRenderable;
import com.google.ar.sceneform.ux.ArFragment;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

public class MainActivity extends AppCompatActivity {

    private ModelRenderable videoRenderable;
    private float HEIGHT = 0.95f;



    MediaPlayer mediaPlayer;
    Button load,exit;
    TextView play_pause;


    ExternalTexture texture;
    AnchorNode anchorNode;
    ArFragment arFragment;


    TextView refresh,about;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        load=findViewById(R.id.button);
        play_pause=findViewById(R.id.button2);
        exit=findViewById(R.id.button3);

        refresh=findViewById(R.id.textView4);
        about=findViewById(R.id.textView3);



        texture = new ExternalTexture();
        mediaPlayer = MediaPlayer.create(this, R.raw.intro);
        mediaPlayer.setSurface(texture.getSurface());

        ModelRenderable.builder()
                .setSource(this, R.raw.video_screen)
                .build()
                .thenAccept(modelRenderable -> {
                    videoRenderable = modelRenderable;
                    videoRenderable.getMaterial().setExternalTexture("videoTexture", texture);
                    videoRenderable.getMaterial().setFloat4("keyColor", new Color(0.01843f, 1.0f,0.098f));

                });

        arFragment = (ArFragment) getSupportFragmentManager().findFragmentById(R.id.fragment);

        arFragment.setOnTapArPlaneListener((hitResult, plane, motionEvent) -> {
            anchorNode = new AnchorNode(hitResult.createAnchor());


            //play_pause.setVisibility(View.VISIBLE);
            exit.setVisibility(View.INVISIBLE);
            refresh.setVisibility(View.INVISIBLE);


            if(!mediaPlayer.isPlaying()){
                mediaPlayer.start();

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        load.setVisibility(View.VISIBLE);

                    }
                },7000);
                texture.getSurfaceTexture().setOnFrameAvailableListener(surfaceTexture -> {
                    anchorNode.setRenderable(videoRenderable);
                    texture.getSurfaceTexture().setOnFrameAvailableListener(null);
                });
            }else{
                anchorNode.setRenderable(videoRenderable);
            }
            float width = mediaPlayer.getVideoWidth();
            float height = mediaPlayer.getVideoHeight();

            anchorNode.setLocalScale(new Vector3(HEIGHT * (width / height), HEIGHT, 0.95f));
            arFragment.getArSceneView().getScene().addChild(anchorNode);
        });


        load.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                askPermissions();
            }
        });

        play_pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mediaPlayer.isPlaying()){
                    play_pause.setBackgroundResource(R.drawable.ic_baseline_play_circle_outline_24);
                    mediaPlayer.pause();
                    load.setVisibility(View.INVISIBLE);
                    exit.setVisibility(View.VISIBLE);
                }else{
                    play_pause.setBackgroundResource(R.drawable.ic_baseline_pause_circle_outline_24);
                    mediaPlayer.start();
                    exit.setVisibility(View.INVISIBLE);
                    load.setVisibility(View.VISIBLE);
                }
            }
        });
        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog();

            }
        });
        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),MainActivity.class));
            }
        });
        about.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),credits.class));
            }
        });
    }

    private void alertDialog() {
        AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
        builder1.setMessage("Are you sure want to exit ?");
        builder1.setCancelable(true);

        builder1.setPositiveButton(
                "Yes",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        android.os.Process.killProcess(android.os.Process.myPid());
                        System.exit(1);
                    }
                });

        builder1.setNegativeButton(
                "No",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        AlertDialog alert11 = builder1.create();
        alert11.show();
    }

    private void askPermissions() {
        Dexter.withContext(this)
                .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                .withListener(new PermissionListener() {
                    @Override public void onPermissionGranted(PermissionGrantedResponse response)
                    {
                        getVideo();

                    }
                    @Override public void onPermissionDenied(PermissionDeniedResponse response)
                    {

                        Toast.makeText(getApplicationContext(), "Permission Denied", Toast.LENGTH_SHORT).show();
                    }
                    @Override public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token)
                    {
                        token.continuePermissionRequest();
                    }
                }).check();
    }

    private void getVideo() {
        Intent intent = new Intent();
        intent.setType("video/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent,5000);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==5000 && resultCode==RESULT_OK && data != null)
        {
            play_pause.setVisibility(View.VISIBLE);
            Uri videoUri=data.getData();
            if(!mediaPlayer.isPlaying()){
                mediaPlayer.release();
                mediaPlayer=MediaPlayer.create(getApplicationContext(),videoUri);
                mediaPlayer.setSurface(texture.getSurface());
                mediaPlayer.start();
                mediaPlayer.setLooping(true);
                play_pause.setBackgroundResource(R.drawable.ic_baseline_pause_circle_outline_24);
            }else{
                mediaPlayer.release();
                mediaPlayer=MediaPlayer.create(getApplicationContext(),videoUri);
                mediaPlayer.setSurface(texture.getSurface());
                mediaPlayer.start();
                mediaPlayer.setLooping(true);
                play_pause.setBackgroundResource(R.drawable.ic_baseline_pause_circle_outline_24);
            }

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode==1000)
        {
            if(grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
            {
                getVideo();
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(mediaPlayer.isPlaying()){
            mediaPlayer.pause();
            play_pause.setBackgroundResource(R.drawable.ic_baseline_play_circle_outline_24);
            load.setVisibility(View.INVISIBLE);
            exit.setVisibility(View.VISIBLE);
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(mediaPlayer!=null){
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer=null;
        }
    }

}
