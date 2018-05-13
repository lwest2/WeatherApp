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
import android.os.Debug;
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

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DecimalFormat;
import java.sql.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

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

    public static boolean hasPermissions(Context context, String... permissions) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
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

        private DatabaseReference mWeatherDatabase;

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

        //Below will be the collection of weather pins
        List<String> weatherPins = new ArrayList<>();
        List<Double> pressures = new ArrayList<>();
        List<Double> latitudes = new ArrayList<>();
        List<Double> longitudes = new ArrayList<>();
        List<Double> temperatures = new ArrayList<>();
        List<java.util.Date> times = new ArrayList<>();
        List<String> weatherConditions = new ArrayList<>();

        int start = -1;
        private String startname = "Start";
        int end = -1;
        private String endname = "End";
        private String childNameSE = "Adding";

        View rootView;
        //private Button m_buttonGetTemperature;
        //private Button m_buttonGetPressure;
        //private Button m_buttonGetLocation;
        private Button m_buttonSubmit;
        private Button m_buttonGetData;

        private Spinner m_spinnerGetWeather;

        private TextView m_textViewTemperatureOutput;
        private TextView m_textViewPressureOutput;
        private TextView m_textViewLatitudeOutput;
        private TextView m_textViewLongitudeOutput;

        private LocationManager m_locationManager;
        private double latitudeGetLoc;
        private double longitudeGetLoc;

        private static final int REQUEST_LOCATION = 1;

        private SensorManager m_sensorManager;
        private Sensor m_temperature;
        private float m_ambientTemperature;
        private Sensor m_pressure;
        private float m_bioPressure;

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {

            mWeatherDatabase = FirebaseDatabase.getInstance().getReference();

            switch (getArguments().getInt(ARG_SECTION_NUMBER)) {
                case 1:
                    rootView = inflater.inflate(R.layout.fragment_main, container, false);

                    /*
                    m_buttonGetTemperature = (Button) rootView.findViewById(R.id.buttonGetTemparture);
                    m_buttonGetPressure = (Button) rootView.findViewById(R.id.buttonGetPressure);
                    m_buttonGetLocation = (Button) rootView.findViewById(R.id.buttonGetLocation);
                    */
                    m_buttonSubmit = (Button) rootView.findViewById(R.id.buttonSubmit);
                    m_buttonGetData = (Button) rootView.findViewById(R.id.getData);

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
                    LocationListener locationListener = new LocationListener() {
                        @Override
                        public void onLocationChanged(Location location) {
                            longitudeGetLoc = location.getLongitude();
                            latitudeGetLoc = location.getLatitude();
                        }

                        @Override
                        public void onStatusChanged(String s, int i, Bundle bundle) { }

                        @Override
                        public void onProviderEnabled(String s) { }

                        @Override
                        public void onProviderDisabled(String s) { }
                    };

                    if(ActivityCompat.checkSelfPermission(this.getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                            && ActivityCompat.checkSelfPermission(this.getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(this.getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);
                    } else {
                        m_locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 5000, 10, locationListener);
                    }

                    m_buttonGetData.setOnClickListener(this);
                    m_buttonSubmit.setOnClickListener(this);
                    /*
                    m_buttonGetLocation.setOnClickListener(this);
                    m_buttonGetPressure.setOnClickListener(this);
                    m_buttonGetTemperature.setOnClickListener(this);
                    */
                    break;

                case 2:
                    rootView = inflater.inflate(R.layout.fragment_mapview, container, false);
                    break;

                case 3:
                    rootView = inflater.inflate(R.layout.fragment_firebasetest, container, false);

                    Button button_firebaseSendTest = (Button) rootView.findViewById(R.id.button_testsend);

                    button_firebaseSendTest.setOnClickListener(this);

                    break;
            }

            mWeatherDatabase.addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    String pinKey = dataSnapshot.getKey();

                    if (childNameSE.equals(pinKey))
                    {
                        start = Integer.parseInt(dataSnapshot.child(startname).getValue().toString());
                        end = Integer.parseInt(dataSnapshot.child(endname).getValue().toString());
                    }

                    if ((!(weatherPins.contains(pinKey)))&&(!childNameSE.equals(pinKey))) {
                        EditStartAndEnd(dataSnapshot);
                        updateLists(dataSnapshot);
                    }
                }

                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                }

                @Override
                public void onChildRemoved(DataSnapshot dataSnapshot) {
                    String pinKey = dataSnapshot.getKey();

                    if ((weatherPins.contains(pinKey))&&(!childNameSE.equals(pinKey))) {
                        EditStartAndEnd(dataSnapshot);

                        Log.d("Look at me", pinKey);

                        int place = weatherPins.indexOf(pinKey);
                        weatherPins.remove(place);
                        pressures.remove(place);
                        latitudes.remove(place);
                        longitudes.remove(place);
                        temperatures.remove(place);
                        times.remove(place);
                        weatherConditions.remove(place);
                    }

                    if (((!weatherPins.contains(pinKey)))&&(!childNameSE.equals(pinKey))) {
                        updateLists(dataSnapshot);
                    }

                }

                @Override
                public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                    Log.d("Child Moved", "Shouldn't happen");
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.d("Error", "Database Error");
                }
            });

            return rootView;
        }

        public void EditStartAndEnd(DataSnapshot dataSnapshot) {
            String[] splitString = dataSnapshot.getKey().split("n");
            int newPointer = Integer.parseInt(splitString[1]);

            if ((newPointer + 1) < start) {
                    start = newPointer + 1;
            }
            else if ((newPointer < end) && (newPointer > start)) {
                    end = newPointer;
            }

            mWeatherDatabase.child(childNameSE).child(endname).setValue(end);
            mWeatherDatabase.child(childNameSE).child(startname).setValue(start);
        }

        @Override
        public void onClick(View view) {
            switch(view.getId())
            {
                case R.id.button_testsend:
                    Log.d("Test Send Button", "Un-needed");
                    break;
                    /*
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
                    */

                case R.id.buttonSubmit:
                    // code to submit
                    // POPUP : ARE YOU SURE? Y / N?

                    DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            switch(i)
                            {
                                case DialogInterface.BUTTON_NEGATIVE:

                                    break;

                                case DialogInterface.BUTTON_POSITIVE:
                                    Submit();
                                    break;
                            }
                        }
                    };

                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setMessage("Are you sure you want to submit your weather report?")
                            .setPositiveButton("Yes", dialogClickListener)
                            .setNegativeButton("No", dialogClickListener);
                    builder.create();
                    builder.show();
                    break;

                case R.id.getData:
                    GetLocation();
                    GetTemperature();
                    GetPressure();
                    break;
            }
        }

        public void updateLists(DataSnapshot dataSnapshot) {
            DateFormat dateFormat = new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy");

            Double pressure = 9999.9999;
            Double latitude = 9999.9999;
            Double longitude = 9999.9999;
            Double temperature = 9999.9999;

            java.util.Date time = null;
            String weatherCondition = "n/a";

            Boolean outOfDate = true;

            try {
                pressure = Double.valueOf(dataSnapshot.child("Barometer").getValue().toString());
            } catch (Exception ex) {
                Log.d("Error", "firebase barometer error");
            }

            try {
                latitude = Double.valueOf(dataSnapshot.child("Lat").getValue().toString());
            } catch (Exception ex) {
                Log.d("Error", "firebase latitude error");
            }

            try {
                longitude = Double.valueOf(dataSnapshot.child("Long").getValue().toString());
            } catch (Exception ex) {
                Log.d("Error", "firebase longitude error");
            }

            try {
                temperature = Double.valueOf(dataSnapshot.child("Temperature").getValue().toString());
            } catch (Exception ex) {
                Log.d("Error", "firebase temperature error");
            }

            try {
                String dateString = dataSnapshot.child("Time").getValue().toString();
                time = dateFormat.parse(dateString);

                //Check time to see if out of date
                outOfDate = OutOfDate(time);
            } catch (Exception ex) {
                Log.d("Error", "firebase time error");
            }

            try {
                weatherCondition = dataSnapshot.child("Weather").getValue().toString();
            } catch (Exception ex) {
                Log.d("Error", "firebase weather error");
            }

            if (outOfDate == false) {

                weatherPins.add(dataSnapshot.getKey());
                pressures.add(pressure);
                latitudes.add(latitude);
                longitudes.add(longitude);
                temperatures.add(temperature);
                times.add(time);
                weatherConditions.add(weatherCondition);

                Log.d("Weather Pins", "Below is the weather pin List for: " + dataSnapshot.getKey());
                Log.d("Weather Pin", weatherPins.get(weatherPins.size() - 1));
                Log.d("Pressure", "" + pressures.get(weatherPins.size() - 1));
                Log.d("Lat", "" + latitudes.get(weatherPins.size() - 1));
                Log.d("Long", "" + longitudes.get(weatherPins.size() - 1));
                Log.d("Temperature", "" + temperatures.get(weatherPins.size() - 1));
                Log.d("Time", "" + times.get(weatherPins.size() - 1).toString());
                Log.d("Weather Condition", weatherConditions.get(weatherPins.size() - 1));

            } else if (outOfDate == true) {
                if (!childNameSE.equals(dataSnapshot.getKey())) {
                    mWeatherDatabase.child(dataSnapshot.getKey()).removeValue();
                }
            }
        }

        //1 hour in milliseconds = 3,600,000
        //8 hour = 8* 3,600,000 = 28,800,000
        public Boolean OutOfDate(java.util.Date toCheck)
        {
            boolean outOfDate = true;
            long eightHours = 28800000;

            if (toCheck != null) {
                java.util.Date currentTime = Calendar.getInstance().getTime();
                Long difference = currentTime.getTime() - toCheck.getTime();

                if (difference < eightHours) {
                    outOfDate = false;
                }
            }
		return outOfDate;
	}

        public void SendFirebase(String pressureString, String latString, String longString, String tempString, String conditionString){
            
	        String pinName = "Pin";

            if (start == end)
            {
                pinName = pinName + weatherPins.size();
            }
            else
            {
                pinName = pinName + start;
                start = start + 1;
                mWeatherDatabase.child(childNameSE).child(startname).setValue(start);
            }

            mWeatherDatabase.child(pinName).child("Time").setValue(Calendar.getInstance().getTime().toString());
            mWeatherDatabase.child(pinName).child("Barometer").setValue(pressureString);
            mWeatherDatabase.child(pinName).child("Lat").setValue(latString);
            mWeatherDatabase.child(pinName).child("Long").setValue(longString);
            mWeatherDatabase.child(pinName).child("Temperature").setValue(tempString);
            mWeatherDatabase.child(pinName).child("Weather").setValue(conditionString);
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
                    longitudeGetLoc = location.getLongitude();
                    latitudeGetLoc = location.getLatitude();

                    m_textViewLongitudeOutput.setText(Double.toString(longitudeGetLoc));
                    m_textViewLatitudeOutput.setText(Double.toString(latitudeGetLoc));
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

            m_textViewTemperatureOutput.setText(getResources().getString(R.string.n_a));
            m_textViewPressureOutput.setText(getResources().getString(R.string.n_a));
            m_spinnerGetWeather.setSelection(0);
            m_textViewLatitudeOutput.setText(getResources().getString(R.string.n_a));
            m_textViewLongitudeOutput.setText(getResources().getString(R.string.n_a));

            Log.i("Output: ", temperature + " " + pressure + " " + weatherCondition + " " + latitude + " " + longitude);
            if (!weatherCondition.equalsIgnoreCase("") && !longitude.equalsIgnoreCase("Unable to find location") && !latitude.equalsIgnoreCase("Unable to find location")) {
                SendFirebase(pressure, latitude, longitude, temperature, weatherCondition);
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("Uploaded.")
                        .setMessage("Your weather report has been submitted.")
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        });
                builder.create();
                builder.show();
            } else
            {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("Missing Data.")
                        .setMessage("Requires at least weather condition and location to submit.")
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        });
                builder.create();
                builder.show();
            }
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
            return 3;
        }
    }
}
