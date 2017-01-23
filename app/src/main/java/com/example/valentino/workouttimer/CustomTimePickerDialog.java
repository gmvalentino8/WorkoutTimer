package com.example.valentino.workouttimer;

import android.app.AlertDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.graphics.Color;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.TimePicker;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Valentino on 1/24/17.
 */

public class CustomTimePickerDialog extends TimePickerDialog {
    private final static int TIME_PICKER_INTERVAL = 5;
    private TimePicker mTimePicker;
    private final OnTimeSetListener mTimeSetListener;

    private int initialMinutes;
    private int initialSeconds;

    public CustomTimePickerDialog(Context context, OnTimeSetListener listener,
                                  int minutes, int seconds) {
        super(context, AlertDialog.THEME_HOLO_LIGHT, listener, minutes,
                seconds / TIME_PICKER_INTERVAL, true);
        initialMinutes = minutes;
        initialSeconds = seconds;
        mTimeSetListener = listener;
    }

    @Override
    public void updateTime(int hourOfDay, int minuteOfHour) {
        mTimePicker.setCurrentHour(hourOfDay);
        mTimePicker.setCurrentMinute(minuteOfHour / TIME_PICKER_INTERVAL);
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        switch (which) {
            case BUTTON_POSITIVE:
                if(mTimePicker.getCurrentHour() == 0 && mTimePicker.getCurrentMinute() == 0) {
                    cancel();
                    break;
                }
                if (mTimeSetListener != null) {
                    mTimeSetListener.onTimeSet(mTimePicker, mTimePicker.getCurrentHour(),
                            mTimePicker.getCurrentMinute() * TIME_PICKER_INTERVAL);
                }
                break;
            case BUTTON_NEGATIVE:
                cancel();
                break;
        }
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        try {
            Class<?> classForid = Class.forName("com.android.internal.R$id");
            Field timePickerField = classForid.getField("timePicker");
            mTimePicker = (TimePicker) findViewById(timePickerField.getInt(null));
            mTimePicker.setScaleY(2.0f);
            mTimePicker.setScaleX(2.0f);

            Field minutesField = classForid.getField("hour");
            int maxMinutes = 60;
            NumberPicker mMinutesSpinner = (NumberPicker) mTimePicker.findViewById(minutesField.getInt(null));
            mMinutesSpinner.setMinValue(0);
            mMinutesSpinner.setMaxValue(maxMinutes);
            List<String> minutesDisplayedValues = new ArrayList<String>();
            for (int i = 0; i <= maxMinutes; i++) {
                minutesDisplayedValues.add(String.format("%02d", i));
            }
            mMinutesSpinner.setDisplayedValues(minutesDisplayedValues.toArray(new String[0]));
            mMinutesSpinner.setValue(initialMinutes);

            Field secondsField = classForid.getField("minute");
            NumberPicker mSecondsSpinner = (NumberPicker) mTimePicker
                    .findViewById(secondsField.getInt(null));
            mSecondsSpinner.setMinValue(0);
            mSecondsSpinner.setMaxValue((60 / TIME_PICKER_INTERVAL) - 1);
            List<String> secondsDisplayedValues = new ArrayList<>();
            for (int i = 0; i < 60; i += TIME_PICKER_INTERVAL) {
                secondsDisplayedValues.add(String.format("%02d", i));
            }
            mSecondsSpinner.setDisplayedValues(secondsDisplayedValues
                    .toArray(new String[secondsDisplayedValues.size()]));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
