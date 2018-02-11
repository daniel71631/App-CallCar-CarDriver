package com.example.daniel.cardriver;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.github.nkzawa.socketio.client.Socket;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by daniel on 2017/9/9.
 */

public class DriverCaseDetail extends AppCompatActivity {

    private TextView mClientName, mClientLocation;
    private String ClientAddress;
    private Button mbtnYes, mbtnNo;
    private Socket mSocket;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.drivercasedetail);

        socketapplication app = (socketapplication) getApplication();
        mSocket = app.getSocket();

        mClientName=(TextView)findViewById(R.id.txtCaseClientname);
        mClientLocation=(TextView)findViewById(R.id.txtCaseDetailClientLocation);

        Bundle fromdrivermap=this.getIntent().getExtras();
        String ClientName=fromdrivermap.getString("ClientName");
        final String ClientLocation=fromdrivermap.getString("ClientLocation");
        final String caseCid=fromdrivermap.getString("caseCid");

        mClientName.setText(ClientName);
        mClientLocation.setText(ClientLocation);

        mbtnYes=(Button)findViewById(R.id.btnMapCarDriverYes);
        mbtnYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DriverMapsActivity.updatelocation=false;
                JSONObject Yes=new JSONObject();
                try {
                    Yes.put("cid", caseCid);
                    Yes.put("agree", true);
                    mSocket.off("fastrequest");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                mSocket.emit("autocall response", Yes);
                mSocket.emit("driver clear location");
                Bundle tomaproute=new Bundle();
                tomaproute.putString("Clientlocation", ClientLocation);
                tomaproute.putString("Cid", caseCid);
                Intent intent = new Intent();
                intent.setClass(DriverCaseDetail.this, GoogleMapRoute.class);
                intent.putExtras(tomaproute);
                startActivity(intent);
            }
        });

        mbtnNo=(Button)findViewById(R.id.btnNo);
        mbtnNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                JSONObject No=new JSONObject();
                try {
                    No.put("cid", caseCid);
                    No.put("agree", false);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                mSocket.emit("autocall response", No);
            }
        });
    }
}
