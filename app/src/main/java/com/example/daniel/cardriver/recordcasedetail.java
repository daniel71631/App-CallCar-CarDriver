package com.example.daniel.cardriver;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.github.nkzawa.socketio.client.Socket;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by daniel on 2017/9/14.
 */

public class recordcasedetail extends AppCompatActivity {

    private String StartTime, EndTime, CaseFares, ConfrimCid;
    private TextView mStart, mEnd;
    private EditText mCaseFares;
    private Socket mSocket;
    private Button mbtnCheck;
    public final static String TAG = recordcasedetail.class.getName();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recordcasedetail);

        socketapplication app = (socketapplication) getApplication();
        mSocket = app.getSocket();

        Bundle fromDriverMap2=this.getIntent().getExtras();
        StartTime=fromDriverMap2.getString("StartTime");
        EndTime=fromDriverMap2.getString("EndTime");
        ConfrimCid=fromDriverMap2.getString("ConfirmCid");
        Log.d(TAG, ConfrimCid);

        mStart=(TextView)findViewById(R.id.txtStartTime);
        mStart.setText(StartTime);
        mEnd=(TextView)findViewById(R.id.txtEndTime);
        mEnd.setText(EndTime);

        mbtnCheck=(Button)findViewById(R.id.btncheckcase);
        mbtnCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCaseFares=(EditText)findViewById(R.id.etxtCaseFares);
                CaseFares=mCaseFares.getText().toString();
                JSONObject CaseEnd=new JSONObject();
                try {
                    CaseEnd.put("record",DriverMap2.LatLng);
                    CaseEnd.put("ontime", StartTime);
                    CaseEnd.put("offtime", EndTime);
                    CaseEnd.put("cid", ConfrimCid);
                    CaseEnd.put("money", CaseFares);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                mSocket.emit("new record",CaseEnd);
                startActivity(new Intent(recordcasedetail.this, NavigationActivity.class));
            }
        });
    }
}
