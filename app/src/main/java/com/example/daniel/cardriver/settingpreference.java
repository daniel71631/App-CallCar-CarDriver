package com.example.daniel.cardriver;

import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.support.annotation.Nullable;
import android.view.KeyEvent;

/**
 * Created by daniel on 2017/8/16.
 */

public class settingpreference extends PreferenceActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preference);
    }

    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        if(keyCode == KeyEvent.KEYCODE_BACK){
            Intent lastpage = new Intent();
            lastpage = new Intent(settingpreference.this, NavigationActivity.class);
            startActivity(lastpage);
            this.finish();
        }
        return super.onKeyDown(keyCode, event);
    }
}
