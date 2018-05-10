package com.example.sal.weatherapp;

import android.location.Location;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    private GPSTracker gpsTracker;
    private Location mLocation;
    double latitude, longitude;

    private static List<Double> m_Lats = new ArrayList<>();
    private static List<Double> m_Longs = new ArrayList<>();
    private static List<Double> m_Pressures = new ArrayList<>();
    private static List<Double> m_Temps = new ArrayList<>();
    private static List<String> m_Conditions = new ArrayList<>();
    private static List<String> m_Times = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        gpsTracker = new GPSTracker(getApplicationContext());
        mLocation = gpsTracker.getLocation();


        latitude = mLocation.getLatitude();
        longitude = mLocation.getLongitude();

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    public static void AddMarkerData(Double _lat, Double _long, Double _pressure, Double _temp, String _condition, String _time)
    {
        m_Lats.add(_lat);
        m_Longs.add(_long);
        m_Pressures.add(_pressure);
        m_Temps.add(_temp);
        m_Conditions.add(_condition);
        m_Times.add(_time);
    }

    public static void RemoveMarkerData(int pointer)
    {
        m_Lats.remove(pointer);
        m_Longs.remove(pointer);
        m_Pressures.remove(pointer);
        m_Temps.remove(pointer);
        m_Conditions.remove(pointer);
        m_Times.remove(pointer);
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

        Log.d("Got into On Map Ready", "stuff");

        SetupMarkers();
    }

    private void SetupMarkers()
    {
        Log.d("num of markers", ""+ m_Lats.size());
        for (int i = 0; i < m_Lats.size(); i++)
        {
            AddMapMarker(m_Lats.get(i),m_Longs.get(i),m_Pressures.get(i),m_Temps.get(i),m_Conditions.get(i),m_Times.get(i));
        }
    }

    private void AddMapMarker(Double _lat, Double _long, Double _pressure, Double _temp, String _condition, String _time)
    {
        Log.d("Got in to add marker", "yay?");
        Marker marker;

        if ((_temp == 9999.9999) && (_pressure == 9999.9999))
        {
            marker = mMap.addMarker(new MarkerOptions().position(new LatLng(_lat, _long)).title("Weather Condition: " + _condition).snippet("Time: " + _time));
        }
        else {
            if (_temp == 9999.9999) {
                marker = mMap.addMarker(new MarkerOptions().position(new LatLng(_lat, _long)).title("Weather Condition: " + _condition).snippet("Pressure: " + _pressure+ " Time: " + _time));
            } else if (_pressure == 9999.9999){
                marker = mMap.addMarker(new MarkerOptions().position(new LatLng(_lat, _long)).title("Weather Condition: " + _condition).snippet("Temperature: " + _temp+ " Time: " + _time));
            }
            else
            {
                marker = mMap.addMarker(new MarkerOptions().position(new LatLng(_lat, _long)).title("Weather Condition: " + _condition).snippet("Temperature: " + _temp+ " Pressure: " + _pressure+ " Time: " + _time));
            }
        }
    }

}
