package com.example.daniel.cardriver;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class areaActivity extends AppCompatActivity {

    private Button mbtnFinish;
    private String url="http://203.77.73.5:9797/d_register";
    private String areanum;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_area);
        final RequestQueue queue = Volley.newRequestQueue(areaActivity.this);

        final CheckBox CB0=(CheckBox)findViewById(R.id.check0);
        final CheckBox CB1=(CheckBox)findViewById(R.id.check1);
        final CheckBox CB2=(CheckBox)findViewById(R.id.check2);
        final CheckBox CB3=(CheckBox)findViewById(R.id.check3);
        final CheckBox CB4=(CheckBox)findViewById(R.id.check4);
        final CheckBox CB5=(CheckBox)findViewById(R.id.check5);
        final CheckBox CB6=(CheckBox)findViewById(R.id.check6);
        final CheckBox CB7=(CheckBox)findViewById(R.id.check7);
        final CheckBox CB8=(CheckBox)findViewById(R.id.check8);
        final CheckBox CB9=(CheckBox)findViewById(R.id.check9);
        final CheckBox CB10=(CheckBox)findViewById(R.id.check10);
        final CheckBox CB11=(CheckBox)findViewById(R.id.check11);
        final CheckBox CB12=(CheckBox)findViewById(R.id.check12);
        final CheckBox CB13=(CheckBox)findViewById(R.id.check13);
        final CheckBox CB14=(CheckBox)findViewById(R.id.check14);
        final CheckBox CB15=(CheckBox)findViewById(R.id.check15);
        final CheckBox CB16=(CheckBox)findViewById(R.id.check16);
        final CheckBox CB17=(CheckBox)findViewById(R.id.check17);
        final CheckBox CB18=(CheckBox)findViewById(R.id.check18);
        final CheckBox CB19=(CheckBox)findViewById(R.id.check19);
        final CheckBox CB20=(CheckBox)findViewById(R.id.check20);

        //final ArrayList<String> areaNum=new ArrayList();

        /*areanum="";
        if(CB0.isChecked()){
           areanum+="0"+",";
        }if(CB1.isChecked()){
            areanum+="1"+",";
        }if(CB2.isChecked()){
            areanum+="2"+",";
        }if(CB3.isChecked()){
            areanum+="3"+",";
        }if(CB4.isChecked()){
            areanum+="4"+",";
        }if(CB5.isChecked()){
            areanum+="5"+",";
        }if(CB6.isChecked()){
            areanum+="6"+",";
        }if(CB7.isChecked()){
            areanum+="7"+",";
        }if(CB8.isChecked()){
            areanum+="8"+",";
        }if(CB9.isChecked()){
            areanum+="9"+",";
        }if(CB10.isChecked()){
            areanum+="10"+",";
        }if(CB11.isChecked()){
            areanum+="11"+",";
        }if(CB12.isChecked()){
            areanum+="12"+",";
        }if(CB13.isChecked()){
            areanum+="13"+",";
        }if(CB14.isChecked()){
            areanum+="14"+",";
        }if(CB15.isChecked()){
            areanum+="15"+",";
        }if(CB16.isChecked()){
            areanum+="16"+",";
        }if(CB17.isChecked()){
            areanum+="17"+",";
        }if(CB18.isChecked()){
            areanum+="18"+",";
        }if(CB19.isChecked()){
            areanum+="19"+",";
        }if(CB20.isChecked()){
            areanum+="20"+",";
        }*/

        Bundle fromDriverCarInformation=this.getIntent().getExtras();
        final String DriverMail=fromDriverCarInformation.getString("Drivermail");
        final String DriverName=fromDriverCarInformation.getString("DriverName");
        final String DriverSex=fromDriverCarInformation.getString("DriverSex");
        final String DriverPhone=fromDriverCarInformation.getString("DriverPhone");
        final String DriverCopse=fromDriverCarInformation.getString("DriverCopse");
        final String DriverStyle=fromDriverCarInformation.getString("DriverStyle");
        final String CarYear=fromDriverCarInformation.getString("CarYear");
        final String CarColor=fromDriverCarInformation.getString("CarColor");
        final String CarBrand=fromDriverCarInformation.getString("CarBrand");
        final String CarNum=fromDriverCarInformation.getString("DriverCarNum");
        final String DriverPassword=fromDriverCarInformation.getString("DriverPassword");


        mbtnFinish=(Button)findViewById(R.id.btnfinish);
        mbtnFinish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                areanum="";
                if(CB0.isChecked()){
                    areanum+="0"+",";
                }if(CB1.isChecked()){
                    areanum+="1"+",";
                }if(CB2.isChecked()){
                    areanum+="2"+",";
                }if(CB3.isChecked()){
                    areanum+="3"+",";
                }if(CB4.isChecked()){
                    areanum+="4"+",";
                }if(CB5.isChecked()){
                    areanum+="5"+",";
                }if(CB6.isChecked()){
                    areanum+="6"+",";
                }if(CB7.isChecked()){
                    areanum+="7"+",";
                }if(CB8.isChecked()){
                    areanum+="8"+",";
                }if(CB9.isChecked()){
                    areanum+="9"+",";
                }if(CB10.isChecked()){
                    areanum+="10"+",";
                }if(CB11.isChecked()){
                    areanum+="11"+",";
                }if(CB12.isChecked()){
                    areanum+="12"+",";
                }if(CB13.isChecked()){
                    areanum+="13"+",";
                }if(CB14.isChecked()){
                    areanum+="14"+",";
                }if(CB15.isChecked()){
                    areanum+="15"+",";
                }if(CB16.isChecked()){
                    areanum+="16"+",";
                }if(CB17.isChecked()){
                    areanum+="17"+",";
                }if(CB18.isChecked()){
                    areanum+="18"+",";
                }if(CB19.isChecked()){
                    areanum+="19"+",";
                }if(CB20.isChecked()){
                    areanum+="20"+",";
                }

                HashMap<String, String> params = new HashMap<String, String>();
                params.put("A_Mail", DriverMail);
                params.put("name", DriverName);
                params.put("sex", DriverSex);
                params.put("phone", DriverPhone);
                params.put("t_copse", DriverCopse);
                params.put("kind", DriverStyle);
                params.put("M_Year", CarYear);
                params.put("color", CarColor);
                params.put("brand", CarBrand);
                params.put("c_passenger", CarNum);
                params.put("number_area", areanum);
                params.put("Password", DriverPassword);
                params.put("part", "2");


                JsonObjectRequest stringRequest = new JsonObjectRequest(url, new JSONObject(params),
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                try {
                                    //JSONObject js=new JSONObject(response);
                                    if (response.getBoolean("success")) {
                                        Toast.makeText(getApplicationContext(), "註冊完成", Toast.LENGTH_SHORT).show();
                                        //Log.e("CONNECTED", "SUCCESS");
                                        Bundle bundle=new Bundle();
                                        bundle.putString("Test", areanum);
                                        Intent intent = new Intent();
                                        intent.setClass(areaActivity.this, MainActivity.class);
                                        intent.putExtras(bundle);
                                        startActivity(intent);
                                        //startActivity(new Intent(areaActivity.this, MainActivity.class));
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(), "註冊失敗", Toast.LENGTH_LONG).show();
                    }
                }) {
                };
                queue.add(stringRequest);
            }
        });
    }
}
