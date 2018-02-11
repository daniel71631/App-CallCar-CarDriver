package com.example.daniel.cardriver;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;

public class changemailActivity extends AppCompatActivity {

    private Toolbar mtoolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_changemail);

        mtoolbar=(Toolbar)findViewById(R.id.tbar);
        mtoolbar.setTitle("更改信箱");
        mtoolbar.setNavigationIcon(R.drawable.ic_chevron_left_black_24dp);
        mtoolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(changemailActivity.this,settingpreference.class));
            }
        });


    }
}
