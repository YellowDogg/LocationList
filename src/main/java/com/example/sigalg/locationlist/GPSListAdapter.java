package com.example.sigalg.locationlist;

import android.content.Context;
import android.location.Location;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by sigalg on 12/31/2014.
 * Creates the list view used to display saved GPS location results
 */

// TODO Add code to expand display of GPSItem to include picture
    // In list view, display as Uri (in TextView)
    // Add onClickListener so that when someone licks on the Uri, the picture is displayed
    // Don't forget to update the layout xml


public class GPSListAdapter extends BaseAdapter {

    // List of GPSItems
    private final List<GPSItem> mItems = new ArrayList<GPSItem>();

    // Context associated with the created view
    private final Context mContext;

    // Format for displaying date information
    public final static SimpleDateFormat FORMAT = new SimpleDateFormat(
            "yyy-MM-dd HH:mm:ss", Locale.US);

    // Create GPSList Adapter instance
    public GPSListAdapter(Context context) {

        mContext = context;

    }

    // Add a GPSItem to the adapter
    // Notify observers that the data set has changed
    public void add(GPSItem item) {

        mItems.add(item);
        notifyDataSetChanged();

    }

    // Clears the list adapter of all items.
    public void clear(){

        mItems.clear();
        notifyDataSetChanged();

    }

    // Required methods for BaseAdaptor class

    // Get number of items in list
    @Override
    public int getCount() {
        return mItems.size();
    }

    // Return the item in positions number "position"
    @Override
    public Object getItem(int position) {
        return mItems.get(position);
    }

    // Return the ID for item in position number "position"
    @Override
    public long getItemId(int position) {
        return position;
    }

    // Create a view to display the GPSItem at a specified position
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        GPSActivity.log("Entered getView for GPSListAdapter");

        // Get the current GPS item
        final GPSItem gpsItem = (GPSItem) this.getItem(position);

        // Inflate the View for this GPSItem
        // from gps_item.xml.
        // the LayoutInflater.inflate method matches itemLayout to the fields in the xml layout file
        LayoutInflater inflater = LayoutInflater.from(mContext);
        RelativeLayout itemLayout = (RelativeLayout) inflater.inflate(R.layout.gps_item, null);

        // Display description (title) in appropriate TextView
        // Find the view then set the text to match the value in gpsItem
        final TextView titleView = (TextView) itemLayout.findViewById(R.id.titleView);
        titleView.setText(gpsItem.getTitle());

        // Display date in appropriate TextView
        // Find the view then set the text to match the value in gpsItem
        final TextView dateView = (TextView) itemLayout.findViewById(R.id.dateView);
        //dateView.setText(FORMAT.format(gpsItem.getDate()));
        Date tempDate = new Date(gpsItem.getLocation().getTime());
        String tempString = FORMAT.format(tempDate);
        dateView.setText(tempString);

        // Display latitude in appropriate TextView
        // Find the view then set the text to match the value in gpsItem
        final TextView latitudeView = (TextView) itemLayout.findViewById(R.id.latitudeView);
        latitudeView.setText(Location.convert(gpsItem.getLocation().getLatitude(),
                Location.FORMAT_SECONDS));

        // Display longitude in appropriate TextView
        // Find the view then set the text to match the value in gpsItem
        final TextView longitudeView = (TextView) itemLayout.findViewById(R.id.longitudeView);
        longitudeView.setText(Location.convert(gpsItem.getLocation().getLongitude(),
                Location.FORMAT_SECONDS));

        // Return the View just created; first set background color
        itemLayout.setBackgroundColor(mContext.getResources().getColor(R.color.background_color));
        return itemLayout;

    }

}
