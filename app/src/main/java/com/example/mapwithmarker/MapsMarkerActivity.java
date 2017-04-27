package com.example.mapwithmarker;

import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.location.Location;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v4.app.ActivityCompat;
import android.content.pm.PackageManager;
import android.content.Intent;
import com.google.android.gms.common.api.Status;
import org.json.JSONArray;
import org.json.JSONException;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import android.widget.Button;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.location.places.Place;

import org.json.JSONObject;

import java.util.List;


public class MapsMarkerActivity extends AppCompatActivity
        implements ConnectionCallbacks, OnConnectionFailedListener, OnMapReadyCallback{

    protected GoogleApiClient mGoogleApiClient;
    protected Location mLastLocation;
    private GoogleMap d;
    private EditText edDestination;
    private TextView txtDisplay;
    Double lat;
    Double lng;
    private boolean flag=false;
    Button btnNext;
    Double oldlat,oldlng;
    String strPlace;
    private int count;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_maps);

        btnNext=(Button)findViewById(R.id.next);
        count=0;






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
    public void onConnected(Bundle connectionHint) {

        Log.d("Error","I am called first!");

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

        }
        else {
            mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        }
        if (mLastLocation != null) {
            Log.d("Error", Double.toString(mLastLocation.getLatitude()));

        } else {

            Log.d("Error", "Object is NULL");
        }
        if(flag==false)
        {
            lat=mLastLocation.getLatitude();
            lng=mLastLocation.getLongitude();
            oldlat=lat;
            oldlng=lng;
        }
        if(count==0)
        {
            LatLng source = new LatLng(lat,lng);
            d.setMapType(1);
            d.addMarker(new MarkerOptions().position(source)
                    .title("Marker in Home1"));

            d.moveCamera(CameraUpdateFactory.newLatLng(source));
            d.animateCamera(CameraUpdateFactory.zoomTo(11), 2000, null);
            count++;

        }

//autocomplete feature of the destination
        PlaceAutocompleteFragment autocompleteFragment = (PlaceAutocompleteFragment)
                getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);

        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {


                strPlace=place.getName().toString();

            }
            @Override
            public void onError(Status status) {

            }


        });

    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {

        Log.d("Error","Connection failed");
    }


    @Override
    public void onConnectionSuspended(int cause) {

        mGoogleApiClient.connect();
    }



    //connect the googleapiclient and initialize the map
    @Override
    public void onMapReady(GoogleMap googleMap) {

        Log.d("Error","I am getting called second!");
        buildGoogleApiClient();
        mGoogleApiClient.connect();
        d=googleMap;



    }
    public void onNext(View v)
    {
        Intent i=new Intent(this,EnterDestination.class);
        i.putExtra("latitude",lat);
        i.putExtra("longitude",lng);
        startActivity(i);


    }

    //marks the destination on the map and draw a connection between source and destination
    public void onGetLatLng(View v) {




        Log.d("message","we are in the button method");

        Geocoder requested_coordinates = new Geocoder(MapsMarkerActivity.this);
        List<Address> address;
        try {
            Log.d("destination",strPlace);

            address = requested_coordinates.getFromLocationName(strPlace, 5);
            if(address==null)
            {

            }
            else
            {

                Address location = address.get(0);
                lat= location.getLatitude();

                lng = location.getLongitude();
                LatLng destination = new LatLng(lat,lng);
                d.addMarker(new MarkerOptions().position(destination)
                        .title("Marker in Home2").icon(BitmapDescriptorFactory
                                .defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
                Polyline line = d.addPolyline(new PolylineOptions()
                        .add(new LatLng(oldlat, oldlng), new LatLng(lat, lng))
                        .width(5)
                        .color(Color.parseColor("#FF0000")));

                flag=true;
                if(!btnNext.isEnabled()) {
                    btnNext.setEnabled(true);
                    btnNext.setBackgroundResource(R.drawable.rounded_corners_button);
                }




            }

        } catch (Exception e) {


        }
    }
}
