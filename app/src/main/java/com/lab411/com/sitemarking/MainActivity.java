package com.lab411.com.sitemarking;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.hardware.SensorListener;
import android.location.Location;
import android.os.Bundle;
import android.os.Vibrator;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener,SensorListener {
    MapView mMapView;
    private GoogleMap googleMap;
    MarkerOptions marker=null;
    LocationRequest mLocationRequest;
    GoogleApiClient mGoogleApiClient;
    Boolean check= true;
    LatLng latLng,Marker_latLng;
    GoogleMap mGoogleMap;
    SupportMapFragment mFragment;
    Marker currLocationMarker;
    Bitmap bmp;
    TextView tv_say;
    Button btn_tb;
    private ShakeListener mShaker;
    @Override
    public void onSensorChanged(int i, float[] floats) {

    }

    @Override
    public void onAccuracyChanged(int i, int i1) {

    }

    class MyInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {

        private final View myContentsView;

        MyInfoWindowAdapter(){
            myContentsView = getLayoutInflater().inflate(R.layout.custom_info_contents, null);
        }

        @Override
        public View getInfoContents(Marker marker) {

            TextView tvTitle = ((TextView)myContentsView.findViewById(R.id.title));
            tvTitle.setText("Warning");
            TextView tvSnippet = ((TextView)myContentsView.findViewById(R.id.snippet));
            tvSnippet.setText("!DANGEROUS!");

            return myContentsView;
        }

        @Override
        public View getInfoWindow(Marker marker) {
            // TODO Auto-generated method stub
            return null;
        }

    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().addFlags(
                WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD |
                        WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |
                        WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
        tv_say= (TextView)findViewById(R.id.tv_say);
        btn_tb=(Button)findViewById(R.id.btn_tb);
        mFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapView);
        mFragment.getMapAsync(this);
        final Vibrator vibe = (Vibrator)getSystemService(Context.VIBRATOR_SERVICE);
        if(SpeechRecognizer.isRecognitionAvailable(this)){
            Toast.makeText(MainActivity.this," SUPPOST",Toast.LENGTH_SHORT).show();
        }else     Toast.makeText(MainActivity.this,"NOT SUPPOST1",Toast.LENGTH_SHORT).show();
        mShaker = new ShakeListener(this);
        mShaker.setOnShakeListener(new ShakeListener.OnShakeListener() {
            public void onShake() {
                vibe.vibrate(100);
                Toast.makeText(getApplicationContext(),"Shaking",Toast.LENGTH_SHORT).show();
                if(Marker_latLng!=null) {
                    marker = new MarkerOptions().position(
                            new LatLng(Marker_latLng.latitude, Marker_latLng.longitude));

                    // Changing marker icon
                    //  MapsInitializer.initialize(getActivity().getApplicationContext());
                    marker.icon(BitmapDescriptorFactory
                            .defaultMarker(BitmapDescriptorFactory.HUE_YELLOW));

                    // adding marker
                    mGoogleMap.addMarker(marker);
                }
            }
        });



    }
    public void onButtonClick(View v){
        if(v.getId()==R.id.btn_say){
            SpeedchInput();
        }
    }
    public void SpeedchInput(){
        Intent i = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        i.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        i.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        i.putExtra(RecognizerIntent.EXTRA_PROMPT,"Say somthing");
        try{
            startActivityForResult(i,100);
        }catch (ActivityNotFoundException a){
            Toast.makeText(MainActivity.this,"NOT SUPPOST",Toast.LENGTH_SHORT).show();
        }
    }
    public void onActivityResult(int requestCode, int resultCode, Intent i){
        super.onActivityResult(requestCode, resultCode, i);
        switch (requestCode)
        {
            case 100: if ((resultCode== RESULT_OK) && i!=null)
            {
                ArrayList<String>result=i.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                tv_say.setText(result.get(0));
                if(result.get(0).equals("ok")){
                    if(Marker_latLng!=null) {
                        marker = new MarkerOptions().position(
                                new LatLng(Marker_latLng.latitude, Marker_latLng.longitude));

                        // Changing marker icon
                        //  MapsInitializer.initialize(getActivity().getApplicationContext());
                        marker.icon(BitmapDescriptorFactory
                                .defaultMarker(BitmapDescriptorFactory.HUE_RED));

                        // adding marker
                        mGoogleMap.addMarker(marker);
                        btn_tb.setText("Thông báo");
                    }

                } else {
                    btn_tb.setText("Không đúng");
                    Toast.makeText(MainActivity.this,"Không đúng",Toast.LENGTH_SHORT).show();
                }
            }
                break;
            default:break;
        }
    }


    protected synchronized void buildGoogleApiClient() {
        Toast.makeText(this,"buildGoogleApiClient",Toast.LENGTH_SHORT).show();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    @Override
    public void onConnected(Bundle bundle) {
        Toast.makeText(this,"onConnected",Toast.LENGTH_SHORT).show();
        Location mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);
        if (mLastLocation != null) {
            //place marker at current position
            //mGoogleMap.clear();
            latLng = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position(latLng);
            markerOptions.title("Current Position");

            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));
            currLocationMarker = mGoogleMap.addMarker(markerOptions);
            mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
        }
        mGoogleMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng1) {
                marker = new MarkerOptions().position(
                        new LatLng(latLng1.latitude, latLng1.longitude));

                // Changing marker icon
                //  MapsInitializer.initialize(getActivity().getApplicationContext());
                marker.icon(BitmapDescriptorFactory
                        .defaultMarker(BitmapDescriptorFactory.HUE_GREEN));

                // adding marker
               mGoogleMap.addMarker(marker);

            }
        });
//        mGoogleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
//            @Override
//            public boolean onMarkerClick(Marker marker) {
//
//                if(check) {
//                    marker.setVisible(true);
//                    mGoogleMap.addMarker(new MarkerOptions().position(marker.getPosition()).title("Warning").snippet("!DANGEROUS!")
//                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.thesun))
//                                    // Specifies the anchor to be at a particular point in the marker image.
//                            .anchor(0.5f, 1));
//                    check=!check;
//                }
//                else{
//                   marker.setVisible(false);
//                    check=!check;
//                }
//
//                return true;
//            }
//
//        });
        mGoogleMap.setInfoWindowAdapter(new MyInfoWindowAdapter());
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(5000); //5 seconds
        mLocationRequest.setFastestInterval(3000); //3 seconds
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        //mLocationRequest.setSmallestDisplacement(0.1F); //1/10 meter

        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);



    }

    @Override
    public void onConnectionSuspended(int i) {
        Toast.makeText(this,"onConnectionSuspended",Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Toast.makeText(this,"onConnectionFailed",Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onLocationChanged(Location location) {

        //place marker at current position
        //mGoogleMap.clear();
        if (currLocationMarker != null) {
            currLocationMarker.remove();
        }
        latLng = new LatLng(location.getLatitude(), location.getLongitude());
        Marker_latLng= latLng;
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.title("Current Position");
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));
        currLocationMarker = mGoogleMap.addMarker(markerOptions);

        //Toast.makeText(this,"Location Changed",Toast.LENGTH_SHORT).show();

        //zoom to current position:
      //  mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 11));

        //If you only need one location, unregister the listener
        //LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;
        mGoogleMap.setMyLocationEnabled(true);

        buildGoogleApiClient();

        mGoogleApiClient.connect();

    }
}
