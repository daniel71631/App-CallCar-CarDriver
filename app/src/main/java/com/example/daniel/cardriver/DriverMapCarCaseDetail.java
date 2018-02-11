package com.example.daniel.cardriver;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.Socket;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by daniel on 2017/9/18.
 */

public class DriverMapCarCaseDetail extends AppCompatActivity {

    private TextView mtxtClientName, mtxtClientLocation, mtxtClientDestination;
    private Button mYES, mNO;
    private EditText mFares;
    private Socket mSocket;
    private String CID, Driverconfirm, MapClientName,MapClientLocation,MapClientDestination,MapCaseCID,MapCaseStyle ;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.drivermapcarcasedetail);

        socketapplication app = (socketapplication) getApplication();
        mSocket = app.getSocket();

        Bundle fromCaseQuickMapCar=this.getIntent().getExtras();
        MapClientName=fromCaseQuickMapCar.getString("MapClientName");
        MapClientLocation=fromCaseQuickMapCar.getString("MapClientLocation");
        MapClientDestination=fromCaseQuickMapCar.getString("MapClientDestination");
        MapCaseCID=fromCaseQuickMapCar.getString("MapcaseCid");
        MapCaseStyle=fromCaseQuickMapCar.getString("CaseStyle");

        mtxtClientName=(TextView)findViewById(R.id.txtMapCarClientName);
        mtxtClientLocation=(TextView)findViewById(R.id.txtMapCarLocation);
        mtxtClientDestination=(TextView)findViewById(R.id.txtMapCarClientDestination);
        mtxtClientName.setText(MapClientName);
        mtxtClientLocation.setText(MapClientLocation);
        mtxtClientDestination.setText(MapClientDestination);

        mFares=(EditText)findViewById(R.id.etxtMapCarCaseFares);


        mYES=(Button)findViewById(R.id.btnMapCarDriverYes);
        mYES.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String CaseFares=mFares.getText().toString();
                DriverMapsActivity.updatelocation=false;
                JSONObject MapCarYes=new JSONObject();
                try {
                    MapCarYes.put("cid", MapCaseCID);
                    MapCarYes.put("money", CaseFares);
                    MapCarYes.put("agree", true);
                    mSocket.off("map request");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                mSocket.emit("map response", MapCarYes);
                mSocket.emit("driver clear location");
                mSocket.once("map confirm", Clientconfirm);
            }
        });
    }

    private Emitter.Listener Clientconfirm = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            JSONObject data=(JSONObject) args[0];
            try {
                Driverconfirm=data.getString("agree");
                CID=data.getString("cid");
            } catch (JSONException e) {
                e.printStackTrace();
            }


            if(Driverconfirm.equals("true")){
                Bundle toMapRoute=new Bundle();
                toMapRoute.putString("MapClientLocation", MapClientLocation);
                toMapRoute.putString("MapCid", MapCaseCID);
                toMapRoute.putString("MapClientDestination", MapClientDestination);
                toMapRoute.putString("MapCaseStyle",MapCaseStyle);
                Intent intent = new Intent();
                intent.setClass(DriverMapCarCaseDetail.this, GoogleMapRoute.class);
                intent.putExtras(toMapRoute);
                startActivity(intent);
            }
        }
    };
}
