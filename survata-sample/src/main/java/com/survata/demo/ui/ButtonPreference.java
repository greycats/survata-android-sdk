package com.survata.demo.ui;

import android.content.Context;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceViewHolder;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;

import com.survata.demo.R;

public class ButtonPreference extends Preference {

    private Button mButton;
    private View.OnClickListener mOnClickListener;

    public ButtonPreference(Context context) {
        this(context, null);
    }

    public ButtonPreference(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ButtonPreference(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        setLayoutResource(R.layout.button_preference);
    }

    @Override
    public void onBindViewHolder(PreferenceViewHolder holder) {
        super.onBindViewHolder(holder);

        mButton = (Button) holder.findViewById(R.id.button);
        String key = getKey();
        mButton.setText(key);
        mButton.setOnClickListener(mOnClickListener);
    }

    public void setButtonClick(View.OnClickListener onClickListener) {
        mOnClickListener = onClickListener;
    }
}
