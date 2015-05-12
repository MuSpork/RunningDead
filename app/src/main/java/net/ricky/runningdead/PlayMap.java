package net.ricky.runningdead;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

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
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Random;

public class PlayMap extends Activity implements com.google.android.gms.location.LocationListener,
        OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    GoogleMap googlemap;
    Location currentLocation;
    GoogleApiClient apiClient;
    LocationRequest locationRequest;
    NfcAdapter nfcAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_map);
        buildGoogleApiClient();
        nfcAdapter = NfcAdapter.getDefaultAdapter(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_play_map, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        String title = readFromTag(intent);
        LocationNFC bhoo = new LocationNFC(title);
        bhoo.execute(buildParameter(title.substring(title.indexOf(" ")+1)));
    }

    public void makeToast(String title){
        Toast.makeText(this,title,Toast.LENGTH_LONG).show();
    }

    public String buildParameter(String checkPointNumber){
        System.out.println("dd"+checkPointNumber);
        return "lat="+currentLocation.getLatitude()+"&"
                +"long="+currentLocation.getLongitude()+"&"+
                "checkpoint="+checkPointNumber;

    }

    public String readFromTag(Intent intent){
        String str="";
        if(intent.getType() != null && intent.getType().equals("application/tag")) {
            Parcelable[] rawMsgs =intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
            NdefMessage msg = (NdefMessage) rawMsgs[0];
            NdefRecord record = msg.getRecords()[0];
            str = new String(record.getPayload());
        }
        return str;
    }

    public void drawCheckpoint(String title){
        googlemap.addMarker(new MarkerOptions().
                position(new LatLng(currentLocation.getLatitude(),currentLocation.getLongitude())).
                title(title).icon(BitmapDescriptorFactory.fromResource(R.drawable.checkpoint_icon)));
    }

    protected void createLocationRequest() {
        locationRequest = new LocationRequest();
        locationRequest.setInterval(3000);
        locationRequest.setFastestInterval(1000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    protected void startLocationUpdates() {
        createLocationRequest();
        LocationServices.FusedLocationApi.
                requestLocationUpdates(apiClient, locationRequest, this);
    }

    protected void stopLocationUpdates() {
        LocationServices.FusedLocationApi.removeLocationUpdates(
                apiClient, this);
    }

    @Override
    public void onLocationChanged(Location location) {
        currentLocation = location;
        drawTrail();
        centreMapToLastLocation();
    }


    @Override
    protected void onPause() {
        super.onPause();
        stopLocationUpdates();
        nfcAdapter.disableForegroundDispatch(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (apiClient.isConnected()) {
            startLocationUpdates();
        }

        updateDb();
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

    private void centreMapToLastLocation() {
        if (currentLocation != null) {
            googlemap.animateCamera(CameraUpdateFactory.
                    newLatLngZoom(new LatLng(currentLocation.getLatitude(),
                            currentLocation.getLongitude()), 19));
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
        centreMapToLastLocation();
        mapReadyDraw();
    }

    public void drawTrail(){
        PolylineOptions options = new PolylineOptions();
        options.add(new LatLng(currentLocation.getLatitude(),currentLocation.getLongitude()));
        googlemap.addPolyline(options);
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
        return new LatLng(foundLongitude, foundLatitude);
    }

    public void onConnectionFailed(ConnectionResult connectionResult) {
    }

    @Override
    public void onConnected(Bundle bundle) {
        currentLocation = LocationServices.
                FusedLocationApi.getLastLocation(apiClient);
        initMap();
        startLocationUpdates();
    }

    private void updateDb() {
        // set up a PendingIntent to open the app when a tag is scanned
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
        IntentFilter tagDetected = new IntentFilter(NfcAdapter.ACTION_NDEF_DISCOVERED);
        try {
            tagDetected.addDataType("*/*");
        } catch (IntentFilter.MalformedMimeTypeException e) {
            e.printStackTrace();
        }
        IntentFilter[] filters = new IntentFilter[] { tagDetected };

        nfcAdapter.enableForegroundDispatch(this, pendingIntent, filters, null);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    public class LocationNFC extends AsyncTask<String, Void, Boolean>{
        private String checkPoint;
        String request = "http://rickx.ddns.net/LocationServ";
        URL url = null;
        HttpURLConnection connection = null;

        public LocationNFC(String checkPoint) {
            this.checkPoint = checkPoint;
        }

        @Override
        protected Boolean doInBackground(String... params) {
            try {
                url = new URL(request);
                connection = (HttpURLConnection) url.openConnection();
                sendPost(params[0]);
                readFromServlet();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return true;
        }

        private String readFromServlet() throws IOException {
            BufferedReader stream = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            return stream.readLine();
        }

        private void sendPost(String message) throws IOException {
            connection.setRequestMethod("POST");
            connection.setRequestProperty( "Content-Type", "application/x-www-form-urlencoded");
            connection.setRequestProperty( "charset", "utf-8");
            connection.setDoOutput(true);
            OutputStreamWriter wr = new OutputStreamWriter(connection.getOutputStream());
            wr.write(message);
            wr.flush();
            wr.close();
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            makeToast("Updated Database with current Location\n"+checkPoint);
            drawCheckpoint(checkPoint);
        }
    }
}
