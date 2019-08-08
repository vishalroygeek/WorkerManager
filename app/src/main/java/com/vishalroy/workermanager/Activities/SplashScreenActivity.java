package com.vishalroy.workermanager.Activities;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.WindowManager;

import com.google.firebase.auth.FirebaseAuth;
import com.vishalroy.workermanager.R;

public class SplashScreenActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        mAuth = FirebaseAuth.getInstance();


        //Hiding statusBar
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);


        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                startProperActivity();
            }
        }, 2000);

    }

    private void startProperActivity(){
        if (mAuth.getCurrentUser()!=null){
            SplashScreenActivity.this.finish();
            startActivity(new Intent(SplashScreenActivity.this, MainActivity.class));
        }else {
            SplashScreenActivity.this.finish();
            startActivity(new Intent(SplashScreenActivity.this, SignInActivity.class));
        }
    }
}
