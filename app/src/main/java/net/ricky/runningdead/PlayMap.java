package net.ricky.runningdead;

import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.os.Parcelable;
import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.animation.ObjectAnimator;
import android.animation.TypeEvaluator;
import android.animation.ValueAnimator;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.os.Build;
import android.os.Handler;
import android.os.SystemClock;
import android.provider.Settings;
import android.os.Bundle;
import android.util.Property;
import android.view.Menu;
import android.view.MenuItem;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Interpolator;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

<<<<<<< HEAD
import java.text.DecimalFormat;
=======
import java.text.DateFormat;
import java.util.Date;
>>>>>>> 3db7423a9fb8ec2f00f3599b675381bcb0bd18fd
import java.util.Random;
import java.util.Timer;

public class PlayMap extends Activity implements com.google.android.gms.location.LocationListener, GoogleMap.OnMapClickListener,
        OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, Runnable {
    GoogleMap googlemap;
    Location currentLocation;
    Location newLocation;
    GoogleApiClient apiClient;
<<<<<<< HEAD
    String string;
    double distanceT;
    boolean initByTag = false;
=======
    LocationRequest locationRequest;
    String mLastUpdateTime;
>>>>>>> 3db7423a9fb8ec2f00f3599b675381bcb0bd18fd

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_map);
        buildGoogleApiClient();
        Intent intent = getIntent();
        if(intent.getType() != null && intent.getType().equals("application/tag")) {
            Parcelable[] rawMsgs = getIntent().getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
            NdefMessage msg = (NdefMessage) rawMsgs[0];
            NdefRecord record = msg.getRecords()[0];
            string = new String(record.getPayload());
            initByTag = true;
        }
        //initMap();
    }

    private void placeCheckpoint() {
        if (initByTag == true) {
                googlemap.addMarker(new MarkerOptions().
                    position(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude())).
                    title(string).icon(BitmapDescriptorFactory.fromResource(R.drawable.checkpoint_icon)));

        }
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

<<<<<<< HEAD
    public void onLocationChanged(Location location) {
        googlemap.animateCamera(CameraUpdateFactory.
                newLatLngZoom(new LatLng(currentLocation.getLatitude(),
                        currentLocation.getLongitude()), 15));
        newLocation = currentLocation;
        calculateDistTravelled();
        System.out.println(currentLocation);
=======
>>>>>>> 3db7423a9fb8ec2f00f3599b675381bcb0bd18fd

    protected void createLocationRequest() {
        locationRequest = new LocationRequest();
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(5000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

<<<<<<< HEAD
=======
    protected void startLocationUpdates() {
        LocationServices.FusedLocationApi.
                requestLocationUpdates(apiClient, locationRequest, this);
    }
>>>>>>> 3db7423a9fb8ec2f00f3599b675381bcb0bd18fd


    @Override
    public void onLocationChanged(Location location) {
        currentLocation = location;
        mLastUpdateTime = DateFormat.getTimeInstance().format(new Date());
        updateUI();
    }

    private void updateUI() {
        System.out.println(currentLocation.toString());
        System.out.println(mLastUpdateTime);
    }

    protected synchronized void buildGoogleApiClient() {
        apiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        apiClient.connect();

    }

    //Initialise Map and also SetLocation to true for ActiveGPS Tracking
    private void initMap() {
        final MapFragment mf = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
        mf.getMapAsync(this);
    }

    @Override
    public void onMapClick(LatLng position) {

    }

    private void centreMapToLastLocation() {
        if (currentLocation != null) {
            googlemap.animateCamera(CameraUpdateFactory.
                    newLatLngZoom(new LatLng(currentLocation.getLatitude(),
                            currentLocation.getLongitude()), 15));
        }
    }

    private void mapReadyDraw() {
        if (currentLocation != null){
            for(int i=1; i<=10;i++) {
                if((i%3)==1) {
                    googlemap.addMarker(new MarkerOptions().
                            position(getZombieLocation(currentLocation.getLatitude(), currentLocation.getLongitude(), 100)).
                            title("Zombie "+i).icon(BitmapDescriptorFactory.fromResource(R.drawable.zombie1small)));
                    }else
                if((i%3)==2){
                    googlemap.addMarker(new MarkerOptions().
                            position(getZombieLocation(currentLocation.getLatitude(), currentLocation.getLongitude(), 100)).
                            title("Zombie "+i).icon(BitmapDescriptorFactory.fromResource(R.drawable.zombie2small)));
                }else
                if((i%3)==0){
                    googlemap.addMarker(new MarkerOptions().
                            position(getZombieLocation(currentLocation.getLatitude(), currentLocation.getLongitude(), 100)).
                            title("Zombie "+i).icon(BitmapDescriptorFactory.fromResource(R.drawable.zombie3small)));
                }
                }
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
        mapReadyDraw();
        placeCheckpoint();

        /*
        PolylineOptions rectOptions = new PolylineOptions()
                .add(new LatLng(-42.0000, 174.0000))
                .add(new LatLng(-35.3080, 149.1245));
        Polyline polyline = googlemap.addPolyline(rectOptions);
        */
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
        LatLng g = new LatLng(foundLongitude, foundLatitude);
        return g;
    }

    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    @Override
    public void onConnected(Bundle bundle) {
<<<<<<< HEAD
        currentLocation = LocationServices.FusedLocationApi.getLastLocation(apiClient);
        initMap();
        //placeMarker();
=======
        currentLocation = LocationServices.
                FusedLocationApi.getLastLocation(apiClient);
        initMap();
        startLocationUpdates();
>>>>>>> 3db7423a9fb8ec2f00f3599b675381bcb0bd18fd
    }

    @Override
    public void onConnectionSuspended(int i) {

    }
    public void calculateDistTravelled(){
        distanceT += calculateDistance(new LatLng(currentLocation.getLatitude(),currentLocation.getLongitude()),new LatLng(newLocation.getLatitude(),newLocation.getLongitude()));
        System.out.println(distanceT);
    }

    public double calculateDistance(LatLng StartP, LatLng EndP) {
        int Radius=6371;//radius of earth in Km
        double lat1 = StartP.latitude;
        double lat2 = EndP.latitude;
        double lon1 = StartP.longitude;
        double lon2 = EndP.longitude;
        double dLat = Math.toRadians(lat2-lat1);
        double dLon = Math.toRadians(lon2-lon1);
        double a = Math.sin(dLat/2) * Math.sin(dLat/2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(dLon/2) * Math.sin(dLon/2);
        double c = 2 * Math.asin(Math.sqrt(a));
        double valueResult= Radius*c;
        double km=valueResult/1;
        DecimalFormat newFormat = new DecimalFormat("####");
        int kmInDec =  Integer.valueOf(newFormat.format(km));
        double meter=valueResult%1000;
        int  meterInDec= Integer.valueOf(newFormat.format(meter));
        Log.i("Radius Value", "" + valueResult + "   KM  " + kmInDec + " Meter   " + meterInDec);

        return Radius * c;
    }







    /*
    public void checkpoint(){
        Location mylocation = currentLocation;
            googlemap.addMarker(new MarkerOptions().
                    position(new LatLng(mylocation.getLatitude(), mylocation.getLongitude())).
                    title("NFCtag").icon(BitmapDescriptorFactory.fromResource(R.drawable.checkpoint)));
    }*/


    //***********************ANIMATE MARKER CODE***********************
    static void animateMarkerToGB(final Marker marker, final LatLng finalPosition, final LatLngInterpolator latLngInterpolator) {
        final LatLng startPosition = marker.getPosition();
        final Handler handler = new Handler();
        final long start = SystemClock.uptimeMillis();
        final Interpolator interpolator = new AccelerateDecelerateInterpolator();
        final float durationInMs = 3000;

        handler.post(new Runnable() {
            long elapsed;
            float t;
            float v;

            @Override
            public void run() {
                // Calculate progress using interpolator
                elapsed = SystemClock.uptimeMillis() - start;
                t = elapsed / durationInMs;
                v = interpolator.getInterpolation(t);

                marker.setPosition(latLngInterpolator.interpolate(v, startPosition, finalPosition));

                // Repeat till progress is complete.
                if (t < 1) {
                    // Post again 16ms later.
                    handler.postDelayed(this, 16);
                }
            }
        });
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    static void animateMarkerToHC(final Marker marker, final LatLng finalPosition, final LatLngInterpolator latLngInterpolator) {
        final LatLng startPosition = marker.getPosition();

        ValueAnimator valueAnimator = new ValueAnimator();
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float v = animation.getAnimatedFraction();
                LatLng newPosition = latLngInterpolator.interpolate(v, startPosition, finalPosition);
                marker.setPosition(newPosition);
            }
        });
        valueAnimator.setFloatValues(0, 1); // Ignored.
        valueAnimator.setDuration(3000);
        valueAnimator.start();
    }

    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    static void animateMarkerToICS(Marker marker, LatLng finalPosition, final LatLngInterpolator latLngInterpolator) {
        TypeEvaluator<LatLng> typeEvaluator = new TypeEvaluator<LatLng>() {
            @Override
            public LatLng evaluate(float fraction, LatLng startValue, LatLng endValue) {
                return latLngInterpolator.interpolate(fraction, startValue, endValue);
            }
        };
        Property<Marker, LatLng> property = Property.of(Marker.class, LatLng.class, "position");
        ObjectAnimator animator = ObjectAnimator.ofObject(marker, property, typeEvaluator, finalPosition);
        animator.setDuration(3000);
        animator.start();
    }

    @Override
    public void run() {

    }
<<<<<<< HEAD




=======
>>>>>>> 3db7423a9fb8ec2f00f3599b675381bcb0bd18fd
}
