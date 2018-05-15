package com.example.sal.weatherapp;


import org.junit.Test;
import org.mockito.Mockito;

import static org.junit.Assert.*;

/**
 * Created by lwest2 on 14/05/2018.
 */
public class PlaceholderFragmentTest {

    // Edit: Some tests might be missing. Need to be added.


    @Test
    public void newInstance() throws Exception {
    }

    @Test
    public void onCreateView() throws Exception {
    }

    @Test
    public void editStartAndEnd() throws Exception {
    }

    @Test
    public void onClick() throws Exception {
        MainActivity mainActivity = Mockito.mock(MainActivity.class);
        mainActivity.findViewById(R.id.buttonSubmit);
    }

    @Test
    public void updateLists() throws Exception {
    }

    @Test
    public void outOfDate() throws Exception {
    }

    @Test
    public void sendFirebase() throws Exception {
    }

    @Test
    public void GetLocation() throws Exception {
    }

    @Test
    public void GetTemperature() {
        double temperature = 20.01313414;
        double expected = 20.01;
        double output = Math.round(temperature * 100.0) / 100.0;
        double delta = 0.001;

        assertEquals(expected, output, delta);
    }

    @Test
    public void GetPressure() throws Exception {

    }

    @Test
    public void Submit() throws Exception {

    }

    @Test
    public void onRequestPermissionsResult() throws Exception {
    }

    @Test
    public void onSensorChanged() throws Exception {

    }

    @Test
    public void onAccuracyChanged() throws Exception {
    }

    @Test
    public void onMapReady() throws Exception {
    }

    @Test
    public void addMarkerData() throws Exception {
    }

    @Test
    public void removeMarkerData() throws Exception {
    }

}