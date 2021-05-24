package me.keepalive.demo;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.view.View;

import me.keepalive.demo.em.One;

public class MainActivity extends AppCompatActivity {
    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContext=getApplicationContext();
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn1:
                One.launchAlarm(mContext);
                break;
            case R.id.btn2:
                break;
            case R.id.btn3:
                break;
            default:
                break;
        }
    }
}