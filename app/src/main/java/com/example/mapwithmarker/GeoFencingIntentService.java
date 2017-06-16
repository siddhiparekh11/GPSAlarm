package com.example.mapwithmarker;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.text.TextUtils;
import android.util.Log;
import android.app.PendingIntent;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;
import android.location.Location;
/**
 * Created by siddhiparekh11 on 4/24/17.
 */

public class GeoFencingIntentService extends IntentService {

    protected static final String TAG="GeoFencingIntentService";
    private float remdis;
    double latitude,longitude;
    Location l;

    public GeoFencingIntentService()
    {
        super(TAG);
    }

    //on receiving the geofencing trigger this function send notification to  the user
    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);
        if (geofencingEvent.hasError()) {

            return;
        }


        int geofenceTransition = geofencingEvent.getGeofenceTransition();
        Log.d("values",String.valueOf(intent.getDoubleExtra("latitude",0.0)));
        latitude=intent.getDoubleExtra("latitude",0.0);
        longitude=intent.getDoubleExtra("longitude",0.0);
        l=new Location("");
        l.setLatitude(latitude);
        l.setLongitude(longitude);

        remdis=geofencingEvent.getTriggeringLocation().distanceTo(l);



        if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER)
        {
            List<Geofence> t= geofencingEvent.getTriggeringGeofences();
            String geofenceTransitionDetails = getGeofenceTransitionDetails(
                    this,
                    geofenceTransition,
                    t
            );
            sendNotification(geofenceTransitionDetails);
            Log.i(TAG, geofenceTransitionDetails);

        }

    }
    private void sendNotification(String notificationDetails) {

        Notification nobj;
        Intent notificationIntent = new Intent(this, Relax.class);
         notificationIntent.putExtra("type","notification");
         notificationIntent.putExtra("distance",String.valueOf(remdis));


        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);


        stackBuilder.addParentStack(MapsMarkerActivity.class);


        stackBuilder.addNextIntent(notificationIntent);


        PendingIntent notificationPendingIntent =
                stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);


        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);


        builder.setSmallIcon(R.drawable.ic_launcher)

                .setLargeIcon(BitmapFactory.decodeResource(getResources(),
                        R.drawable.ic_launcher))
                .setColor(Color.RED)
                .setAutoCancel(true)
                .setContentTitle(notificationDetails)
                .setContentText(getString(R.string.geofence_transition_notification_text))
                .setContentIntent(notificationPendingIntent)
        ;

       // Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        Uri alarmSound=Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.lovingyou);

        builder.setSound(alarmSound);



        nobj=builder.build();
        nobj.flags=Notification.DEFAULT_SOUND | Notification.FLAG_INSISTENT | Notification.FLAG_AUTO_CANCEL;


        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);


        mNotificationManager.notify(0, nobj);
    }
    private String getGeofenceTransitionDetails(
            Context context,
            int geofenceTransition,
            List<Geofence> triggeringGeofences) {

        String geofenceTransitionString = getTransitionString(geofenceTransition);


        ArrayList triggeringGeofencesIdsList = new ArrayList();
        for (Geofence geofence : triggeringGeofences) {
            triggeringGeofencesIdsList.add(geofence.getRequestId());

        }
        String triggeringGeofencesIdsString = TextUtils.join(", ",  triggeringGeofencesIdsList);

        return geofenceTransitionString ;
    }
    private String getTransitionString(int transitionType) {
        switch (transitionType) {
            case Geofence.GEOFENCE_TRANSITION_ENTER:
                return getString(R.string.geofence_transition_entered);
            case Geofence.GEOFENCE_TRANSITION_EXIT:
                return getString(R.string.geofence_transition_exited);
            default:
                return getString(R.string.unknown_geofence_transition);
        }
    }
}
