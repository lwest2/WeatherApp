package com.example.sal.weatherapp;

import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
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
import android.widget.TextView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

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
        fab.setOnClickListener(this);

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

    @Override
    public void onClick(View view) {
        switch (view.getId())
        {
            case R.id.fab:
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                break;
        }
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment implements View.OnClickListener{
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

        View rootView;
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {

            mWeatherDatabase = FirebaseDatabase.getInstance().getReference();

            switch (getArguments().getInt(ARG_SECTION_NUMBER))
            {
                case 1:
                    rootView = inflater.inflate(R.layout.fragment_main, container, false);
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
                    weatherPins.add(pinKey);

                    updateLists(dataSnapshot);
                }

                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                    Log.d("Child Changed", "Shouldn't happen");
                }

                @Override
                public void onChildRemoved(DataSnapshot dataSnapshot) {
                    String pinKey = dataSnapshot.getKey();
                    weatherPins.remove(pinKey);

                    updateLists(dataSnapshot);
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

        @Override
        public void onClick(View view) {
            switch(view.getId())
            {
                case R.id.button_testsend:
                    SendFirebase();
                    break;
            }
        }

        public void updateLists(DataSnapshot dataSnapshot)
        {
            Double pressure = 9999.9999;
            Double latitude = 9999.9999;
            Double longitude =  9999.9999;
            Double temperature =  9999.9999;
            java.util.Date time = Calendar.getInstance().getTime();
            String weatherCondition = "n/a";

            try {pressure = Double.valueOf(dataSnapshot.child("Barometer").getValue().toString());}
            catch (Exception ex) {Log.d("Error", "firebase barometer error");}

            try {latitude = Double.valueOf(dataSnapshot.child("Lat").getValue().toString());}
            catch (Exception ex) {Log.d("Error", "firebase latitude error");}

            try {longitude = Double.valueOf(dataSnapshot.child("Long").getValue().toString());}
            catch (Exception ex) {Log.d("Error", "firebase longitude error");}

            try {temperature = Double.valueOf(dataSnapshot.child("Temperature").getValue().toString());}
            catch (Exception ex) {Log.d("Error", "firebase temperature error");}

            try {time = Date.valueOf(dataSnapshot.child("Time").getValue().toString());}
            catch (Exception ex) {Log.d("Error", "firebase time error");}

            try {weatherCondition = dataSnapshot.child("Weather").getValue().toString();}
            catch (Exception ex) {Log.d("Error", "firebase weather error");}

            pressures.add(pressure);
            latitudes.add(latitude);
            longitudes.add(longitude);
            temperatures.add(temperature);
            times.add(time);
            weatherConditions.add(weatherCondition);

            Log.d("Weather Pins", "Below is the weather pin List");
            for(int i = 0; i < weatherPins.size(); i++)
            {
                Log.d("Weather Pin", weatherPins.get(i));
                Log.d("Pressure", ""+ pressures.get(i));
                Log.d("Lat", ""+ latitudes.get(i));
                Log.d("Long", ""+ longitudes.get(i));
                Log.d("Temperature", ""+ temperatures.get(i));
                Log.d("Time", ""+ times.get(i).toString());
                Log.d("Weather Condition", weatherConditions.get(i));
            }
        }

        public void SendFirebase(){
            //int currentPin = mWeatherDatabase.child("NumPins");
            String testPinName = "TestPin";

            mWeatherDatabase.child(testPinName).child("Barometer").setValue("test");
            mWeatherDatabase.child(testPinName).child("Lat").setValue("test");
            mWeatherDatabase.child(testPinName).child("Long").setValue("test");
            mWeatherDatabase.child(testPinName).child("Temperature").setValue("test");
            mWeatherDatabase.child(testPinName).child("Time").setValue(Calendar.getInstance().getTime().toString());
            mWeatherDatabase.child(testPinName).child("Weather").setValue("test");
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
