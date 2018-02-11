package com.example.daniel.cardriver;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

/**
 * Created by daniel on 2017/9/6.
 */

public class testcheckbox extends AppCompatActivity {

    private TextView mtest;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.testcheckbox);

        mtest=(TextView)findViewById(R.id.txttest);
        Bundle bundle=this.getIntent().getExtras();
        String test=bundle.getString("Test");
        mtest.setText(test);
    }
}
