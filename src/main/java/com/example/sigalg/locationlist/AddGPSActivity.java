package com.example.sigalg.locationlist;

import android.app.Activity;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.util.Date;

/**
 * Created by sigalg on 12/31/2014.
 * Class is designed to accept a location description from the add_location layout
 * and to pass the description and the current time and location back to GPSActivity
 */

// TODO Add button for adding / replacing picture
    // Add ImageView for displaying picture
    // Need to add the button and imageview into layout
    // For sizing, size ImageView based on some fraction of parent size
    // Figure out size on screen
    // Size picture to fit in ImageView
    // see http://stackoverflow.com/questions/4916159/android-get-thumbnail-of-image-on-sd-card-given-uri-of-original-image
    // Will need to add onClickListener to call camera activity when button is clicked
    // Need to save image Uri for packaging in intent and also display image in ImageView
    // Need to generate png image with words "No Image" - make 1000x1000 and scale as needed


public class AddGPSActivity extends Activity implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    // Location is considered recent if it is less than 15 seconds old
    private static final long RECENT = 15000;

    // default minimum time between new readings
    private long MINTIME = 1000;

    // default minimum distance (meters) between old and new readings.
    private long MINDIST = 10;

    // Class variables
    private EditText mTitleText;
    private Location mLocation;

    // Create instances of LocationClient and LocationRequest
    private LocationRequest mLocationRequest;
    private GoogleApiClient mGoogleApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Apply stored layout
        setContentView(R.layout.add_location);

        // Declare variable for title in edit text box
        mTitleText = (EditText) findViewById(R.id.title);

        // Set up instance of Api Client to Location Services
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        // Define actions on clicking the cancel button
        final Button cancelButton = (Button) findViewById(R.id.cancelButton);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GPSActivity.log("Entered cancelButton.OnClickListener.onClick()");
                // Set the result code as RESULT_CANCELED then finish
                AddGPSActivity.this.setResult(RESULT_CANCELED);
                finish();
            }
        });

        //Define actions for the Reset Button
        final Button resetButton = (Button) findViewById(R.id.resetButton);
        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GPSActivity.log("Entered resetButton.OnClickListener.onClick()");

                // Get ride of any entered title text
                mTitleText.setText(null);

            }
        });

        // Define actions for the Submit Button
        final Button submitButton = (Button) findViewById(R.id.submitButton);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GPSActivity.log("Entered submitButton.OnClickListener.onClick()");

                // Get location description from text box
                // Need to convert the Editable type used with the view to a string
                String titleString = mTitleText.getText().toString();

                // Get last location
                mLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

                // If there is no recent location, give toast message and take no other action
                // User can wait and hit button again later or turn on GPS if needed
                if (mLocation == null || age(mLocation) > RECENT) {
                    GPSActivity.log("No recent location is available");
                    Toast.makeText(getApplicationContext(),
                            "No recent location was available", Toast.LENGTH_LONG)
                            .show();
                }
                // Otherwise package data into an Intent and finish activity
                else {
                    Intent data = new Intent();
                    // Note: removed mDate from arguments since using date in mLocation
                    GPSItem.packageIntent(data, titleString, mLocation);
                    AddGPSActivity.this.setResult(RESULT_OK, data);
                    finish();
                }

            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        // Connect the client.
        mGoogleApiClient.connect();
    }

    @Override
    protected void onStop() {
        // Disconnecting the client invalidates it.
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    // Below are the methods that say what happens when LocationService status changes

    @Override
    public void onConnected(Bundle bundle) {

        // On connection, use LocationRequest to set parameters for collecting locations
        mLocationRequest = LocationRequest.create();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY); // Want high-acc locs
        mLocationRequest.setInterval(MINTIME); // Update location every MINTIME
        mLocationRequest.setSmallestDisplacement(MINDIST); // Update location for moves > MINDIST

        // Turn on automatic location updates
        LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleApiClient, mLocationRequest, this);
    }

    @Override
    public void onConnectionSuspended(int i) {
        GPSActivity.log("GoogleApiClient connection has suspended");
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        GPSActivity.log("GoogleApiClient connection has failed");
    }

    @Override
    public void onLocationChanged(Location location) {

    }

    // Calculate age of location measurement relative to current time
    private long age(Location location) {
        return System.currentTimeMillis() - location.getTime();
    }

}
