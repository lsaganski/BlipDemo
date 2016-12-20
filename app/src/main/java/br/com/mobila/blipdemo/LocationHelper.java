package br.com.mobila.blipdemo;

import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;


/**
 * Created by Leonardo Saganski on 27/11/16.
 */
public class LocationHelper implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private static LocationHelper instance;

    public static final String TAG = "LocationHelper";
    private GoogleApiClient mGoogleApiClient;
    Context _context;
    boolean connected;

    public static LocationHelper getInstance() {
        if (instance == null)
            instance = new LocationHelper();

        return instance;
    }

    public LocationHelper() {

        _context =  Globals.getInstance().applicationContext;

        mGoogleApiClient = new GoogleApiClient.Builder(_context)
                .addOnConnectionFailedListener(this)
                .addConnectionCallbacks(this)
                .addApi(LocationServices.API)
                .build();
    }

    public void startLocation() {
        if (!mGoogleApiClient.isConnected()) {
            mGoogleApiClient.connect();
        }
    }

    public void stopLocation() {
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }    }

    // LISTENER
    @Override
    public void onConnected(Bundle bundle) {
        Log.i("LOG", "onConnected(" + bundle + ")");

        connected = true;

    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.i("LOG", "onConnectionSuspended(" + i + ")");
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.i("LOG", "onConnectionFailed(" + connectionResult + ")");
    }

    public Location getLastLocation() {
        if (connected) {
            Location l = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

            return l;
        } else {
            return null;
        }
    }
}
