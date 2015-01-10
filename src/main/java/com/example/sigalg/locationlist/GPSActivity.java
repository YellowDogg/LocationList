package com.example.sigalg.locationlist;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.text.ParseException;
import java.util.Date;


public class GPSActivity extends ListActivity {

    // Add constants
    private static final int ADD_GPS_ITEM_REQUEST = 0; // Request code
    private static final String FILE_NAME = "GPSCoordData.txt"; // Private archive file name
    private static final String EXPORT_PREFIX = "RangerBook"; // Prefix for export files
    private static final String TAG = "Location-ListInterface"; // TAG for log file
    private static final int MENU_DELETE = Menu.FIRST; // ID for delete menu item
    private static final int MENU_DUMP = Menu.FIRST + 1; // ID for dump menu item
    private static final int MENU_SAVE = Menu.FIRST + 2; // ID for export menu item

    GPSListAdapter mAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Create a new GPSListAdapter for this ListActivity's ListView
        mAdapter = new GPSListAdapter(getApplicationContext());

        // Put divider between ToDoItems and FooterView
        getListView().setFooterDividersEnabled(true);

        // Inflate footerView for footer_view.xml file then add footerView to ListView
        // The Inflator.inflate() method matches the java view instance to the xml layout format
        LayoutInflater inflater = LayoutInflater.from(getApplicationContext());
        TextView footerView = (TextView) inflater.inflate(R.layout.footer_view, null);
        getListView().addFooterView(footerView);

        // Attach Listener to FooterView and implement onClick()
        footerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                log("Entered footerView.OnClickListener.onClick()");

                Intent intent = new Intent(getApplicationContext(), AddGPSActivity.class);
                startActivityForResult(intent, ADD_GPS_ITEM_REQUEST);
            }
        });

        // Attach the adapter to this ListActivity's ListView
        getListView().setAdapter(mAdapter);

    }

    // On receiving result from the footerView's Listener, add GPSItem to display
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        log("Entered onActivityResult()");

        // Check request code to make sure it is the right request
        // and check result code to make sure a result is being returned.
        // If user submitted a new ToDoItem
        // If user submitted a new ToDoItem
        // Create a new ToDoItem from the data Intent
        // and then add it to the adapter

        if (requestCode == ADD_GPS_ITEM_REQUEST) {
            if (resultCode == RESULT_OK) {
                GPSItem newGPSItem = new GPSItem(data);
                mAdapter.add(newGPSItem);
            }
        }

    }

    @Override
    public void onResume() {
        super.onResume();

        // Load saved GPSItems, if necessary

        if (mAdapter.getCount() == 0)
            loadItems();
    }

    @Override
    protected void onPause() {
        super.onPause();

        // Save GPSItems for later use
        saveItems();

    }


    // Create menu options for deleting and dumping (to log) the displayed items
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        menu.add(Menu.NONE, MENU_DELETE, Menu.NONE, "Delete all");
        menu.add(Menu.NONE, MENU_DUMP, Menu.NONE, "Dump to log");
        menu.add(Menu.NONE, MENU_SAVE, Menu.NONE, "Email Data as CSV File");
        return true;
    }

    // Actions to take if menu items are selected
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // For Delete option, clear all items from list adapter
            case MENU_DELETE:
                mAdapter.clear();
                return true;
            // For Dump option, dump all list information to log
            case MENU_DUMP:
                dump();
                return true;
            case MENU_SAVE:
                sendMail(exportItems());
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    // Dump all items in list adaptor to log
    private void dump() {

        for (int i = 0; i < mAdapter.getCount(); i++) {
            String data = ((GPSItem) mAdapter.getItem(i)).toLog();
            log("Item " + i + ": " + data.replace(GPSItem.ITEM_SEP, ","));
        }

    }

    // Load stored GPSItems
    private void loadItems() {
        BufferedReader reader = null;
        try {

            // Open file and start BufferedReader instance for reading file
            FileInputStream fis = openFileInput(FILE_NAME);
            reader = new BufferedReader(new InputStreamReader(fis));

            String fileLine;
            String title;
            Location location;


            // For each GPSItem, the file has a line with the following info in order:
            //  Date (UTC), Date (human-readable), provider, latitude (degrees),
            //      longitude (degrees), altitude (meters), accuracy (meters), description
            while (null != (fileLine = reader.readLine())) {

                // Divide line into string array based on seprator ITEM_SEP
                String[] fileLineParts = fileLine.split(GPSItem.ITEM_SEP);

                // Create location and populate with data
                location = new Location("No Provider");
                if (!fileLineParts[0].equals("NA")) { location.setProvider(fileLineParts[0]); }
                location.setTime(Long.valueOf(fileLineParts[0]));
                location.setLatitude(Double.valueOf(fileLineParts[3]));
                location.setLongitude(Double.valueOf(fileLineParts[4]));
                if (!fileLineParts[5].equals("NA")) {
                    location.setAltitude(Double.valueOf(fileLineParts[5]));
                }
                if (!fileLineParts[6].equals("NA")) {
                    location.setAccuracy(Float.valueOf(fileLineParts[6]));
                }

                title = fileLineParts[7];

                // Create GPSItem from title and location and add to GPSListAdaptor
                mAdapter.add(new GPSItem(title, location));
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
//        } catch (ParseException e) {
//            e.printStackTrace();
        } finally {
            if (null != reader) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    // Archive GPSItems to private file to provide data permanence
    private void saveItems() {
        PrintWriter writer = null;
        try {
            // Create the file and PrintWriter instance for writing data
            FileOutputStream fos = openFileOutput(FILE_NAME, MODE_PRIVATE);
            writer = new PrintWriter(new BufferedWriter(new OutputStreamWriter(
                    fos)));

            // Need to iterate through all locations in mAdapater
            for (int idx = 0; idx < mAdapter.getCount(); idx++) {

                // Get the GPSItem a position idx and print (will use toString method from GPSItem)
                // Note: all location info except longitude and latitude is lost
                writer.println(mAdapter.getItem(idx));

            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (null != writer) {
                writer.close();
            }
        }
    }

    // Export GPSItems to file on external memory (will use to export data for other uses)
    private Uri exportItems() {

        // Create string for file name based on EXPORT_PREFIX and current time stamp
        Date newDate = new Date(); // Generate current date
        String fileName = GPSItem.FORMAT.format(newDate); // Human readable date/time format
        fileName = fileName.replaceAll("[\\W]", ""); // Delete characters except a-z, A-Z, 0-9
        fileName = EXPORT_PREFIX + "_" + fileName + ".csv";

        // Get File object  for desired file in appropriate external director for context
        File exportFile = new File(((Context)this).getExternalFilesDir(null), fileName);
        log("Export File Path = " + exportFile.getAbsolutePath());

        PrintWriter writer = null;

        try {

            // Create BufferedWriter instance for writing data
            writer = new PrintWriter(new BufferedWriter(new FileWriter(exportFile)));

            // Write heading file
            writer.println(GPSItem.toStringHeaders());

            // Need to iterate through all locations in mAdapater
            for (int idx = 0; idx < mAdapter.getCount(); idx++) {

                // Get the GPSItem a position idx and print (will use toString method from GPSItem)
                // Note: all location info except longitude and latitude is lost
                writer.println(mAdapter.getItem(idx));

            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (null != writer) {
                writer.close();
            }
        }

        return Uri.fromFile(exportFile);
    }

    // Attaches exported csv file to e-mail
    // Note:  Not checking to make sure file exists
    private void sendMail(Uri uri) {

        // Create ACTION_SEND intent which causes e-mail message to be created in e-mail app
        Intent i = new Intent(Intent.ACTION_SEND);
        i.putExtra(Intent.EXTRA_SUBJECT, "Range Book Data File");
        i.putExtra(Intent.EXTRA_TEXT, "See attached csv file");
        i.putExtra(Intent.EXTRA_STREAM, uri);
        i.setType("text/plain");
        startActivity(Intent.createChooser(i, "Send mail"));
    }

    public static void log(String msg) {
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Log.i(TAG, msg);
    }

}
