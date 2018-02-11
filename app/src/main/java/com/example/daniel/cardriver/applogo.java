package com.example.daniel.cardriver;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.github.nkzawa.socketio.client.Socket;

/**
 * Created by daniel on 2017/8/9.
 */

public class applogo extends AppCompatActivity {

    private Socket mSocket;
    private TextView mtxtTitle;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.applogo);

        socketapplication app = (socketapplication) getApplication();
        mSocket = app.getSocket();

        mtxtTitle=(TextView)findViewById(R.id.txttitle);
        mtxtTitle.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/segoescb.ttf"));


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent().setClass(applogo.this, MainActivity.class));
            }
        }, 3000);


    }

}
