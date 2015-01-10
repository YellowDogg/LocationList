package com.example.sigalg.locationlist;

import android.content.Intent;
import android.location.Location;
import android.net.Uri;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by sigalg on 12/31/2014.
 * Class holds data associated with a GPS location
 * Class variables are
 *  title - descriptor of location
 *  date - date and time of location entry
 *  location - GPS location
 */

// TODO Need to add code to handle a picture associated with a GPSItem
    //  Note the picture will be saved as a Uri
    //  Need to add instance variable
    //  Need to include in the constructor methods
    //  Need to include in intent building method
    //  Need to amend the toString, toLog and toStringHeader methods
    // Add get and setPicture methods

public class GPSItem {

    // Use "," to separate items in text representations
    public final static String ITEM_SEP = ",";
    public final static String TITLE = "title";
    // public final static String DATE = "date";
    public final static String LOCATION = "location";

    public final static SimpleDateFormat FORMAT = new SimpleDateFormat(
            "yyy-MM-dd HH:mm:ss", Locale.US);

    private String mTitle;
    private Location mLocation;
    private Uri mPicture;

    // Construct from values provided as arguments
    GPSItem(String title, Location location) {
        this.mTitle = title;
        this.mLocation = location;

    }

    // Construct from data provided in an intent
    GPSItem(Intent intent) {
        this.mTitle = intent.getStringExtra(GPSItem.TITLE);
        this.mLocation = (Location) intent.getParcelableExtra(GPSItem.LOCATION);
        GPSActivity.log("GPSItem From Intent" + (new Date(this.mLocation.getTime())).toString());
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public Location getLocation() {
        return mLocation;
    }

    public void setLocation(Location location) {
        mLocation = location;
    }

    // Package GPSItem data for transport in an intent
    public static void packageIntent(Intent intent, String title,
                                     Location location) {
        // Note: location will be packaged as a parceable
        intent.putExtra(GPSItem.TITLE, title);
        intent.putExtra(GPSItem.LOCATION, location);
        GPSActivity.log("Packaging Intent from GPSItem" +
                (new Date(location.getTime())).toString());
    }

    // Represent object as a string
    public String toString() {
        // Get Location time/date, latitude, longitude, altitude, accuracy and provider as strings
        // Will provide information in the following order:
        //  Date (UTC), Date (human-readable), provider, latitude (degrees), longitude (degrees),
        //      altitude (meters), accuracy (meters), description
        //  Items are separated using ITEM_SEP which has been set to be a comma
        // For provider, altitude and accuracy, need to check if there is a value, if not use NA

        String sDateUTC = Long.toString(mLocation.getTime());
        Date newDate = new Date(mLocation.getTime());
        String sDateHuman = FORMAT.format(newDate); // Convert UTC time to human readable date/time

        String sProvider = "NA";
        if (mLocation.getProvider() != null) {
            sProvider = mLocation.getProvider();
        }

        String sLatitude = Location.convert(mLocation.getLatitude(),
                Location.FORMAT_DEGREES);
        String sLongitude = Location.convert(mLocation.getLongitude(),
                Location.FORMAT_DEGREES);

        String sAltitude = "NA";
        if (mLocation.hasAltitude()) {
            sAltitude = Double.toString(mLocation.getAltitude());
        }

        String sAccuracy = "NA";
        if (mLocation.hasAccuracy()) {
            sAccuracy = Float.toString(mLocation.getAccuracy());
        }

        // Return concatenated string
        return sDateUTC + ITEM_SEP + sDateHuman + ITEM_SEP +
                sProvider + ITEM_SEP + sLatitude + ITEM_SEP + sLongitude + ITEM_SEP +
                sAltitude + ITEM_SEP + sAccuracy + ITEM_SEP + mTitle;

    }

    // String representation for log file
    public String toLog() {
        // Get date, latitude and longitude as strings
        // String sDate = FORMAT.format(Date date) where the location time is converted to Date

        Date newDate = new Date(mLocation.getTime());
        String sDateHuman = FORMAT.format(newDate); // Convert UTC time to human readable date/time

        String sLatitude = Location.convert(mLocation.getLatitude(),
                Location.FORMAT_SECONDS);
        String sLongitude = Location.convert(mLocation.getLatitude(),
                Location.FORMAT_SECONDS);

        // Return concatenated string
        return "Description: " + mTitle + ITEM_SEP +
                "Date: " + sDateHuman + ITEM_SEP +
                "Latitude: " + sLatitude + ITEM_SEP +
                "Longitude: " + sLongitude;
    }

    // Static method that gives a string with the descriptors of fields in the toString method
    public static String toStringHeaders() {

        // Return concatenated string
        return "Date(UTC)" + ITEM_SEP + "Date(Readable)" + ITEM_SEP +
                "LocationProvider" + ITEM_SEP +
                "Latitude(Degrees)" + ITEM_SEP + "Longitude(Degrees)" + ITEM_SEP +
                "Altitude(meters)" + ITEM_SEP + "Accuracy(meters)" + ITEM_SEP +
                "Description";

    }



}
