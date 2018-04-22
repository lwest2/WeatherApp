package com.example.sal.weatherapp;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.text.DecimalFormat;

public class MainActivity extends AppCompatActivity {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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

    public static boolean hasPermissions(Context context, String... permissions)
    {
        if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.M && context != null && permissions != null)
        {
            for (String permission: permissions)
            {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED)
                {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment implements View.OnClickListener, SensorEventListener {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        public PlaceholderFragment() {

        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        View rootView;
        private Button m_buttonGetTemperature;
        private Button m_buttonGetPressure;
        private Button m_buttonGetLocation;
        private Button m_buttonSubmit;

        private Spinner m_spinnerGetWeather;

        private TextView m_textViewTemperatureOutput;
        private TextView m_textViewPressureOutput;
        private TextView m_textViewLatitudeOutput;
        private TextView m_textViewLongitudeOutput;

        private LocationManager m_locationManager;

        private static final int REQUEST_LOCATION = 1;

        private SensorManager m_sensorManager;
        private Sensor m_temperature;
        private float m_ambientTemperature;
        private Sensor m_pressure;
        private float m_bioPressure;

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {

            switch (getArguments().getInt(ARG_SECTION_NUMBER)) {
                case 1:
                    rootView = inflater.inflate(R.layout.fragment_main, container, false);

                    m_buttonGetTemperature = (Button) rootView.findViewById(R.id.buttonGetTemparture);
                    m_buttonGetPressure = (Button) rootView.findViewById(R.id.buttonGetPressure);
                    m_buttonGetLocation = (Button) rootView.findViewById(R.id.buttonGetLocation);
                    m_buttonSubmit = (Button) rootView.findViewById(R.id.buttonSubmit);

                    m_spinnerGetWeather = (Spinner) rootView.findViewById(R.id.spinnerGetWeather);

                    m_textViewTemperatureOutput = (TextView) rootView.findViewById(R.id.textViewTemperatureOutput);
                    m_textViewPressureOutput = (TextView) rootView.findViewById(R.id.textViewPressureOutput);
                    m_textViewLongitudeOutput = (TextView) rootView.findViewById(R.id.textViewLongitudeOutput);
                    m_textViewLatitudeOutput = (TextView) rootView.findViewById(R.id.textViewLatitudeOutput);

                    m_locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
                    m_sensorManager = (SensorManager) getActivity().getSystemService(Context.SENSOR_SERVICE);
                    m_temperature = m_sensorManager.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE);
                    m_pressure = m_sensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE);

                    m_sensorManager.registerListener(this, m_temperature, SensorManager.SENSOR_DELAY_NORMAL);
                    m_sensorManager.registerListener(this, m_pressure, SensorManager.SENSOR_DELAY_NORMAL);

                    m_buttonSubmit.setOnClickListener(this);
                    m_buttonGetLocation.setOnClickListener(this);
                    m_buttonGetPressure.setOnClickListener(this);
                    m_buttonGetTemperature.setOnClickListener(this);

                    break;

                case 2:
                    rootView = inflater.inflate(R.layout.fragment_mapview, container, false);
                    break;
            }

            return rootView;
        }

        @Override
        public void onClick(View view) {
            int id = view.getId();

            switch (id) {
                case R.id.buttonGetLocation:
                    // code to set location
                    GetLocation();
                    break;

                case R.id.buttonGetTemparture:
                    // code to set temperature
                    GetTemperature();
                    break;

                case R.id.buttonGetPressure:
                    // code to set pressure
                    GetPressure();
                    break;

                case R.id.buttonSubmit:
                    // code to submit
                    Submit();
                    break;
            }

        }


        private void GetLocation() {
            // check permissions
            if(ActivityCompat.checkSelfPermission(this.getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(this.getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this.getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);
            } else
            {
                Location location = m_locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

                if (location != null)
                {
                    double longitude = location.getLongitude();
                    double latitude = location.getLatitude();

                    m_textViewLongitudeOutput.setText(Double.toString(longitude));
                    m_textViewLatitudeOutput.setText(Double.toString(latitude));
                } else {
                    m_textViewLongitudeOutput.setText(R.string.NoLongitude);
                    m_textViewLatitudeOutput.setText(R.string.NoLatitude);
                }
            }
        }

        private void GetTemperature() {
            if (m_temperature != null)
            {
                double roundedNumber = Math.round(m_ambientTemperature * 100.0) / 100.0;
                m_textViewTemperatureOutput.setText((Double.toString(roundedNumber)) + " " + getResources().getString(R.string.Celsius));
            }
            else
            {
                m_textViewTemperatureOutput.setText(R.string.NoTemperature);
            }
        }

        private void GetPressure(){
            if (m_pressure != null)
            {
                m_textViewPressureOutput.setText((Float.toString(m_bioPressure)) + " " + getResources().getString(R.string.mb));
            }
            else
            {
                m_textViewPressureOutput.setText(R.string.NoPressure);
            }
        }

        private void Submit(){
            String temperature_arr[] = m_textViewTemperatureOutput.getText().toString().split(" ");
            String temperature = temperature_arr[0]; // number

            String pressure_arr[] = m_textViewPressureOutput.getText().toString().split(" ");
            String pressure = pressure_arr[0]; // number

            String weatherCondition = m_spinnerGetWeather.getSelectedItem().toString();
            String latitude = m_textViewLatitudeOutput.getText().toString();
            String longitude = m_textViewLongitudeOutput.getText().toString();

            m_textViewTemperatureOutput.setText(R.string._0);
            m_textViewPressureOutput.setText(R.string._0);
            m_spinnerGetWeather.setSelection(0);
            m_textViewLatitudeOutput.setText(R.string._0);
            m_textViewLongitudeOutput.setText(R.string._0);

            Log.i("Output: ", temperature + " " + pressure + " " + weatherCondition + " " + latitude + " " + longitude);

        }


        public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
        {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);

            switch (requestCode)
            {
                case REQUEST_LOCATION:
                    GetLocation();
                    break;
            }
        }

        @Override
        public void onSensorChanged(SensorEvent sensorEvent) {
            if (sensorEvent.sensor.getType() == Sensor.TYPE_AMBIENT_TEMPERATURE) {
                m_ambientTemperature = sensorEvent.values[0];
            }
            if (sensorEvent.sensor.getType() == Sensor.TYPE_PRESSURE) {
                m_bioPressure = sensorEvent.values[0];
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int i) {
            // do something if accuracy changes
        }
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            return PlaceholderFragment.newInstance(position + 1);
        }

        @Override
        public int getCount() {
            // Show 2 total pages.
            return 2;
        }
    }
}
