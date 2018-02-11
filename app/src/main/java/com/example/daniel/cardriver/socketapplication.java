package com.example.daniel.cardriver;

import android.app.Application;

import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

import java.net.URISyntaxException;

/**
 * Created by daniel on 2017/8/25.
 */

public class socketapplication extends Application {
    private Socket mSocket;
    IO.Options options=new IO.Options();
    {
        try {
            options.forceNew=false;
            mSocket = IO.socket("http://203.77.73.5:9797/",options);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }
    public Socket getSocket() {
        return mSocket;
    }
}
