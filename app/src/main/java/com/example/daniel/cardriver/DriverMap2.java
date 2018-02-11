package com.example.daniel.cardriver;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
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
import android.widget.Toast;

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
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by daniel on 2017/9/13.
 */

public class DriverMap2 extends FragmentActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    private GoogleMap mMap;
    private final int REQUEST_PERMISSION_FOR_ACCESS_FINE_LOCATION=100;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private LocationManager mLocationMgr;
    private Marker currentMarker, itemMarker;
    private double latitude=0.0;
    private double longitude =0.0;
    private ArrayList<LatLng> traceOfMe;
    private Button mbtnrecordroute, mbtnarrivedestination;
    private Socket mSocket;
    public final static String TAG = DriverMap2.class.getName();
    private String StartTime, EndTime, ConfrimCid, latlng;
    public static String LatLng;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.drivermap2);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map2);
        mapFragment.getMapAsync(this);
        mGoogleApiClient=new GoogleApiClient.Builder(this).addConnectionCallbacks(this).addOnConnectionFailedListener(this).addApi(LocationServices.API).build();
        mLocationMgr=(LocationManager)getSystemService(LOCATION_SERVICE);

        socketapplication app = (socketapplication) getApplication();
        mSocket = app.getSocket();

        Bundle fromGoogleMaoRoute=this.getIntent().getExtras();
        ConfrimCid=fromGoogleMaoRoute.getString("ConfrimCid");

        mbtnrecordroute=(Button)findViewById(R.id.btnopenrecordroute);
        mbtnrecordroute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                enableLocationAndGetLastLocation(true);
                Calendar dtstart = Calendar.getInstance();
                int thisYear = dtstart.get(Calendar.YEAR);
                int thisMonth = dtstart.get(Calendar.MONTH)+1;
                int thisDate = dtstart.get(Calendar.DAY_OF_MONTH);
                int thisHour =dtstart.get(Calendar.HOUR_OF_DAY);
                int thisMin = dtstart.get(Calendar.MINUTE);
                StartTime=thisYear+"-"+thisMonth+"-"+thisDate+" "+thisHour+":"+thisMin;
                trackToMe(latitude,longitude);

            }
        });

        mbtnarrivedestination=(Button)findViewById(R.id.btnarrivedestination);
        mbtnarrivedestination.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                enableLocationAndGetLastLocation(false);

                Calendar dtEnd = Calendar.getInstance();
                int thisYear = dtEnd.get(Calendar.YEAR);
                int thisMonth = dtEnd.get(Calendar.MONTH)+1;
                int thisDate = dtEnd.get(Calendar.DAY_OF_MONTH);
                int thisHour = dtEnd.get(Calendar.HOUR_OF_DAY);
                int thisMin = dtEnd.get(Calendar.MINUTE);
                EndTime=thisYear+"-"+thisMonth+"-"+thisDate+" "+thisHour+":"+thisMin;



                Bundle torecordcasedetail=new Bundle();
                torecordcasedetail.putString("StartTime",StartTime);
                torecordcasedetail.putString("EndTime", EndTime);
                torecordcasedetail.putString("ConfirmCid", ConfrimCid);
                Intent intent = new Intent();
                intent.setClass(DriverMap2.this, recordcasedetail.class);
                intent.putExtras(torecordcasedetail);
                startActivity(intent);
            }
        });

    }

    @Override
    protected void onStart(){
        super.onStart();
        //啟動google api
        mGoogleApiClient.connect();
    }

    @Override
    protected void onPause(){
        super.onPause();
        //停止定位
        enableLocationAndGetLastLocation(false);
    }

    @Override
    protected void onStop(){
        super.onStop();
        //Toast.makeText(MapsActivity.this, "停用Google API", Toast.LENGTH_LONG).show();
        mGoogleApiClient.disconnect();
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
    public void onConnected(@Nullable Bundle bundle) {
        Location location=enableLocationAndGetLastLocation(true);
        if(location!=null){
            //Toast.makeText(MapsActivity.this, "成功取的上一次定位", Toast.LENGTH_LONG).show();
            onLocationChanged(location);
        }else{
            //Toast.makeText(MapsActivity.this, "沒有上一次定位資料", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onConnectionSuspended(int cause) {
        switch(cause){
            case CAUSE_NETWORK_LOST:
                Toast.makeText(DriverMap2.this, "網路斷線，無法定位", Toast.LENGTH_LONG).show();
                break;
            case CAUSE_SERVICE_DISCONNECTED:
                Toast.makeText(DriverMap2.this, "Google API異常，無法定位", Toast.LENGTH_LONG).show();
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
        //抓位置的經緯度
        if(location != null){
            latitude = location.getLatitude(); //經度
            longitude = location.getLongitude(); //緯度
        }
        //這個是要來顯示圖標的方法
        if (currentMarker == null) {
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
    }

    private void moveMap(LatLng place) {
        // 建立地圖攝影機的位置物件
        CameraPosition cameraPosition =
                new CameraPosition.Builder().target(place).zoom(15).build();
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
        if(ContextCompat.checkSelfPermission(DriverMap2.this, android.Manifest.permission.ACCESS_FINE_LOCATION) !=PackageManager.PERMISSION_GRANTED){
            //這項功能尚未取得使用者同意
            //開始執行徵詢使用者的流程
            if(ActivityCompat.shouldShowRequestPermissionRationale(DriverMap2.this, android.Manifest.permission.ACCESS_FINE_LOCATION)){
                AlertDialog.Builder altDlgBuilder=new AlertDialog.Builder(DriverMap2.this);
                altDlgBuilder.setTitle("提示");
                altDlgBuilder.setMessage("APP需要啟動定位功能");
                altDlgBuilder.setIcon(android.R.drawable.ic_dialog_info);
                altDlgBuilder.setCancelable(false);
                altDlgBuilder.setPositiveButton("確定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        ActivityCompat.requestPermissions(DriverMap2.this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_PERMISSION_FOR_ACCESS_FINE_LOCATION);
                    }
                });
                altDlgBuilder.show();
                return null;
            }else{
                ActivityCompat.requestPermissions(DriverMap2.this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_PERMISSION_FOR_ACCESS_FINE_LOCATION);
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

    private void trackToMe(double lat, double lng){
        if (traceOfMe == null) {
            traceOfMe = new ArrayList<LatLng>();
        }
        traceOfMe.add(new LatLng(lat, lng));
        PolylineOptions polylineOpt = new PolylineOptions();
        for (LatLng latlng : traceOfMe) {
            polylineOpt.add(latlng);
        }
        polylineOpt.color(Color.RED);
        Polyline line = mMap.addPolyline(polylineOpt);
        line.setWidth(20);
        //LatLng=null;
        for(int i=0; i<traceOfMe.size(); i++){
            double lat3=traceOfMe.get(i).latitude;
            double lng3=traceOfMe.get(i).longitude;
            latlng=lat3+","+lng3+",";
            if(LatLng==null){
                LatLng=latlng;
            }else{
                LatLng+=latlng;
            }
        }
       Log.d(TAG, latlng);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
    }
}
