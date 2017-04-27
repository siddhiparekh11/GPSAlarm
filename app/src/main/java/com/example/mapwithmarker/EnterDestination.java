package com.example.mapwithmarker;

/**
 * Created by siddhiparekh11 on 4/24/17.
 */
import android.app.Activity;
import android.content.Intent;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;
import android.view.View;
import android.location.Address;
import android.support.v7.app.AppCompatActivity;
import android.support.v4.app.Fragment;
import android.graphics.Color;


import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.MarkerOptions;

import android.widget.Button;


import java.util.List;

public class EnterDestination extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, OnMapReadyCallback {

    private EditText edRadius;
    private Button btnNext;
    protected Location mLastLocation;
    Double lat;
    Double lng;
    protected GoogleApiClient mGoogleApiClient;
    private GoogleMap d;
    double dmetres,dmiles;
    private Circle geoFenceLimit;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);
        edRadius = (EditText) findViewById(R.id.radius);
        btnNext = (Button) findViewById(R.id.next1);

            lat = this.getIntent().getDoubleExtra("latitude",0.0);
            lng = this.getIntent().getDoubleExtra("longitude",0.0);


    }
    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }
    @Override
    protected void onStart() {
        super.onStart();



    }
    @Override
    protected void onResume() {
        super.onResume();
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

    }
    @Override
    protected void onStop() {
        super.onStop();
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

        LatLng destination = new LatLng(lat,lng);
        d.setMapType(1);
        d.addMarker(new MarkerOptions().position(destination)
                .title("Marker in Destination").icon(BitmapDescriptorFactory
                .defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));

        d.moveCamera(CameraUpdateFactory.newLatLng(destination));
        d.animateCamera(CameraUpdateFactory.zoomTo(11), 2000, null);


    }

    @Override
    public void onConnectionSuspended(int i) {

        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d("Error","Connection failed");

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        buildGoogleApiClient();
        mGoogleApiClient.connect();
        d=googleMap;

    }

    //convert miles into metres
    public void onSetRad(View v)
    {
        Log.d("message","We are in onSetRad");
        if(!btnNext.isEnabled()) {
            btnNext.setEnabled(true);
            btnNext.setBackgroundResource(R.drawable.rounded_corners_button);
            dmiles=Double.valueOf(edRadius.getText().toString());
            dmetres=dmiles*1602;
            drawGeofence();
        }
    }
    public void onNext(View v)
    {
        Log.d("message","I am in next of enter destination");
        Intent i=new Intent(this,Relax.class);
        i.putExtra("latitude",lat);
        i.putExtra("longitude",lng);
        //i.putExtra("radius",Double.valueOf(edRadius.getText().toString()));
        i.putExtra("radius",dmetres);
        startActivity(i);
    }

    //draw a circular limit around the destination
    private void drawGeofence() {


        if ( geoFenceLimit != null )
            geoFenceLimit.remove();

        CircleOptions circleOptions = new CircleOptions()
                .center( new LatLng(lat,lng))
                .strokeColor(Color.GREEN)
                .fillColor( Color.argb(64, 0, 255, 0) )
                .radius( dmetres );
        geoFenceLimit = d.addCircle( circleOptions );
    }
}

