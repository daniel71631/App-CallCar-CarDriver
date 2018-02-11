package com.example.daniel.cardriver;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

/**
 * Created by daniel on 2017/9/8.
 */

public class aboutus extends AppCompatActivity {

    private Toolbar mtoolbar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.aboutus);

        mtoolbar=(Toolbar) findViewById(R.id.tbar);
        mtoolbar.setNavigationIcon(R.drawable.ic_keyboard_arrow_left_black_24dp);
        mtoolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(aboutus.this,settingpreference.class));
            }
        });
    }
}
