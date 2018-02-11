package com.example.daniel.cardriver;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
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
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import Modules.DirectionFinder;
import Modules.Route;

/**
 * Created by daniel on 2017/9/13.
 */

public class GoogleMapRoute extends FragmentActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    private GoogleMap mMap;
    private final int REQUEST_PERMISSION_FOR_ACCESS_FINE_LOCATION = 100;
    private GoogleApiClient mGoogleApiClient;
    private LocationManager mLocationMgr;
    private LocationRequest mLocationRequest;
    private Marker currentMarker, itemMarker;
    private double latitude = 0.0;
    private double longitude = 0.0;
    private Button btnFindPath, mbtnnavi, mbtnarrive;
    private EditText etOrigin;
    private EditText etDestination;
    private List<Marker> originMarkers = new ArrayList<>();
    private List<Marker> destinationMarkers = new ArrayList<>();
    private List<Polyline> polylinePaths = new ArrayList<>();
    private ProgressDialog progressDialog;
    private String returnAddress, Cid,  ConfirmCid, MapCarCid, CaseStyle,  MapCarClientDestination, MapCarClientLocation;
    private Socket mSocket;
    private Boolean ClientConfirm;
    public final static String TAG = GoogleMapRoute.class.getName();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.googlemaproute);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        btnFindPath = (Button) findViewById(R.id.btnFindPath);
        etOrigin = (EditText) findViewById(R.id.etOrigin);
        etDestination = (EditText) findViewById(R.id.etDestination);
        mbtnnavi=(Button)findViewById(R.id.btnnavigation);
        mbtnarrive=(Button)findViewById(R.id.btnarrive);

        socketapplication app = (socketapplication) getApplication();
        mSocket = app.getSocket();

        Bundle fromCaseDetail=this.getIntent().getExtras();
        String Clientlocaiton=fromCaseDetail.getString("Clientlocation");
        Cid=fromCaseDetail.getString("Cid");

        Bundle fromDriverMapCarCaseDetail=this.getIntent().getExtras();
        CaseStyle=fromDriverMapCarCaseDetail.getString("MapCaseStyle");
        //Log.d(TAG, CaseStyle);
        MapCarClientDestination=fromDriverMapCarCaseDetail.getString("MapClientLocation");
        MapCarClientLocation=fromDriverMapCarCaseDetail.getString("MapClientDestination");
        MapCarCid=fromDriverMapCarCaseDetail.getString("MapCid");
        final JSONObject cid=new JSONObject();
        try {
            cid.put("cid", Cid);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        final JSONObject mapcarcid=new JSONObject();
        try {
            mapcarcid.put("cid", MapCarCid);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if(CaseStyle==null){
            etDestination.setText(Clientlocaiton);
        }else{
            etDestination.setText(MapCarClientLocation);
        }


        btnFindPath.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendRequest();
            }
        });

        mbtnnavi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                enableLocationAndGetLastLocation(true);
            }
        });

        mbtnarrive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                enableLocationAndGetLastLocation(false);
                if(CaseStyle==null){
                    mSocket.emit("driver arrive", cid);
                }else{
                    mSocket.emit("driver arrive", mapcarcid);
                }
                mSocket.once("arrive confirm", checkconfirm);
            }
        });

        mGoogleApiClient = new GoogleApiClient.Builder(this).addConnectionCallbacks(this).addOnConnectionFailedListener(this).addApi(LocationServices.API).build();
        mLocationMgr = (LocationManager) getSystemService(LOCATION_SERVICE);

    }

    @Override
    protected void onStart() {
        super.onStart();
        //啟動google api
        mGoogleApiClient.connect();
    }

    @Override
    protected void onPause() {
        super.onPause();
        //停止定位
        enableLocationAndGetLastLocation(true);
    }

    @Override
    protected void onStop() {
        super.onStop();
        mGoogleApiClient.connect();
        enableLocationAndGetLastLocation(true);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        //檢查收到的權限要求編號是否和我們送出的相同
        if (requestCode == REQUEST_PERMISSION_FOR_ACCESS_FINE_LOCATION) {
            if (grantResults.length != 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Location location = enableLocationAndGetLastLocation(true);
                if (location != null) {
                    onLocationChanged(location);
                } else {
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
            onLocationChanged(location);
            Notification.Builder mBuilder = new Notification.Builder(this);
            mBuilder.setContentTitle("記錄路線").setSmallIcon(R.mipmap.ic_clock);
        }else{
        }
    }

    @Override
    public void onConnectionSuspended(int cause) {
        switch(cause){
            case CAUSE_NETWORK_LOST:
                Toast.makeText(GoogleMapRoute.this, "網路斷線，無法定位", Toast.LENGTH_LONG).show();
                break;
            case CAUSE_SERVICE_DISCONNECTED:
                Toast.makeText(GoogleMapRoute.this, "Google API異常，無法定位", Toast.LENGTH_LONG).show();
                break;
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        Location currentLocation = location;
        //抓位置的經緯度
        if(location != null){
            latitude = location.getLatitude(); //經度
            longitude = location.getLongitude(); //緯度
        }
        moveMap(latLng);
        Geocoder gc2 = new Geocoder(this, Locale.TRADITIONAL_CHINESE);
        try {
            List<Address> lstAddress = gc2.getFromLocation(latitude, longitude, 1);
            returnAddress=lstAddress.get(0).getAddressLine(0);
        } catch (IOException e) {
            e.printStackTrace();
        }
        etOrigin.setText(returnAddress);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setZoomControlsEnabled(true);
    }

    private void moveMap(LatLng place) {
        // 建立地圖攝影機的位置物件
        CameraPosition cameraPosition =
                new CameraPosition.Builder().target(place).zoom(18).build();
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

    private void sendRequest() {
        String origin = etOrigin.getText().toString();
        String destination = etDestination.getText().toString();
        if (origin.isEmpty()) {
            Toast.makeText(this, "Please enter origin address!", Toast.LENGTH_LONG).show();
            return;
        }
        if (destination.isEmpty()) {
            Toast.makeText(this, "Please enter destination address!", Toast.LENGTH_LONG).show();
            return;
        }
        try {
            new DirectionFinder(this, origin, destination).execute();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        enableLocationAndGetLastLocation(false);
    }

    private Location enableLocationAndGetLastLocation(boolean on){
        if(ContextCompat.checkSelfPermission(GoogleMapRoute.this, android.Manifest.permission.ACCESS_FINE_LOCATION) !=PackageManager.PERMISSION_GRANTED){
            //這項功能尚未取得使用者同意
            //開始執行徵詢使用者的流程
            if(ActivityCompat.shouldShowRequestPermissionRationale(GoogleMapRoute.this, android.Manifest.permission.ACCESS_FINE_LOCATION)){
                AlertDialog.Builder altDlgBuilder=new AlertDialog.Builder(GoogleMapRoute.this);
                altDlgBuilder.setTitle("提示");
                altDlgBuilder.setMessage("APP需要啟動定位功能");
                altDlgBuilder.setIcon(android.R.drawable.ic_dialog_info);
                altDlgBuilder.setCancelable(false);
                altDlgBuilder.setPositiveButton("確定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        ActivityCompat.requestPermissions(GoogleMapRoute.this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_PERMISSION_FOR_ACCESS_FINE_LOCATION);
                    }
                });
                altDlgBuilder.show();
                return null;
            }else{
                ActivityCompat.requestPermissions(GoogleMapRoute.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_PERMISSION_FOR_ACCESS_FINE_LOCATION);
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
                Toast.makeText(GoogleMapRoute.this, "使用GPS定位", Toast.LENGTH_LONG).show();
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


    public void onDirectionFinderStart() {
        progressDialog = ProgressDialog.show(this, "Please wait.",
                "Finding direction..!", true);
        if (originMarkers != null) {
            for (Marker marker : originMarkers) {
                marker.remove();
            }
        }
        if (destinationMarkers != null) {
            for (Marker marker : destinationMarkers) {
                marker.remove();
            }
        }
        if (polylinePaths != null) {
            for (Polyline polyline:polylinePaths ) {
                polyline.remove();
            }
        }
    }

    public void onDirectionFinderSuccess(List<Route> routes) {
        progressDialog.dismiss();
        polylinePaths = new ArrayList<>();
        originMarkers = new ArrayList<>();
        destinationMarkers = new ArrayList<>();
        for (Route route : routes) {
            //mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(route.startLocation, 13));
            LatLngBounds.Builder builder = new LatLngBounds.Builder();
            builder.include(route.startLocation);
            builder.include(route.endLocation);
            LatLngBounds bounds = builder.build();
            mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 20));
            //((TextView) findViewById(R.id.tvDuration)).setText(route.duration.text);
            //((TextView) findViewById(R.id.tvDistance)).setText(route.distance.text);
            //originMarkers.add(mMap.addMarker(new MarkerOptions().title(route.startAddress).position(route.startLocation)));
            destinationMarkers.add(mMap.addMarker(new MarkerOptions().title(etDestination.getText().toString()).position(route.endLocation)));
            PolylineOptions polylineOptions = new PolylineOptions().geodesic(true).color(Color.BLUE).width(30);
            for (int i = 0; i < route.points.size(); i++)
                polylineOptions.add(route.points.get(i));
            polylinePaths.add(mMap.addPolyline(polylineOptions));
        }
    }

    private Emitter.Listener checkconfirm = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            JSONObject confirm = (JSONObject) args[0];
            try {
               ClientConfirm=confirm.getBoolean("confirm");
                Log.d(TAG, ClientConfirm.toString());
                ConfirmCid=confirm.getString("cid");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            if(ClientConfirm.equals(true)){
                Bundle toDriverMap2=new Bundle();
                toDriverMap2.putString("ConfrimCid", ConfirmCid);
                Intent intent = new Intent();
                intent.setClass(GoogleMapRoute.this, DriverMap2.class);
                intent.putExtras(toDriverMap2);
                startActivity(intent);

            }else if(ClientConfirm.equals(false)){
                runOnUiThread(new Runnable(){
                    public void run(){
                        Toast.makeText(getApplicationContext(), "乘客未看見 請再發送一次", Toast.LENGTH_LONG).show();
                    }
                });
            }
        }
    };
}
