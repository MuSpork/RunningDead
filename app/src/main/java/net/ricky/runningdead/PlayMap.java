package net.ricky.runningdead;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.provider.Settings;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.apache.http.io.SessionOutputBuffer;

import java.util.Random;


public class PlayMap extends ActionBarActivity implements LocationListener, GoogleMap.OnMapClickListener, OnMapReadyCallback {
    GoogleMap googlemap;
    int clickCount = 0;
    int clickbtn = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_map);
        initMap();

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_play_map, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void onLocationChanged(Location location) {

    }

    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    public void onProviderEnabled(String provider) {

    }

    //Create an AlertDialog if GPS is not on.
    public void onProviderDisabled(String provider) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("GPS is disabled");
        builder.setCancelable(false);
        builder.setPositiveButton("Enable GPS", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent startGps = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(startGps);
            }
        });
        builder.setNegativeButton("Leave GPS off", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        AlertDialog alert = builder.create();
        alert.show();
    }


    //Initialise Map and also SetLocation to true for ActiveGPS Tracking
    private void initMap() {
        final SupportMapFragment mf = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mf.getMapAsync(this);

    }

    public LatLng getLastLocation(){
        Location mylocation = googlemap.getMyLocation();

        if(mylocation != null) {
            double x = mylocation.getLongitude();
            double y = mylocation.getLatitude();
            LatLng lastLocation = new LatLng(x,y);

            return lastLocation;
        }else
        return new LatLng(-36.8,174.6);
    }

    @Override
    public void onMapClick(LatLng position) {
        Location mylocation = googlemap.getMyLocation();

        System.out.println("Longitude: " + mylocation.getLongitude() + " Latitude: " + mylocation.getLatitude());
        getZomLocation(mylocation.getLongitude(),mylocation.getLatitude(),100);

        googlemap.addMarker(new MarkerOptions().position(position).title("Zombie").icon(BitmapDescriptorFactory.fromResource(R.drawable.zombie1small)));

        googlemap.addMarker(new MarkerOptions().position(getZomLocation(mylocation.getLongitude(),mylocation.getLatitude(),100)).title("Zombie").icon(BitmapDescriptorFactory.fromResource(R.drawable.zombie1small)));
    }


    private void centreMapToLastLocation() {
    googlemap.animateCamera(CameraUpdateFactory.newLatLngZoom(getLastLocation(),3));

    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        googleMap.setMyLocationEnabled(true);
        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        googleMap.setOnMapClickListener(this);
        googlemap = googleMap;
        centreMapToLastLocation();
        }

    public static LatLng getZomLocation( double x0, double y0, int radius) {
        Random random = new Random();
        // Convert radius from meters to degrees
        double radiusInDegrees = radius / 111000f;

        double u = random.nextDouble();
        double v = random.nextDouble();
        double w = radiusInDegrees * Math.sqrt(u);
        double t = 2 * Math.PI * v;
        double x = w * Math.cos(t);
        double y = w * Math.sin(t);

        // Adjust the x-coordinate for the shrinking of the east-west distances
        double new_x = x / Math.cos(y0);

        double foundLongitude = new_x + x0;
        double foundLatitude = y + y0;

        System.out.println("Longitude: " + foundLongitude + "  Latitude: " + foundLatitude);

        LatLng coords = new LatLng(foundLongitude, foundLatitude);

        return coords;
    }


}

