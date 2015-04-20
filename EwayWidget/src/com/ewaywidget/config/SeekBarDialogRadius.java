package com.ewaywidget.config;

import android.app.AlertDialog;
import android.content.Context;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;

import com.ewaywidget.R;
import com.gc.materialdesign.views.Slider;

public class SeekBarDialogRadius extends DialogPreference implements Slider.OnValueChangedListener {

    private static final String PREFERENCE_NS = "http://schemas.android.com/apk/res/com.mnm.seekbarpreference";
    private static final String ANDROID_NS = "http://schemas.android.com/apk/res/android";
    private static final String ATTR_DEFAULT_VALUE = "defaultValue";
    private static final String ATTR_MIN_VALUE = "minValue";
    private static final String ATTR_MAX_VALUE = "maxValue";
    private static final int DEFAULT_CURRENT_VALUE = 250;
    private static final int DEFAULT_MIN_VALUE = 100;
    private static final int DEFAULT_MAX_VALUE = 1000;
    private final int mDefaultValue;
    private final int mMaxValue;
    private final int mMinValue;
    private int mCurrentValue;
    private Slider mSeekBar;
    private TextView mValueText;


    public SeekBarDialogRadius(Context context, AttributeSet attrs) {
        super(context, attrs);
        mMinValue = attrs.getAttributeIntValue(PREFERENCE_NS, ATTR_MIN_VALUE, DEFAULT_MIN_VALUE);
        mMaxValue = attrs.getAttributeIntValue(PREFERENCE_NS, ATTR_MAX_VALUE, DEFAULT_MAX_VALUE);
        mDefaultValue = attrs.getAttributeIntValue(ANDROID_NS, ATTR_DEFAULT_VALUE, DEFAULT_CURRENT_VALUE);
    }

    protected void onPrepareDialogBuilder(AlertDialog.Builder builder) {
        super.onPrepareDialogBuilder(builder);

        builder.setNegativeButton(null, null);
        builder.setPositiveButton(null, null);
        builder.setTitle(null);
    }

    @Override
    protected View onCreateDialogView() {
        // Get current value from preferences
        mCurrentValue = getPersistedInt(mDefaultValue);

        // Inflate layout
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.dialog_pref_radius, null);

        // Setup SeekBar
        mSeekBar = (Slider) view.findViewById(R.id.seek_bar_pref_nearby_radius);
        mSeekBar.setMax(mMaxValue - mMinValue);
        mSeekBar.setValue(mCurrentValue - mMinValue);
        mSeekBar.setOnValueChangedListener(this);
        // Setup text label for current value
        mValueText = (TextView) view.findViewById(R.id.summary_pref_nearby_radius);
        mValueText.setText(Integer.toString(mCurrentValue) + " m");

        return view;
    }

    @Override
    protected void onDialogClosed(boolean positiveResult) {
        super.onDialogClosed(positiveResult);

        // Return if change was cancelled
        if (!positiveResult) {
            persistInt(mCurrentValue);
        }

        // Persist current value if needed
        if (shouldPersist()) {
            persistInt(mCurrentValue);
        }

        // Notify activity about changes (to update preference summary line)
        notifyChanged();
    }

    @Override
    public CharSequence getSummary() {
        // Format summary string with current value
        String summary = super.getSummary().toString();
        int value = getPersistedInt(mDefaultValue);
        return String.format(summary, value);
    }

    public void onStartTrackingTouch(SeekBar seek) {
        // Not used
    }

    public void onStopTrackingTouch(SeekBar seek) {
        // Not used
    }


    @Override
    public void onValueChanged(int value) {
        mCurrentValue = value + mMinValue;
        mValueText.setText(Integer.toString(mCurrentValue) + " m");

    }
}
