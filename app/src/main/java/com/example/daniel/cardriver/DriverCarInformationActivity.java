package com.example.daniel.cardriver;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

public class DriverCarInformationActivity extends AppCompatActivity {

    private EditText mCarYear, mCarColor, mCarBrand, mCarPeopleNum;
    private Spinner mCarStyle;
    private Button mbtnArea;
    private TextView mtxtCarStyle;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_car_information);

        mtxtCarStyle=(TextView)findViewById(R.id.txtEndTime);
        mCarStyle=(Spinner) findViewById(R.id.spinnerCarStyle);
        ArrayAdapter<CharSequence> sexAdapter = ArrayAdapter.createFromResource(this, R.array.CarStyle, android.R.layout.simple_spinner_item);
        mCarStyle.setAdapter(sexAdapter);
        mCarStyle.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String style=mCarStyle.getSelectedItem().toString().trim();
                mtxtCarStyle.setText(style);
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {


            }
        });


        Bundle driverCarInformation=this.getIntent().getExtras();
        final String Drivermail=driverCarInformation.getString("Drivermail");
        final String DriverName=driverCarInformation.getString("DriverName");
        final String DriverSex=driverCarInformation.getString("Sex");
        final String DriverPhone=driverCarInformation.getString("DriverPhone");
        final String DriverCopse=driverCarInformation.getString("Copse");
        final String DriverPassword=driverCarInformation.getString("DriverPassword");


        //final String DriverStyle = mCarStyle.getSelectedItem().toString().trim();
        mCarYear=(EditText)findViewById(R.id.etxtCarYear);
        //final String DriverYear=mCarYear.getText().toString().trim();
        mCarColor=(EditText)findViewById(R.id.etxtCarColor);
        //final String DriverColor=mCarColor.getText().toString().trim();
        mCarBrand=(EditText)findViewById(R.id.etxtCarBrand);
        //final String DriverBrand=mCarBrand.getText().toString().trim();
        mCarPeopleNum=(EditText)findViewById(R.id.etxtCarPeopleNum);
        //final String DriverCarNum=mCarPeopleNum.getText().toString().trim();

        mbtnArea=(Button)findViewById(R.id.btnarea);
        mbtnArea.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String DriverStyle = mCarStyle.getSelectedItem().toString().trim();
                String DriverYear=mCarYear.getText().toString().trim();
                String DriverColor=mCarColor.getText().toString().trim();
                String DriverBrand=mCarBrand.getText().toString().trim();
                String DriverCarNum=mCarPeopleNum.getText().toString().trim();
                Bundle Area=new Bundle();
                Area.putString("Drivermail", Drivermail);
                Area.putString("DriverName", DriverName);
                Area.putString("DriverSex", DriverSex);
                Area.putString("DriverPhone", DriverPhone);
                Area.putString("DriverCopse", DriverCopse);
                Area.putString("DriverStyle", DriverStyle);
                Area.putString("CarYear", DriverYear);
                Area.putString("CarColor", DriverColor);
                Area.putString("CarBrand", DriverBrand);
                Area.putString("DriverCarNum", DriverCarNum);
                Area.putString("DriverPassword", DriverPassword);
                Intent intent = new Intent();
                intent.setClass(DriverCarInformationActivity.this, areaActivity.class);
                intent.putExtras(Area);
                startActivity(intent);
            }
        });
    }
}
