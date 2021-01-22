package com.example.flappyw;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;

import java.io.IOException;

public class Soundrecorder extends AppCompatActivity {
    private Button play, stop, record;
    private MediaRecorder myAudioRecorder;
    private String flappsound;
    private static final int REQUEST_AUDIO_PERMISSION_CODE=1;
    public long Starttime = 0;
    long milis;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sound_recorder);

        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},1024);
        }
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},1);
        }
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.RECORD_AUDIO},10);
        }



        play = (Button) findViewById(R.id.play);
        stop = (Button) findViewById(R.id.stop);
        record = (Button) findViewById(R.id.record);
        TextView timeview =(TextView) findViewById(R.id.timebar);
        stop.setEnabled(false);
        play.setEnabled(false);


        myAudioRecorder = new MediaRecorder();
        myAudioRecorder.reset();
        String flappsound = Environment.getExternalStorageDirectory().getAbsolutePath() + "/recording.3gp";
        myAudioRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        myAudioRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        myAudioRecorder.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB);
        myAudioRecorder.setOutputFile(flappsound);
        Constants.flappstring = flappsound;


        Handler timehandler = new Handler();
        Runnable timerunnable = new Runnable() {
            @Override
            public void run() {

                milis = System.currentTimeMillis()-Starttime;
                System.out.println(milis);
                timehandler.postDelayed(this,0);
                if(milis >= 3000){
                    timehandler.removeCallbacks(this);
                }
                ConstraintLayout.LayoutParams lp = (ConstraintLayout.LayoutParams)timeview.getLayoutParams();
                lp.width= (int) (330*milis/Constants.SCREEN_WIDTH);
                timeview.setLayoutParams(lp);

            }
        };



        record.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Starttime = System.currentTimeMillis();
                ConstraintLayout.LayoutParams lp = (ConstraintLayout.LayoutParams)timeview.getLayoutParams();
                lp.width=250;
                timeview.setLayoutParams(lp);

                timehandler.postDelayed(timerunnable,0);
                Constants.sondcheck=1;


                try {
                    myAudioRecorder.prepare();
                    myAudioRecorder.start();
                    Handler recordhandler = new Handler();
                    recordhandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {

                            try {
                                myAudioRecorder.stop();
                            } catch (IllegalStateException e) {
                                e.printStackTrace();
                            }
                            try {
                                myAudioRecorder.release();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            myAudioRecorder = null;
                            record.setEnabled(false);
                            stop.setEnabled(true);
                            play.setEnabled(true);
                            Toast.makeText(getApplicationContext(), "Audio Recorder successfully", Toast.LENGTH_SHORT).show();
                        }
                    },3000);
                } catch (IllegalStateException ise) {
                    // make something ...
                } catch (IOException ioe) {
                    // make something
                }
            }
        });


        stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myAudioRecorder = new MediaRecorder();
                myAudioRecorder.reset();

                String outputFile = Environment.getExternalStorageDirectory().getAbsolutePath() + "/recording.3gp";
                myAudioRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
                myAudioRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
                myAudioRecorder.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB);
                myAudioRecorder.setOutputFile(flappsound);
                ConstraintLayout.LayoutParams lp = (ConstraintLayout.LayoutParams)timeview.getLayoutParams();
                lp.width= (int) (0);
                timeview.setLayoutParams(lp);
                record.setEnabled(true);
                play.setEnabled(false);
                stop.setEnabled(false);

            }
        });


        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MediaPlayer mediaPlayer = new MediaPlayer();

                try {
                    mediaPlayer.reset();
                    mediaPlayer.setDataSource(flappsound);
                    mediaPlayer.setVolume(50,50);
                    mediaPlayer.prepare();
                    mediaPlayer.start();

                    Toast.makeText(getApplicationContext(), "Playing Audio", Toast.LENGTH_LONG).show();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent backmain = new Intent(Soundrecorder.this,MainActivity.class);
        startActivity(backmain);
        finish();
    }



}
