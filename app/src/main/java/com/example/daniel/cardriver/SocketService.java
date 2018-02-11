package com.example.daniel.cardriver;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

import java.net.URISyntaxException;

/**
 * Created by daniel on 2017/9/7.
 */

public class SocketService extends Service {

    private Socket mSocket;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        try {
            mSocket = IO.socket("http://203.77.73.5:9797/");
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
        initSocket();
        return super.onStartCommand(intent, flags, startId);
    }




    private void initSocket() {
        mSocket.connect();
        mSocket.on("loginstatus", onNewMessage);
    }

    private Emitter.Listener onNewMessage = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {

            String data=args[0].toString();
            String Cid=MainActivity.Cid;
            if(data=="false"){
                mSocket.emit("add driver", Cid);
            }
        }
    };
}
