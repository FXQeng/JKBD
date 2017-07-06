package com.example.administrator.jkbd.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.example.administrator.jkbd.R;

/**
 * Created by Administrator on 2017/6/28.
 */

public class SplashActivity extends AppCompatActivity{
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        mCountDownTimer.start();
    }

    //闪屏2秒跳转到主页
    CountDownTimer mCountDownTimer  = new CountDownTimer(2000,1000) {
     @Override
       public void onTick(long millisUntilFinished) {
     }
      @Override
      public void onFinish() {
          Intent intent = new Intent(SplashActivity.this,MainActivity.class);
          startActivity(intent);
          finish();  //返回退出
      }
  };
}
