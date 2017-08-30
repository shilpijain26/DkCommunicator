package com.dk.dkcommunicator;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;


/**
 * Created by dell on 8/16/2017.
 */

public class SplashActivity extends AppCompatActivity{

    private static int SPLASH_TIME_OUT = 2000;
    Intent intent;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedData sharedData = ApplicationClass.get().getSharePref();
        getSupportActionBar().hide();
        setContentView(R.layout.activity_splash);

        if(sharedData.checkData("mobile") && sharedData.checkData("OTP_Verified")){
            intent = new Intent(SplashActivity.this, MainActivity.class);
        }else if(sharedData.checkData("mobile") && !sharedData.checkData("OTP_Verified")){
            intent = new Intent(SplashActivity.this, OTP_Activity.class);
        }else{
            intent = new Intent(SplashActivity.this, LoginActivity.class);
        }

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                // This method will be executed once the timer is over
                // Start your app main activity

                SplashActivity.this.startActivity(intent);
                SplashActivity.this.finish();
                overridePendingTransition(R.anim.slide_out,0);
            }
        }, SPLASH_TIME_OUT);

    }


}
