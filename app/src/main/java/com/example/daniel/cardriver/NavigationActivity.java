package com.example.daniel.cardriver;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

public class NavigationActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private com.github.nkzawa.socketio.client.Socket mSocket;
    private SQLiteQuickMap dbHelper = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        socketapplication app = (socketapplication) getApplication();
        mSocket = app.getSocket();


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //Bundle fromMain=this.getIntent().getExtras();
        String Cid=MainActivity.Cid;

        starfg Fragment=new starfg();
        FragmentManager manager=getSupportFragmentManager();
        //Bundle toStarFg=new Bundle();
        //toStarFg.putString("Cid", Cid);
        //
        // Fragment.setArguments(toStarFg);
        manager.beginTransaction().addToBackStack(null).replace(R.id.frame, new starfg()).addToBackStack(null).commit();

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.navigation, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if (id == R.id.action_settings) {
            startActivity(new Intent(NavigationActivity.this,settingpreference.class));
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

        int id = item.getItemId();

        if(id==R.id.nav_setting){
            startActivity(new Intent(this,settingpreference.class));
        }else if(id==R.id.nav_loggout){
            MainActivity.metxt1.setText("");
            MainActivity.metxt2.setText("");
            MainActivity.get.edit().clear().commit();
            mSocket.disconnect();
            mSocket.off();
            dbHelper.deleteAll();
            DriverMapsActivity.updatelocation=false;
            startActivity(new Intent(this,MainActivity.class));
        }


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
