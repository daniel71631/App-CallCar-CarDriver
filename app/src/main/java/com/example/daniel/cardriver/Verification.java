package com.example.daniel.cardriver;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class Verification extends AppCompatActivity {

    private EditText metxt1;
    private Button mbtn1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verification);

        metxt1=(EditText)findViewById(R.id.etxt1);
        mbtn1=(Button)findViewById(R.id.btn1);

        Bundle fromRegister=this.getIntent().getExtras();
        final String driveremail=fromRegister.getString("drivermail");
        final String driverpw =fromRegister.getString("driverpw");
        final String Vernum=fromRegister.getString("vernum");


        mbtn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(metxt1.getText().toString().equals(Vernum)){
                    Bundle Register2=new Bundle();
                    Register2.putString("drivermail", driveremail);
                    Register2.putString("driverpassword", driverpw);
                    Intent intent = new Intent();
                    intent.setClass(Verification.this, Register2Activity.class);
                    intent.putExtras(Register2);
                    startActivity(intent);
                }
            }
        });
    }
}
