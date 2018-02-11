package com.example.daniel.cardriver;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.github.nkzawa.socketio.client.Socket;

/**
 * Created by daniel on 2017/8/6.
 */

public class starfg extends Fragment implements View.OnClickListener {
    private String Cid;
    private Socket mSocket;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.starfg, container, false);
        /*Bundle bundle = getArguments();
        Cid=bundle.getString("Cid");*/

    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initview(view);
        socketapplication app = (socketapplication) getActivity().getApplication();
        mSocket = app.getSocket();
    }

    private void initview(View view){
        Button mcarbtn=(Button) view.findViewById(R.id.btncar);
        Button mrecordbtn=(Button) view.findViewById(R.id.btnrecord);
        Button msetbtn=(Button) view.findViewById(R.id.btnset);
        Button mloggout=(Button)view.findViewById(R.id.btnloggout);
        mcarbtn.setOnClickListener(this);
        mrecordbtn.setOnClickListener(this);
        msetbtn.setOnClickListener(this);
        mloggout.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btnset:
                Intent intent = new Intent();
                intent.setClass(getActivity(), settingpreference.class);
                startActivity(intent);
                break;
            case R.id.btncar:
                Context context;
                context = getActivity().getApplicationContext();
                Intent DriverMap = new Intent(context, DriverMapsActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("Cid", Cid);
                DriverMap.putExtras(bundle);
                startActivity(DriverMap);
                /*Intent intentservice = new Intent(getActivity(), SocketService.class);
                context.startService(intentservice);*/
                break;
            case R.id.btnloggout:
                MainActivity.metxt1.setText("");
                MainActivity.metxt2.setText("");
                MainActivity.get.edit().clear().commit();
                mSocket.disconnect();
                mSocket.off();
                DriverMapsActivity.updatelocation=false;
                Intent logout = new Intent();
                logout.setClass(getActivity(), MainActivity.class);
                startActivity(logout);
                break;
        }
    }
}
