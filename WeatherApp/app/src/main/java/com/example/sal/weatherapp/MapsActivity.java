package com.example.sal.weatherapp;

import android.location.Location;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.Map;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    private GPSTracker gpsTracker;
    private Location mLocation;
    double latitude, longitude;
    static MapsActivity instance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        gpsTracker = new GPSTracker(getApplicationContext());
        mLocation = gpsTracker.getLocation();


        latitude = mLocation.getLatitude();
        longitude = mLocation.getLongitude();

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        instance = this;
    }

    public static MapsActivity getInstance()
    {
        return instance;
    }



    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng currentPosition = new LatLng(latitude, longitude);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(currentPosition));
    }

    public Marker AddMapMarker(double _lat, double _long, double _pressure, double _temp, String _condition, String _time)
    {
        Marker returnMarker = null;

        if ((_temp == 9999.9999) && (_pressure == 9999.9999))
        {
            returnMarker = mMap.addMarker(new MarkerOptions().position(new LatLng(_lat, _long)).snippet("Weather Condition: " + _condition).snippet("Time: " + _time));
        }
        else {
            if (_temp == 9999.9999) {
                returnMarker = mMap.addMarker(new MarkerOptions().position(new LatLng(_lat, _long)).snippet("Weather Condition: " + _condition).snippet("Pressure: " + _pressure).snippet("Time: " + _time));
            } else if (_pressure == 9999.9999){
                returnMarker = mMap.addMarker(new MarkerOptions().position(new LatLng(_lat, _long)).snippet("Weather Condition: " + _condition).snippet("Temperature: " + _temp).snippet("Time: " + _time));
            }
            else
            {
                returnMarker = mMap.addMarker(new MarkerOptions().position(new LatLng(_lat, _long)).snippet("Weather Condition: " + _condition).snippet("Temperature: " + _temp).snippet("Pressure: " + _pressure).snippet("Time: " + _time));
            }
        }

        return returnMarker;
    }

}
