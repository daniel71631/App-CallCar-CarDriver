package com.example.daniel.cardriver;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.Socket;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class DriverMapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    private GoogleMap mMap;
    private final int REQUEST_PERMISSION_FOR_ACCESS_FINE_LOCATION=100;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private LocationManager mLocationMgr;
    private Double latitude, CLa, CDesLa=0.0;
    private Double longitude, CLng, CDesLng=0.0;
    private Marker currentMarker,itemMarker;
    private String Cid;
    private Socket mSocket;
    public final static String TAG = DriverMapsActivity.class.getName();
    private String caseName, casela, caselng, caseStyle, caseCid;
    private SQLiteQuickMap dbHelper = null;
    private String ClientAddress,  ClientDestinationAddress;
    public static Boolean updatelocation = false;
    private Button mbtncaselist;
    private Switch mswitchcase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_maps);
        updatelocation=false;
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        mGoogleApiClient=new GoogleApiClient.Builder(this).addConnectionCallbacks(this).addOnConnectionFailedListener(this).addApi(LocationServices.API).build();
        mLocationMgr=(LocationManager)getSystemService(LOCATION_SERVICE);

        Bundle bundle = this.getIntent().getExtras();
        Cid = MainActivity.Cid;

        socketapplication app = (socketapplication) getApplication();
        mSocket = app.getSocket();

        //mSocket.on("fast request", todrivercasedetail);

        dbHelper = new SQLiteQuickMap(this);

        mbtncaselist=(Button)findViewById(R.id.btncaselist);
        mbtncaselist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(DriverMapsActivity.this,CaseQuickMap.class));
            }
        });

        mswitchcase=(Switch)findViewById(R.id.switchcase);
        mswitchcase.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean ischecked) {
                if(ischecked==true){
                    mSocket.on("fast request", todrivercasedetail);
                    mSocket.on("map request", todrivercasemapdetail);
                    updatelocation=true;
                }
                else{
                    mSocket.off("fast request", todrivercasedetail);
                    mSocket.off("map request", todrivercasemapdetail);
                    mSocket.emit("driver clear location");
                    updatelocation=false;
                }
            }
        });
        //updatelocation=true;
    }

    @Override
    protected void onStart(){
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mGoogleApiClient.connect();
        enableLocationAndGetLastLocation(true);
    }

    @Override
    protected void onStop(){
        super.onStop();
        mGoogleApiClient.connect();
        enableLocationAndGetLastLocation(true);
        updatelocation=true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        //檢查收到的權限要求編號是否和我們送出的相同
        if(requestCode==REQUEST_PERMISSION_FOR_ACCESS_FINE_LOCATION){
            if(grantResults.length!=0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                Location location=enableLocationAndGetLastLocation(true);
                if(location!=null){
                    //Toast.makeText(MapsActivity.this, "成功取的上一次定位", Toast.LENGTH_LONG).show();
                    onLocationChanged(location);
                }else{
                    //Toast.makeText(MapsActivity.this, "沒有上一次定位資料", Toast.LENGTH_LONG).show();
                }
                return;
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        //mSocket.on("loginstatus", onNewMessage);
        //mSocket.connect();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Location location=enableLocationAndGetLastLocation(true);
        if(location!=null){
            onLocationChanged(location);
        }else{
        }
    }

    @Override
    public void onConnectionSuspended(int cause) {
        switch(cause){
            case CAUSE_NETWORK_LOST:
                Toast.makeText(DriverMapsActivity.this, "網路斷線，無法定位", Toast.LENGTH_LONG).show();
                break;
            case CAUSE_SERVICE_DISCONNECTED:
                Toast.makeText(DriverMapsActivity.this, "Google API異常，無法定位", Toast.LENGTH_LONG).show();
                break;
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {
        mMap.animateCamera(CameraUpdateFactory.newLatLng(new LatLng(location.getLatitude(), location.getLongitude())));
        Location currentLocation = location;
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());

        if(location != null){
            latitude = location.getLatitude(); //經度
            longitude = location.getLongitude(); //緯度
        } if (currentMarker == null) {
            String la=String.valueOf(latitude);
            String lo= String.valueOf(longitude);
            currentMarker = mMap.addMarker(new MarkerOptions().position(latLng).flat(true));
            currentMarker.setTitle("目前位置");
            currentMarker.setSnippet(la+" "+lo);
        }
        else {
            currentMarker.setPosition(latLng);
            currentMarker.setTitle("目前位置");
        }
        moveMap(latLng);
        JSONObject DriverLocationDetail = new JSONObject();
        try {
            DriverLocationDetail.put("cid", Cid);
            DriverLocationDetail.put("lat", latitude);
            DriverLocationDetail.put("lng", longitude);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JSONObject DriverLocation=new JSONObject();
        try {
            DriverLocation.put("get latlng", DriverLocationDetail);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        mSocket.connect();
        if(updatelocation==true){
            mSocket.emit("update driver location", DriverLocationDetail);
        }
    }

    private void moveMap(LatLng place) {
        // 建立地圖攝影機的位置物件
        CameraPosition cameraPosition =
                new CameraPosition.Builder().target(place).zoom(17).build();
        // 使用動畫的效果移動地圖
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition),
                new GoogleMap.CancelableCallback() {
                    @Override
                    public void onFinish() {
                        if (itemMarker != null) {
                            itemMarker.showInfoWindow();
                        }
                    }
                    @Override
                    public void onCancel() {
                    }
                });
    }

    private Location enableLocationAndGetLastLocation(boolean on){
        if(ContextCompat.checkSelfPermission(DriverMapsActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION) !=PackageManager.PERMISSION_GRANTED){
            //這項功能尚未取得使用者同意
            //開始執行徵詢使用者的流程
            if(ActivityCompat.shouldShowRequestPermissionRationale(DriverMapsActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION)){
                AlertDialog.Builder altDlgBuilder=new AlertDialog.Builder(DriverMapsActivity.this);
                altDlgBuilder.setTitle("提示");
                altDlgBuilder.setMessage("APP需要啟動定位功能");
                altDlgBuilder.setIcon(android.R.drawable.ic_dialog_info);
                altDlgBuilder.setCancelable(false);
                altDlgBuilder.setPositiveButton("確定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        ActivityCompat.requestPermissions(DriverMapsActivity.this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_PERMISSION_FOR_ACCESS_FINE_LOCATION);
                    }
                });
                altDlgBuilder.show();
                return null;
            }else{
                ActivityCompat.requestPermissions(DriverMapsActivity.this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_PERMISSION_FOR_ACCESS_FINE_LOCATION);
                return null;
            }
        }
        Location lastLocation=null;
        if(on){
            lastLocation=LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            mLocationRequest=LocationRequest.create();
            mLocationRequest.setInterval(5000);
            mLocationRequest.setSmallestDisplacement(5);


            if(mLocationMgr.isProviderEnabled(LocationManager.GPS_PROVIDER)){
                mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
                //Toast.makeText(MapsActivity.this, "使用GPS定位", Toast.LENGTH_LONG).show();
            }else if(mLocationMgr.isProviderEnabled(LocationManager.NETWORK_PROVIDER)){
                mLocationRequest.setPriority(LocationRequest.PRIORITY_LOW_POWER);
                //Toast.makeText(MapsActivity.this, "使用網路定位", Toast.LENGTH_LONG).show();
            }
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }else{
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
            //Toast.makeText(MapsActivity.this, "停止定位", Toast.LENGTH_LONG).show();
        }
        return lastLocation;
    }

    private Emitter.Listener onNewMessage = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            String data=args[0].toString();
            Log.d(TAG, data);
            JSONObject DriverLocationDetail2 = new JSONObject();
            try {
                DriverLocationDetail2.put("cid", Cid);
                DriverLocationDetail2.put("lat", latitude);
                DriverLocationDetail2.put("lng", longitude);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            //Toast.makeText(DriverMapsActivity.this, data, Toast.LENGTH_SHORT).show();
            /*if(latitude.equals("") && longitude.equals("")){
                mSocket.emit("add driver",DriverLocationDetail2);
            }*/
            if(data.equals("false")){
                mSocket.emit("add driver", DriverLocationDetail2);
            }
        }
    };

    private Emitter.Listener todrivercasedetail = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            JSONObject data=(JSONObject) args[0];
            try {
                caseName=data.getString("name");
                Log.d(TAG, caseName);
                casela=data.getString("lat");
                Log.d(TAG, casela);
                caselng=data.getString("lng");
                Log.d(TAG, caselng);
                CLa=Double.parseDouble(casela);
                CLng=Double.parseDouble(caselng);
                caseCid=data.getString("cid");
                Log.d(TAG, caseCid);

            } catch (JSONException e) {
                e.printStackTrace();
            }
            Geocoder gc2 = new Geocoder(DriverMapsActivity.this, Locale.TRADITIONAL_CHINESE);
            try {
                List<Address> lstAddress = gc2.getFromLocation(CLa, CLng, 1);
                ClientAddress=lstAddress.get(0).getAddressLine(0);
            } catch (IOException e) {
                e.printStackTrace();
            }
            caseStyle="快速叫車";
            String sql = "insert into tb_quickmap( casecid,casestyle,clientname, clientlocation, clientlocationLa, clientlocationLng, clientDestination)values(?,?,?,?,?,?,?)";
            boolean flag=dbHelper.execData(sql, new Object[] { caseCid, caseStyle,caseName,ClientAddress ,casela, caselng, ""});
            if(flag){
                String success="insert success";
               Log.d(TAG, success);
            }
            else{
                Log.d(TAG, "insert fail");
            }
            Intent intent = new Intent();
            intent.setClass(DriverMapsActivity.this, CaseQuickMap.class);
            //intent.putExtras(toCaseDetail);
            startActivity(intent);
        }
    };

    private Emitter.Listener todrivercasemapdetail = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            JSONObject data=(JSONObject) args[0];
            try {
                caseName=data.getString("name");
                casela=data.getString("lat");
                caselng=data.getString("lng");
                CLa=Double.parseDouble(casela);
                CLng=Double.parseDouble(caselng);
                caseCid=data.getString("cid");
                ClientDestinationAddress=data.getString("destination");

            } catch (JSONException e) {
                e.printStackTrace();
            }
            Geocoder gc2 = new Geocoder(DriverMapsActivity.this, Locale.TRADITIONAL_CHINESE);
            try {
                List<Address> lstAddress = gc2.getFromLocation(CLa, CLng, 1);
                ClientAddress=lstAddress.get(0).getAddressLine(0);
            } catch (IOException e) {
                e.printStackTrace();
            }

            caseStyle="地圖叫車";
            String sql = "insert into tb_quickmap( casecid,casestyle,clientname, clientlocation, clientlocationLa, clientlocationLng, clientDestination)values(?,?,?,?,?,?,?)";
            boolean flag=dbHelper.execData(sql, new Object[] { caseCid, caseStyle,caseName,ClientAddress ,casela, caselng,ClientDestinationAddress});
            if(flag){
                String success="insert success";
                Log.d(TAG, success);
            }
            else{
                Log.d(TAG, "insert fail");
            }
            Intent intent = new Intent();
            intent.setClass(DriverMapsActivity.this, CaseQuickMap.class);
            //intent.putExtras(toCaseDetail);
            startActivity(intent);
        }
    };
}
