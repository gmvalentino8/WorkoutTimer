package com.example.valentino.workouttimer;

import android.app.TimePickerDialog;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.PlaybackParams;
import android.media.SoundPool;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.TimePicker;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    enum State {
        Started, Paused, Stopped
    }

    TextView WorkoutIntervalTextView;
    TextView RestIntervalTextView;
    Button StartButton;
    Button PauseButton;
    Button StopButton;
    int WorkoutMinutes, WorkoutMinutesCounter, WorkoutSeconds, WorkoutSecondsCounter;
    int RestMinutes, RestMinutesCounter, RestSeconds, RestSecondsCounter;
    State currState = State.Stopped;
    boolean working = true;
    MediaPlayer mediaPlayer;
    SoundPool soundPool;
    int soundId;

    CountDownTimer countDownTimer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        WorkoutIntervalTextView = (TextView) findViewById(R.id.WorkoutIntervalTextView);
        RestIntervalTextView = (TextView) findViewById(R.id.RestIntervalTextView);
        StartButton = (Button) findViewById(R.id.StartButton);
        PauseButton = (Button) findViewById(R.id.PauseButton);
        StopButton = (Button) findViewById(R.id.StopButton);
        WorkoutMinutes = 0;
        WorkoutSeconds = 30;
        RestMinutes = 0;
        RestSeconds = 30;
        updateWorkoutTime(WorkoutMinutes, WorkoutSeconds);
        updateRestTime(RestMinutes, RestSeconds);
        mediaPlayer = MediaPlayer.create(this, R.raw.buzzer);
        soundPool = new SoundPool(1, AudioManager.STREAM_NOTIFICATION, 0);
        soundId = soundPool.load(this, R.raw.buzzer, 1);

        WorkoutIntervalTextView.setOnClickListener(this);
        RestIntervalTextView.setOnClickListener(this);
        StartButton.setOnClickListener(this);
        PauseButton.setOnClickListener(this);
        StopButton.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.WorkoutIntervalTextView:
                new CustomTimePickerDialog(MainActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int minutes, int seconds) {
                        updateWorkoutTime(minutes, seconds);
                    }
                }, WorkoutMinutes, WorkoutSeconds).show();
                break;

            case R.id.RestIntervalTextView:
                new CustomTimePickerDialog(MainActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int minutes, int seconds) {
                        updateRestTime(minutes, seconds);
                    }
                }, RestMinutes, RestSeconds).show();
                break;

            case R.id.StartButton:
                WorkoutIntervalTextView.setClickable(false);
                RestIntervalTextView.setClickable(false);
                PauseButton.setClickable(true);
                StopButton.setClickable(true);
                if (currState == State.Stopped) {
                    countDown(WorkoutMinutes, WorkoutSeconds);
                } else if (currState == State.Paused && working) {
                    countDown(WorkoutMinutesCounter, WorkoutSecondsCounter);
                } else if (currState == State.Paused && !working) {
                    countDown(RestMinutesCounter, RestSecondsCounter);
                }
                currState = State.Started;
                break;

            case R.id.PauseButton:
                currState = State.Paused;
                WorkoutIntervalTextView.setClickable(false);
                RestIntervalTextView.setClickable(false);
                StartButton.setClickable(true);
                StopButton.setClickable(true);
                countDownTimer.cancel();
                break;

            case R.id.StopButton:
                currState = State.Stopped;
                working = true;
                WorkoutIntervalTextView.setClickable(true);
                RestIntervalTextView.setClickable(true);
                StartButton.setClickable(true);
                PauseButton.setClickable(false);
                StopButton.setClickable(true);
                countDownTimer.cancel();
                updateWorkoutTime(WorkoutMinutes, WorkoutSeconds);
                updateRestTime(RestMinutes, RestSeconds);
                break;
        }
    }



    private void countDown(int minutes, int seconds) {
        int countdownTime = (minutes * 60 + seconds) * 1000;

        countDownTimer = new CountDownTimer(countdownTime, 100) {
            int totalSecondsLeft = 0;

            @Override
            public void onTick(long ms) {
                if (Math.round((float) ms / 1000.0f) != totalSecondsLeft)
                {
                    totalSecondsLeft = Math.round((float) ms / 1000.0f);

                    if (totalSecondsLeft == 0) {
                        //mediaPlayer.start();
                        soundPool.play(soundId, 1, 1, 1, 0, 1.1f);
                    }

                    if (working) {
                        WorkoutMinutesCounter = totalSecondsLeft / 60;
                        WorkoutSecondsCounter = totalSecondsLeft - WorkoutMinutesCounter * 60;
                        updateWorkoutTime(WorkoutMinutesCounter, WorkoutSecondsCounter);
                    } else {
                        RestMinutesCounter = totalSecondsLeft / 60;
                        RestSecondsCounter = totalSecondsLeft - WorkoutMinutesCounter * 60;
                        updateRestTime(RestMinutesCounter, RestSecondsCounter);
                    }
                }
            }

            @Override
            public void onFinish() {
                if (working) {
                    countDown(RestMinutes, RestSeconds);
                    working = false;
                    updateWorkoutTime(WorkoutMinutes, WorkoutSeconds);
                } else {
                    countDown(WorkoutMinutes, WorkoutSeconds);
                    working = true;
                    updateRestTime(RestMinutes, RestSeconds);
                }
            }
        }.start();

    }

    private void updateWorkoutTime(int minutes, int seconds) {
        if (currState == State.Stopped) {
            WorkoutMinutes = minutes;
            WorkoutSeconds = seconds;
        }
        String time = String.format("%02d:%02d", minutes, seconds);
        WorkoutIntervalTextView.setText(time);
    }

    private void updateRestTime(int minutes, int seconds) {
        if (currState == State.Stopped) {
            RestMinutes = minutes;
            RestSeconds = seconds;
        }
        String time = String.format("%02d:%02d", minutes, seconds);
        RestIntervalTextView.setText(time);
    }
}
