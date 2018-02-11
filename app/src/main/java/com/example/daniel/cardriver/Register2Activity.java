package com.example.daniel.cardriver;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.method.KeyListener;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

public class Register2Activity extends AppCompatActivity {

    private Spinner mspin1;
    private EditText mecopsetxt, mnametxt, mphonetxt;
    private int mYear, mMonth, mDay;

    private Button mdriverRig;
    private TextView mtxtsex;
    private KeyListener listener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register2);
        mspin1=(Spinner) findViewById(R.id.spin1);
        ArrayAdapter<CharSequence> sexAdapter = ArrayAdapter.createFromResource(this, R.array.sex, android.R.layout.simple_spinner_item);
        mspin1.setAdapter(sexAdapter);
        /*mspin1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                String sex=Sex[position];
                mtxtsex.setText(sex);
                view.setVisibility(View.INVISIBLE);
            }
        });*/


        Bundle fromVer=this.getIntent().getExtras();
        final String Drivermail=fromVer.getString("drivermail");
        final String DriverPassword=fromVer.getString("driverpassword");

        mecopsetxt=(EditText) findViewById(R.id.etxtcopse);
        //final String copse=mecopsetxt.getText().toString().trim();
        mnametxt=(EditText)findViewById(R.id.enametxt);
        //final String drivername=mnametxt.getText().toString().trim();
        mphonetxt=(EditText)findViewById(R.id.ephonetxt);
        //final String driverphone=mphonetxt.getText().toString().trim();
        mtxtsex=(TextView)findViewById(R.id.txtsex);
        //final String driverSex=mtxtsex.getText().toString();
        //final String driverSex = mspin1.getSelectedItem().toString().trim();

        mdriverRig=(Button)findViewById(R.id.btnRig);
        mdriverRig.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle driverCarInformation=new Bundle();
                String copse=mecopsetxt.getText().toString().trim();
                String drivername=mnametxt.getText().toString().trim();
                String driverphone=mphonetxt.getText().toString().trim();
                String driverSex = mspin1.getSelectedItem().toString().trim();
                driverCarInformation.putString("Drivermail", Drivermail);
                driverCarInformation.putString("DriverName", drivername);
                driverCarInformation.putString("Sex", driverSex);
                driverCarInformation.putString("DriverPhone", driverphone);
                driverCarInformation.putString("Copse", copse);
                driverCarInformation.putString("DriverPassword", DriverPassword);
                Intent intent = new Intent();
                intent.setClass(Register2Activity.this, DriverCarInformationActivity.class);
                intent.putExtras(driverCarInformation);
                startActivity(intent);
            }
        });
    }

    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        if(keyCode == KeyEvent.KEYCODE_BACK){
            Intent lastpage = new Intent();
            lastpage = new Intent(Register2Activity.this, RegisterActivity.class);
            startActivity(lastpage);
            this.finish();
        }
        return super.onKeyDown(keyCode, event);
    }
}
