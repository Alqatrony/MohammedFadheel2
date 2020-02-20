package com.example.android.mohammedfadheel;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
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
    private SeekBar suraProgressBar;
    private TextView suraTitleLabel;
    private TextView suraCurrentDurationLabel;
    private TextView suraTotalDurationLabel;
    private LinearLayout layoutads;

    private static MediaPlayer mp ;

    private Handler mHandler = new Handler();

    private SuraManager suraManager;
    private Utilities utils;
    private int seekForwardTime = 5000; // 5000 milliseconds
    private int seekBackwardTime = 5000; // 5000 milliseconds
    private int currentSuraIndex = 0;
    private boolean isShuffle = false;
    private boolean isRepeat = false;
    String RecitesSURA="";
    private ArrayList<HashMap<String, String>> surasList = new ArrayList<HashMap<String, String>>();
    @Override
    public void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.play_screen);

        // All player buttons
        btnPlay = (ImageButton) findViewById( R.id.btnPlay);
        btnForward = (ImageButton) findViewById( R.id.btnForward);
        btnBackward = (ImageButton) findViewById( R.id.btnBackward);
        btnNext = (ImageButton) findViewById( R.id.btnNext);
        btnPrevious = (ImageButton) findViewById(R.id.btnPrevios);
        btnRepeat = (ImageButton) findViewById( R.id.btnRepeat);
        btnShuffle = (ImageButton) findViewById( R.id.btnShuffle);
        suraProgressBar = (SeekBar) findViewById( R.id.suraProgressBar);
        suraTitleLabel = (TextView) findViewById( R.id.sura_name);
        suraCurrentDurationLabel = (TextView) findViewById( R.id.suraCurrentDurationLabel);
        suraTotalDurationLabel = (TextView) findViewById( R.id.suraTotalDurationLabel);

        mp = new MediaPlayer();
        processing_actionBar();
        linking_elements();
        suraManager = new SuraManager();
        utils = new Utilities();

        suraProgressBar.setOnSeekBarChangeListener(this); // Important
        mp.setOnCompletionListener(this); // Important


        currentSuraIndex=Integer.parseInt(  RecitesSURA);//-1 ;
        playSura(currentSuraIndex);

        btnPlay.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // check for already playing
                if(mp.isPlaying()){
                    if(mp!=null){
                        mp.pause();
                        // Changing button image to play button
                        btnPlay.setImageResource( R.drawable.btn_play);
                    }
                }else{
                    // Resume song
                    if(mp!=null){
                        mp.start();
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
                int currentPosition = mp.getCurrentPosition();
                // check if seekForward time is lesser than song duration
                if(currentPosition + seekForwardTime <= mp.getDuration()){
                    // forward song
                    mp.seekTo(currentPosition + seekForwardTime);
                }else{
                    // forward to end position
                    mp.seekTo(mp.getDuration());
                }
            }
        });

        btnBackward.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // get current song position
                int currentPosition = mp.getCurrentPosition();
                // check if seekBackward time is greater than 0 sec
                if(currentPosition - seekBackwardTime >= 0){
                    // forward song
                    mp.seekTo(currentPosition - seekBackwardTime);
                }else{
                    // backward to starting position
                    mp.seekTo(0);
                }

            }
        });

        btnNext.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // check if next sura is there or not
                if(currentSuraIndex < (surasList.size() - 1)){
                    playSura(currentSuraIndex + 1);
                    currentSuraIndex = currentSuraIndex + 1;
                }else{
                    // play first sura
                    playSura(0);
                    currentSuraIndex = 0;
                }

            }
        });


        btnPrevious.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                if(currentSuraIndex > 0){
                    playSura(currentSuraIndex - 1);
                    currentSuraIndex = currentSuraIndex - 1;
                }else{
                    // play last song
                    playSura(surasList.size() - 1);
                    currentSuraIndex = surasList.size() - 1;
                }

            }
        });


        btnRepeat.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View arg0) {
                if(isRepeat){
                    isRepeat = false;
                    Toast.makeText(getApplicationContext(), "Repeat is OFF", Toast.LENGTH_SHORT).show();
                    btnRepeat.setImageResource( R.drawable.btn_repeat);
                }else{
                    // make repeat to true
                    isRepeat = true;
                    Toast.makeText(getApplicationContext(), "Repeat is ON", Toast.LENGTH_SHORT).show();
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_play_screen, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.gbackmenu) {
            this.finish();
        }



        return super.onOptionsItemSelected(item);
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK )
        {
            if(mp.isPlaying())
                if(mp!=null)
                    mp.pause();

            this.finish();
        }

        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onActivityResult(int requestCode,
                                    int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == 100){
            currentSuraIndex = data.getExtras().getInt("suraIndex");
            // play selected sura
            playSura(currentSuraIndex);
        }

    }

    public void  playSura(int songIndex){
        // Play song
        try {
            mp.reset();
            mp.setDataSource(surasList.get(songIndex).get("songPath"));
            mp.prepare();
            mp.start();
            // Displaying Song title
            String songTitle = surasList.get(songIndex).get("songTitle");
            suraTitleLabel.setText(songTitle);

            // Changing Button Image to pause image
            btnPlay.setImageResource( R.drawable.btn_pause);

            // set Progress bar values
            suraProgressBar.setProgress(0);
            suraProgressBar.setMax(100);

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
        mHandler.postDelayed(mUpdateTimeTask, 100);
    }

    private Runnable mUpdateTimeTask = new Runnable() {
        public void run() {
            try{
                long totalDuration = mp.getDuration();
                long currentDuration = mp.getCurrentPosition();

                // Displaying Total Duration time
                suraTotalDurationLabel.setText(""+utils.milliSecondsToTimer(totalDuration));
                // Displaying time completed playing
                suraCurrentDurationLabel.setText(""+utils.milliSecondsToTimer(currentDuration));

                // Updating progress bar
                int progress = (int)(utils.getProgressPercentage(currentDuration, totalDuration));
                //Log.d("Progress", ""+progress);
                suraProgressBar.setProgress(progress);

                // Running this thread after 100 milliseconds
                mHandler.postDelayed(this, 100);
                if(currentDuration>=(totalDuration/8)){
                    layoutads.setVisibility(View.VISIBLE);
                }
            }
            catch (Exception ex){}
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
        int totalDuration = mp.getDuration();
        int currentPosition = utils.progressToTimer(seekBar.getProgress(), totalDuration);

        // forward or backward to certain seconds
        mp.seekTo(currentPosition);

        // update timer progress again
        updateProgressBar();
    }

    @Override
    public void onCompletion(MediaPlayer arg0) {

        // check for repeat is ON or OFF
        if(isRepeat){
            // repeat is on play same song again
            playSura(currentSuraIndex);
        } else if(isShuffle){
            // shuffle is on - play a random song
            Random rand = new Random();
            currentSuraIndex = rand.nextInt((surasList.size() - 1) - 0 + 1) + 0;
            playSura(currentSuraIndex);
        } else{
            // no repeat or shuffle ON - play next song
            if(currentSuraIndex < (surasList.size() - 1)){
                playSura(currentSuraIndex + 1);
                currentSuraIndex = currentSuraIndex + 1;
            }else{
                // play first song
                playSura(0);
                currentSuraIndex = 0;
            }
        }
    }

    @Override
    public void onDestroy(){

        mp.release();
        super.onDestroy();
    }

    @SuppressLint("WrongConstant")
    public void processing_actionBar(){
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.custom_actionbar);

        TextView mTitleTextView = (TextView) findViewById(R.id.title_text);
        mTitleTextView.setText(getIntent().getExtras().getString("name"));
    }

    public void linking_elements(){

    }
}
