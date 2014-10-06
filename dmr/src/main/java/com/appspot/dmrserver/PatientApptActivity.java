package com.appspot.dmrserver;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

/**
 * Created by Taranveer on 28/11/13.
 */
public class PatientApptActivity extends Activity{
    private DrawerLayout dLayout;
    private ListView dList;
    private ActionBarDrawerToggle dToggle;
    private CharSequence dTitle;
    private CharSequence winTitle;
    private String [] patientMenu;
    private Fragment fragment;
    private ListView apptTimes;
    public static final String PREFS_NAME = "DMRPrefsFile";
    private SharedPreferences loginPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Creating the Screen
        super.onCreate(savedInstanceState);
        setContentView(R.layout.patient_info_read);

        /**snip **/
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("com.package.ACTION_LOGOUT");
        registerReceiver(new BroadcastReceiver() {

            @Override
            public void onReceive(Context context, Intent intent) {
                Log.d("onReceive", "Logout in progress");
                //At this point you should start the login activity and finish this one
                finish();
            }
        }, intentFilter);
        //** snip **//

        winTitle = dTitle = getTitle();
        patientMenu = getResources().getStringArray(R.array.patientMenu);
        dLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        dList = (ListView) findViewById(R.id.left_drawer);

        // Set the adapter for the list view
        dList.setAdapter(new ArrayAdapter<String>(this,
                R.layout.drawer_list_item, patientMenu));
        // Set the list's click listener
        dList.setOnItemClickListener(new DrawerItemClickListener());

        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeButtonEnabled(true);

        dToggle = new ActionBarDrawerToggle(this, dLayout,
                R.drawable.ic_drawer, R.string.drawer_open, R.string.drawer_close) {

            /** Called when a drawer has settled in a completely closed state. */
            public void onDrawerClosed(View view) {
                getActionBar().setTitle(winTitle);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            /** Called when a drawer has settled in a completely open state. */
            public void onDrawerOpened(View drawerView) {
                getActionBar().setTitle(dTitle);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            // ACTUAL STUFF
            //firstNameEdit = (EditText) findViewById(R.id.firstNameEdit);
        };

        // Set the drawer toggle as the DrawerListener
        dLayout.setDrawerListener(dToggle);
        if (savedInstanceState == null) {
            selectItem(0);
        }
    }   // END OF onCreate

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    /* Called whenever we call invalidateOptionsMenu() */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // If the nav drawer is open, hide action items related to the content view
        //boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // The action bar home/up action should open or close the drawer.
        // ActionBarDrawerToggle will take care of this.
        if (dToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /* The click listner for ListView in the navigation drawer */
    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            selectItem(position);
        }
    }

    private void selectItem(int position) {
        System.out.println(position);
        System.out.println("Patient APPTS");
        /*if (position == 0) {
            Intent patientApptIntent = new Intent(this, PatientApptActivity.class);
            startActivity(patientApptIntent);
        }*/

        if (position == 1) {
            Intent patientIntent = new Intent(this, PatientViewInfo.class);
            startActivity(patientIntent);
        }

        else if (position == 3) {
            Intent patientIntent = new Intent(this, PatGraphActivity.class);
            startActivity(patientIntent);
        }

        else if (position == 4) {
            Intent logoutIntent = new Intent(this, Logout.class);
            startActivity(logoutIntent);
        }

        else {
            // update the main content by replacing fragments
            fragment = new PlanetFragment(position);
            //Bundle args = new Bundle();
            //args.putInt(PlanetFragment.ARG_PLANET_NUMBER, position);
            //fragment.setArguments(args);
            FragmentManager fragmentManager = getFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();

            // update selected item and title, then close the drawer
            dList.setItemChecked(position, true);
            setTitle(patientMenu[position]);
            dLayout.closeDrawer(dList);
        }
    }   //END OF selectItem

    @Override
    public void setTitle(CharSequence title) {
        winTitle = title;
        getActionBar().setTitle(winTitle);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        dToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        dToggle.onConfigurationChanged(newConfig);
    }

    /**
     * Fragment that appears in the "content_frame", shows a planet
     */
    public class PlanetFragment extends Fragment {
        //public static final String ARG_PLANET_NUMBER = "planet_number";
        int ARG_PLANET_NUMBER;

        public PlanetFragment(int position) {
            ARG_PLANET_NUMBER = position;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = getActivity().getLayoutInflater().inflate(R.layout.patient_appointment_fragment, container, false);
            int i = ARG_PLANET_NUMBER;
            String planet = getResources().getStringArray(R.array.patientMenu)[i];
            System.out.println(planet);
            System.out.println(i);

            getActivity().setTitle(planet);

            // MY STUFF
            apptTimes = (ListView) rootView.findViewById(R.id.listView);

            loginPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
            String userid = (loginPreferences.getString("userid", null));
            System.out.println ("USERID" + userid);

            // MAKE SERVER CALL FOR DATA FOR APPOINTMENTS HERE!!
            // *************************************************

            List<Map<String, String>> data = new ArrayList<Map<String, String>>();

            System.out.println ("APPT LIST");
            ServerComm.PatientApptList logMeIn = new ServerComm.PatientApptList();
            System.out.println ("USER ID");
            logMeIn.execute(userid);
            JSONObject resp = null;
            try {
                System.out.println ("STARTING");
                resp = logMeIn.get();
                System.out.println ("SUCCESS");
                System.out.println (resp);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
            JSONArray appt1 = null;
            JSONArray appt2 = null;
            try {
                appt1 = resp.getJSONArray("text1");
                appt2 = resp.getJSONArray("text2");
            } catch (JSONException e) {
                e.printStackTrace();
            }


            for (int ij=0; ij<appt1.length(); ij++) {
                Map <String, String> hm = new HashMap<String, String>(2);
                try {
                    hm.put ("text1", appt1.getString(ij));
                    hm.put ("text2", appt2.getString(ij));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                data.add(hm);
            }

            SimpleAdapter adapter = new SimpleAdapter(getActivity(), data,
                    android.R.layout.simple_list_item_2,
                    new String[] {"text1", "text2"},
                    new int[] {android.R.id.text1,
                            android.R.id.text2});
            apptTimes.setAdapter(adapter);

            return rootView;
        }
    }
}

        /*
        apptTimes = (ListView) findViewById(R.id.listView);

        // Defined Array values to show in ListView
        String[] values = new String[] { "Android List View",
                "Adapter implementation",
                "Simple List View In Android",
                "Create List View Android",
                "Android Example",
                "List View Source Code",
                "List View Array Adapter",
                "Android Example List View"
        };

        // Define a new Adapter
        // First parameter - Context
        // Second parameter - Layout for the row
        // Third parameter - ID of the TextView to which the data is written
        // Forth - the Array of data

        List<Map<String, String>> data = new ArrayList<Map<String, String>>();

        Map <String, String> hm = new HashMap<String, String>(2);
        hm.put ("text1", "25/06/14");
        hm.put ("text2", "4:30 PM");
        data.add(hm);
        Map <String, String> hm1 = new HashMap<String, String>(2);
        hm1.put ("text1", "23/10/10");
        hm1.put ("text2", "2:15 PM");
        data.add(hm1);
        Map <String, String> hm2 = new HashMap<String, String>(2);
        hm2.put ("text1", "Fuck");
        hm2.put ("text2", "3A04");
        data.add(hm2);

        SimpleAdapter adapter = new SimpleAdapter(this, data,
                android.R.layout.simple_list_item_2,
                new String[] {"text1", "text2"},
                new int[] {android.R.id.text1,
                        android.R.id.text2});
        apptTimes.setAdapter(adapter);

        /*ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, android.R.id.text1, values);

        // Assign adapter to ListView
        apptTimes.setAdapter(adapter);*/

        // ListView Item Click Listener
        /*apptTimes.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

                // ListView Clicked item index
                int itemPosition     = position;

                // ListView Clicked item value
                //String  itemValue    = (String) apptTimes.getItemAtPosition(position);

                // Show Alert
                Toast.makeText(getApplicationContext(),
                        "Position :"+itemPosition+"  ListItem : " + "TEST" , Toast.LENGTH_LONG)
                        .show();

            }
        });
    }
}
*/