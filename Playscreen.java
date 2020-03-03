package com.example.android.mohammedfadheel;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.URLUtil;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.logging.LogRecord;

public class Playscreen extends AppCompatActivity implements MediaPlayer.OnCompletionListener, SeekBar.OnSeekBarChangeListener {
    private ImageButton btnPlay;
    private ImageButton btnForward;
    private ImageButton btnBackward;
    private ImageButton btnNext;
    private ImageButton btnPrevious;

    private ImageButton btnRepeat;
    private ImageButton btnShuffle;
    private SeekBar mSuraProgressBar;
    private TextView suraCurrentDurationLabel;
    private TextView suraTotalDurationLabel;
    ProgressDialog pDialog;

    private static MediaPlayer suraplayer ;

    private Handler mHandler = new Handler();

    private SuraManager suraManager;
    private Utilities utils;
    private int seekForwardTime = 5000; // 5000 milliseconds
    private int seekBackwardTime = 5000; // 5000 milliseconds
    private int currentSongIndex = 0;
    private boolean isShuffle = false;
    private boolean isRepeat = false;
    String SuraName="";
    private ArrayList<HashMap<String, String>> surasList = new ArrayList<HashMap<String, String>>();
    @Override
    public void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.play_screen);
        Bundle b=getIntent().getExtras();
        SuraName = b.getString("name");

        // All player buttons
        btnPlay = (ImageButton) findViewById( R.id.btnPlay);
        btnForward = (ImageButton) findViewById( R.id.btnForward);
        btnBackward = (ImageButton) findViewById( R.id.btnBackward);
        btnNext = (ImageButton) findViewById( R.id.btnNext);
        btnPrevious = (ImageButton) findViewById(R.id.btnPrevios);
        btnRepeat = (ImageButton) findViewById( R.id.btnRepeat);
        btnShuffle = (ImageButton) findViewById( R.id.btnShuffle);
        mSuraProgressBar = (SeekBar) findViewById( R.id.suraProgressBar);
        suraCurrentDurationLabel = (TextView) findViewById( R.id.suraCurrentDurationLabel);
        suraTotalDurationLabel = (TextView) findViewById( R.id.suraTotalDurationLabel);


        suraplayer = new MediaPlayer();
        suraManager = new SuraManager();

        utils = new Utilities();

        mSuraProgressBar.setOnSeekBarChangeListener(this); // Important
        suraplayer.setOnCompletionListener(this); // Important

        surasList = suraManager.getSurasList(SuraName);



        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();
        if (ni == null) { // للتأكد من توفر الانترنت
            Toast.makeText(getApplicationContext(), "للأسف لا يوجد لديك اتصال بالانترنت", Toast.LENGTH_SHORT).show();
        }


        btnPlay.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // check for already playing
                if(suraplayer.isPlaying()){
                    if(suraplayer!=null){
                        suraplayer.pause();
                        // Changing button image to play button
                        btnPlay.setImageResource( R.drawable.btn_play);
                    }
                }else{
                    // Resume song
                    if(suraplayer!=null){
                        suraplayer.start();
                        // Changing button image to pause button
                        btnPlay.setImageResource( R.drawable.btn_pause);
                    }
                }

            }
        });

        btnForward.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // get current song position
                int currentPosition = suraplayer.getCurrentPosition();
                // check if seekForward time is lesser than song duration
                if(currentPosition + seekForwardTime <= suraplayer.getDuration()){
                    // forward song
                    suraplayer.seekTo(currentPosition + seekForwardTime);
                }else{
                    // forward to end position
                    suraplayer.seekTo(suraplayer.getDuration());
                }
            }
        });

        btnBackward.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // get current song position
                int currentPosition = suraplayer.getCurrentPosition();
                // check if seekBackward time is greater than 0 sec
                if(currentPosition - seekBackwardTime >= 0){
                    // forward song
                    suraplayer.seekTo(currentPosition - seekBackwardTime);
                }else{
                    // backward to starting position
                    suraplayer.seekTo(0);
                }

            }
        });

        btnNext.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // check if next sura is there or not
                if(currentSongIndex < (surasList.size() - 1)){
                    playSura(currentSongIndex + 1);
                    currentSongIndex = currentSongIndex + 1;
                }else{
                    // play first sura
                    playSura(0);
                    currentSongIndex = 0;
                }

            }
        });


        btnPrevious.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                if(currentSongIndex > 0){
                    playSura(currentSongIndex - 1);
                    currentSongIndex = currentSongIndex - 1;
                }else{
                    // play last song
                    playSura(surasList.size() - 1);
                    currentSongIndex = surasList.size() - 1;
                }

            }
        });


        btnRepeat.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View arg0) {
                if(isRepeat){
                    isRepeat = false;
                    Toast.makeText(getApplicationContext(), "إيقاف تكرار السورة", Toast.LENGTH_SHORT).show();
                    btnRepeat.setImageResource( R.drawable.btn_repeat);
                }else{
                    // make repeat to true
                    isRepeat = true;
                    Toast.makeText(getApplicationContext(), "تكرار السورة قيد التشغيل", Toast.LENGTH_SHORT).show();
                    // make shuffle to false
                    isShuffle = false;
                    btnRepeat.setImageResource( R.drawable.btn_repeat_focused);
                    btnShuffle.setImageResource( R.drawable.btn_shuffle);
                }
            }
        });

        btnShuffle.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View arg0) {
                if(isShuffle){
                    isShuffle = false;
                    Toast.makeText(getApplicationContext(), "Shuffle is OFF", Toast.LENGTH_SHORT).show();
                    btnShuffle.setImageResource( R.drawable.btn_shuffle);
                }else{
                    // make repeat to true
                    isShuffle= true;
                    Toast.makeText(getApplicationContext(), "Shuffle is ON", Toast.LENGTH_SHORT).show();
                    // make shuffle to false
                    isRepeat = false;
                    btnShuffle.setImageResource( R.drawable.btn_shuffle_focused);
                    btnRepeat.setImageResource( R.drawable.btn_repeat);
                }
            }
        });

    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK )
        {
            if(suraplayer.isPlaying())
                if(suraplayer!=null)
                    suraplayer.pause();

            this.finish();
        }

        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onActivityResult(int requestCode,
                                    int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == 100){
            currentSongIndex = data.getExtras().getInt("url");
            // play selected song
            playSura(currentSongIndex);
        }

    }


    public void  playSura(int suraIndex){
        // Play sura
        try {
            suraplayer.reset();
            suraplayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            suraplayer.setDataSource(getIntent().getExtras().getString("url"));
            suraplayer.prepareAsync();
            suraplayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    mp.start();
                    updateProgressBar();
                    btnPlay.setImageResource(R.drawable.btn_pause);
                }
            });

            btnPlay.setImageResource(R.drawable.btn_pause);

            // set Progress bar values
            mSuraProgressBar.setProgress(0);
            mSuraProgressBar.setMax(1000);

            // Updating progress bar
            updateProgressBar();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void updateProgressBar() {
        mHandler.postDelayed(mUpdateTimeTask, 1000);
    }

    private Runnable mUpdateTimeTask = new Runnable() {
        public void run() {
                long totalDuration = suraplayer.getDuration();
                long currentDuration = suraplayer.getCurrentPosition();

                // Displaying Total Duration time
                suraTotalDurationLabel.setText(""+utils.milliSecondsToTimer(totalDuration));
                // Displaying time completed playing
                suraCurrentDurationLabel.setText(""+utils.milliSecondsToTimer(currentDuration));

                // Updating progress bar
                int progress = (int)(utils.getProgressPercentage(currentDuration, totalDuration));
                //Log.d("Progress", ""+progress);
                mSuraProgressBar.setProgress(progress);

                // Running this thread after 100 milliseconds
                mHandler.postDelayed(this, 1000);
                }
    };

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromTouch) {

    }


    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        // remove message Handler from updating progress bar
        mHandler.removeCallbacks(mUpdateTimeTask);
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        mHandler.removeCallbacks(mUpdateTimeTask);
        int totalDuration = suraplayer.getDuration();
        int currentPosition = utils.progressToTimer(seekBar.getProgress(), totalDuration);

        // forward or backward to certain seconds
        suraplayer.seekTo(currentPosition);

        // update timer progress again
        updateProgressBar();
    }

    @Override
    public void onCompletion(MediaPlayer arg0) {

        // check for repeat is ON or OFF
        if(isRepeat){
            // repeat is on play same song again
            playSura(currentSongIndex);
        } else if(isShuffle){
            // shuffle is on - play a random song
            Random rand = new Random();
            currentSongIndex = rand.nextInt((surasList.size() - 1) - 0 + 1) + 0;
            playSura(currentSongIndex);
        } else{
            btnPlay.setImageResource(R.drawable.btn_play);
            }
    }

    @Override
    public void onDestroy(){
        suraplayer.release();
        super.onDestroy();
    }
}
