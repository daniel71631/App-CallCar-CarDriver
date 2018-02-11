package com.example.daniel.cardriver;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.Socket;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    private Button mbtn1, mbtn2, mbtn3;
    public static EditText metxt1, metxt2;
    private String url="http://203.77.73.5:9797/d_login";
    private TextView mtxt1, mtxt2, mtxtNewTitle;
    private Socket mSocket;
    public static String Cid;
    private SharedPreferences settings;
    private static final String logginemail = "LoginEmail";
    private static final String logginpassword = "LoginPassword";
    private static final String data = "DATA";
    public static SharedPreferences get;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        socketapplication app = (socketapplication) getApplication();
        mSocket = app.getSocket();


        metxt1 = (EditText) findViewById(R.id.etxt1);
        metxt2 = (EditText) findViewById(R.id.etxt2);
        mtxt1 = (TextView) findViewById(R.id.txt);
        mtxt2 = (TextView) findViewById(R.id.txt2);
        mbtn2 = (Button) findViewById(R.id.btn2);
        mtxtNewTitle=(TextView)findViewById(R.id.txtNewTitle);
        mtxtNewTitle.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/segoescb.ttf"));
        final RequestQueue queue = Volley.newRequestQueue(MainActivity.this);

        get=getSharedPreferences(data,0);
        metxt1.setText(get.getString(logginemail, ""));
        metxt2.setText(get.getString(logginpassword, ""));
        if(!metxt1.getText().toString().equals("") && !metxt2.getText().toString().equals("") ){
            HashMap<String, String> params = new HashMap<String, String>();
            final String useremail = metxt1.getText().toString().trim();
            final String password = metxt2.getText().toString().trim();
            params.put("user", useremail);
            params.put("pw", password);
            JsonObjectRequest stringRequest = new JsonObjectRequest(url, new JSONObject(params),
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                Cid = response.getString("did");
                                //JSONObject js=new JSONObject(response);
                                if (response.getString("did") != null) {
                                    mSocket.on("loginstatus", onNewMessage);
                                    //mSocket.on("repeat login", repeatlogin);
                                    mSocket.connect();
                                    Toast.makeText(getApplicationContext(), "登入成功", Toast.LENGTH_SHORT).show();
                                    saveData();
                                    Intent intent = new Intent();
                                    intent.setClass(MainActivity.this, NavigationActivity.class);
                                    //intent.putExtras(intent);
                                    startActivity(intent);
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(getApplicationContext(), "登入失敗", Toast.LENGTH_LONG).show();
                }
            }) {
            };
            queue.add(stringRequest);
        }

        mtxt1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, forgetpasswordActivity.class));
            }
        });


        mbtn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, RegisterActivity.class));
            }
        });

        mbtn3 = (Button) findViewById(R.id.btn1);
        mbtn3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, NavigationActivity.class));
            }
        });

        final String email = metxt1.getText().toString().trim();
        final String emailPattern = "\\w+([-+.]\\w+)*" + "\\@"
                + "\\w+([-.]\\w+)*" + "\\." + "\\w+([-.]\\w+)*";
        mbtn1 = (Button) findViewById(R.id.btnRig);
            /*@Override
            public void onClick(View view) {
                mSocket.connect();
                Log.e("CONNECTED", "SUCCESS");
                mSocket.emit("send message", "message");
            }
        });*/
        mbtn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (metxt1.getText().toString().equals("") && !metxt2.getText().toString().equals("")) {
                    Toast.makeText(view.getContext(),
                            "帳號欄位是空的", Toast.LENGTH_SHORT).show();
                } else if (metxt2.getText().toString().equals("") && !metxt1.getText().toString().equals("")) {
                    Toast.makeText(view.getContext(),
                            "密碼欄位是空的", Toast.LENGTH_SHORT).show();
                } else if (metxt1.getText().toString().equals("") && metxt2.getText().toString().equals("")) {
                    Toast.makeText(view.getContext(),
                            "帳號和密碼欄位都是空的", Toast.LENGTH_SHORT).show();
                } else if (!email.matches(emailPattern) && metxt1.getText().toString().equals("")) {
                    Toast.makeText(getApplicationContext(), "無效的信箱地址 請重新輸入", Toast.LENGTH_SHORT).show();
                } else {
                    if (metxt2.getText().toString().length() < 6) {
                        Toast.makeText(getApplicationContext(), "無效的密碼 請重新輸入", Toast.LENGTH_SHORT).show();
                    } else {
                                HashMap<String, String> params = new HashMap<String, String>();
                                final String useremail = metxt1.getText().toString().trim();
                                final String password = metxt2.getText().toString().trim();
                                params.put("user", useremail);
                                params.put("pw", password);
                                JsonObjectRequest stringRequest = new JsonObjectRequest(url, new JSONObject(params),
                                        new Response.Listener<JSONObject>() {
                                            @Override
                                            public void onResponse(JSONObject response) {
                                                try {
                                                    Cid = response.getString("did");
                                                    //JSONObject js=new JSONObject(response);
                                                    if (response.getString("did") != null) {

                                                        mSocket.on("loginstatus", onNewMessage);
                                                        //mSocket.on("repeat login", repeatlogin);
                                                        mSocket.connect();
                                                        Toast.makeText(getApplicationContext(), "登入成功", Toast.LENGTH_SHORT).show();
                                                        saveData();
                                                        Intent intent = new Intent();
                                                        intent.setClass(MainActivity.this, NavigationActivity.class);
                                                        //intent.putExtras(intent);
                                                        startActivity(intent);
                                                    }
                                                } catch (JSONException e) {
                                                    e.printStackTrace();
                                                }
                                            }
                                        }, new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        Toast.makeText(getApplicationContext(), "登入失敗", Toast.LENGTH_LONG).show();
                                    }
                                }) {
                                };
                                queue.add(stringRequest);
                    }
                }
            }
        });
    }

    public void saveData(){
        settings = getSharedPreferences(data,0);
        if(!metxt1.getText().toString().equals("") && !metxt2.getText().toString().equals("")){
            settings.edit()
                    .putString(logginemail, metxt1.getText().toString().trim())
                    .putString(logginpassword, metxt2.getText().toString().trim())
                    .commit();
        }
    }

    private Emitter.Listener onNewMessage = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            String data=args[0].toString();
            JSONObject DriverLocationDetail2 = new JSONObject();
            try {
                DriverLocationDetail2.put("cid", Cid);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            if(data.equals("false")){
                mSocket.emit("add driver", DriverLocationDetail2);
            }
        }
    };

}
