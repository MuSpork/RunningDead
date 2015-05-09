package net.ricky.runningdead;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.provider.Settings;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import java.util.Random;

public class PlayMap extends Activity implements LocationListener, GoogleMap.OnMapClickListener,
        OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    GoogleMap googlemap;
    Location currentLocation;
    GoogleApiClient apiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_map);
        buildGoogleApiClient();
        //initMap();
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

    protected synchronized void buildGoogleApiClient() {
        apiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        apiClient.connect();

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
        final MapFragment mf = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
        mf.getMapAsync(this);
    }

    @Override
    public void onMapClick(LatLng position) {
        Location mylocation = currentLocation;
        googlemap.addMarker(new MarkerOptions().
                position(getZombieLocation(mylocation.getLatitude(), mylocation.getLongitude(), 100)).
                title("Zombie").icon(BitmapDescriptorFactory.fromResource(R.drawable.zombie1small)));
    }


    private void centreMapToLastLocation() {
        if(currentLocation!= null) {
            googlemap.animateCamera(CameraUpdateFactory.
                    newLatLngZoom(new LatLng(currentLocation.getLatitude(),
                            currentLocation.getLongitude()), 15));
        }
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        googlemap = googleMap;
        googlemap.setMyLocationEnabled(true);
        googlemap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        googlemap.setOnMapClickListener(this);
        // Instantiates a new Polyline object and adds points to define a rectangle
        centreMapToLastLocation();
        PolylineOptions rectOptions = new PolylineOptions()
                .add(new LatLng(-42.0000, 174.0000))
                .add(new LatLng(-35.3080, 149.1245));
        Polyline polyline = googlemap.addPolyline(rectOptions);
    }

    public static LatLng getZombieLocation(double x0, double y0, int radius) {
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
        double newX = x / Math.cos(y0);

        double foundLongitude = newX + x0;
        double foundLatitude = y + y0;
        LatLng g = new LatLng(foundLongitude,foundLatitude);
        return  g;
    }

    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    @Override
    public void onConnected(Bundle bundle) {
        currentLocation = LocationServices.FusedLocationApi.getLastLocation(apiClient);;
        initMap();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }
    /*
    public void checkpoint(){
        Location mylocation = currentLocation;
            googlemap.addMarker(new MarkerOptions().
                    position(new LatLng(mylocation.getLatitude(), mylocation.getLongitude())).
                    title("NFCtag").icon(BitmapDescriptorFactory.fromResource(R.drawable.checkpoint)));
    }*/

}

