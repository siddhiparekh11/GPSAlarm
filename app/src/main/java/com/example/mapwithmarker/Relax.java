package com.example.mapwithmarker;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.media.MediaPlayer.OnCompletionListener;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;
import android.app.PendingIntent;
import android.widget.Button;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.UUID;


/**
 * Created by siddhiparekh11 on 4/25/17.
 */

public class Relax extends Activity implements ConnectionCallbacks, OnConnectionFailedListener, ResultCallback<Status> {

    private double lat;
    private double lng;
    private float radius;
    protected GoogleApiClient mGoogleApiClient;
    protected Geofence gf1;
    private boolean geofenceAdded;
    private PendingIntent pdi;
    private Button btnAlarm;
    private MediaPlayer m;
    private TextView txtReached;
    private double miles;
    private ImageView img;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("message","Relax create is getting called!");

        setContentView(R.layout.activity_relax);
        btnAlarm=(Button)findViewById(R.id.setalarm);
        txtReached=(TextView)findViewById(R.id.reached);
        img=(ImageView)findViewById(R.id.smilingbuddha);
        if(this.getIntent().getStringExtra("type")!=null) //handles the notification
        {
            miles = Double.valueOf(getIntent().getStringExtra("distance")) * 0.000621371;
            txtReached.setText("You are " + String.format("%.2f", miles) + " miles away from your destination!");
            Log.d("message","I am being called from notification");
            txtReached.setVisibility(View.VISIBLE);
            img.setVisibility(View.VISIBLE);
           m  = new MediaPlayer().create(this,R.raw.lovingyou);
            m.start();
            m.setOnCompletionListener(new OnCompletionListener() {
                public void onCompletion(MediaPlayer mp) {

                        mp.start();

                    }
                }
             );

            btnAlarm.setText("Stop Alarm");
        }
        lat=this.getIntent().getDoubleExtra("latitude",0.0);
        lng=this.getIntent().getDoubleExtra("longitude",0.0);
        radius=(float)this.getIntent().getDoubleExtra("radius",0.0);
        pdi=null;
        geofenceAdded=false;

        btnAlarm=(Button)findViewById(R.id.setalarm);
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
        buildGoogleApiClient();
        mGoogleApiClient.connect();
    }
    @Override
    protected  void onResume(){
        super.onResume();
        Log.d("message","resume is being called");
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
      //  mGoogleApiClient.connect();



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
    public void onResult(@NonNull Status status) {

    }

    public void addGeoFence()
    {
        gf1=new Geofence.Builder().setRequestId(UUID.randomUUID().toString()).setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER).setCircularRegion(lat,lng,radius).setExpirationDuration(12*60*60*1000).build();
        Log.d("message","addGeoFence");
    }
    private GeofencingRequest getGeofencingRequest() {
        GeofencingRequest.Builder builder = new GeofencingRequest.Builder();


        builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER);


        builder.addGeofence(gf1);
        Log.d("message","getGeoFencingRequest");


        return builder.build();
    }
    private PendingIntent getGeofencePendingIntent() {
        if (pdi != null) {
            return pdi;
        }
        Intent intent = new Intent(this, GeoFencingIntentService.class);
        intent.putExtra("latitude",lat);
        intent.putExtra("longitude",lng);

        return PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    //add the geofence to the destination and start the service

    public void onStartAlarm(View v)
    {
          if(btnAlarm.getText().equals("Start Alarm"))
          {
              if(mGoogleApiClient.isConnected()) {
                  btnAlarm.setText("Stop Alarm");
                  try {
                      addGeoFence();
                      LocationServices.GeofencingApi.addGeofences(mGoogleApiClient,getGeofencingRequest(),getGeofencePendingIntent()).setResultCallback(this);

                  }catch (SecurityException e)
                  {
                      Log.d("Error",e.toString());
                  }
              }
              else
              {
                  return;
              }
          }
          else if(btnAlarm.getText().equals("Stop Alarm"))
          {
              m.stop();
              removeGeoFencing();
              btnAlarm.setText("Close App");

          }
          else if(btnAlarm.getText().equals("Close App"))
          {
              finishAffinity();
          }
    }
    public void removeGeoFencing()
    {
        if(mGoogleApiClient.isConnected()) {

            try {
                LocationServices.GeofencingApi.removeGeofences(mGoogleApiClient,getGeofencePendingIntent()).setResultCallback(this);

            }catch (SecurityException e)
            {
                Log.d("Error",e.toString());
            }
        }
        else
        {
            return;
        }

    }
}
