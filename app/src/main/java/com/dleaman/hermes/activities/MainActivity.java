package com.dleaman.hermes.activities;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;

import com.dleaman.hermes.R;
import com.dleaman.hermes.services.TronService;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static com.dleaman.hermes.models.Constants.PERMISSIONS;

public class MainActivity extends AppCompatActivity {
    @BindView(R.id.startButton)
    ImageView mStartButton;
    @BindView(R.id.pauseButton)
    ImageView mPauseButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        /** Bind ButterKnife **/
        ButterKnife.bind(this);

        /** Verify app has permissions to view contacts, etc **/
        checkPermissions();

        /** Initialize view controls and service **/
        initializeControls();
    }

    /**
     * Initializes the controls used by the Activity, such as the mStartButton and mPauseButton.
     * Also detects if the {@link TronService} is currently running and hides the mStartButton if
     * so.
     */
    private void initializeControls() {
        if (isTronServiceOn()) {
            mStartButton.setVisibility(GONE);
            mPauseButton.setVisibility(VISIBLE);
        }

        mStartButton.setOnClickListener(getStartButtonListener());

        mPauseButton.setOnClickListener(getPauseButtonListener());
    }

    // TODO: Account for a user not wanting to give permission for contacts

    /**
     * If the device is using Android >= 23 and the app doesn't have the necessary permissions
     * granted, this method will query the device and ask the user for permission.
     */
    private void checkPermissions() {
        boolean check = false;

        if (Build.VERSION.SDK_INT >= 23) {
            for (String permission : PERMISSIONS) {
                if (checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED)
                    check = true;
            }

            if(check)
                requestPermissions(PERMISSIONS, 0);
        }
    }

    /**
     * Check to see if {@link TronService} is currently running and return a boolean indicating the
     * answer.
     *
     * @return {@link Boolean}
     */
    private boolean isTronServiceOn() {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);

        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (TronService.class.getName().equals(service.service.getClassName())) {
                return true;
            }
        }

        return false;
    }

    /**
     * Build and return the {@link android.view.View.OnClickListener} for the mStartButton that
     * starts the service if it isn't currently running.
     *
     * @return {@link android.view.View.OnClickListener}
     */
    public View.OnClickListener getStartButtonListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isTronServiceOn())
                    startService(new Intent(MainActivity.this, TronService.class));

                mStartButton.setVisibility(GONE);
                mPauseButton.setVisibility(VISIBLE);
            }
        };
    }


    /**
     * Build and return the {@link android.view.View.OnClickListener} for the mPauseButton that
     * stops the service if it is running.
     *
     * @return {@link android.view.View.OnClickListener}
     */
    public View.OnClickListener getPauseButtonListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isTronServiceOn())
                    stopService(new Intent(MainActivity.this, TronService.class));

                mStartButton.setVisibility(VISIBLE);
                mPauseButton.setVisibility(GONE);
            }
        };
    }
}
