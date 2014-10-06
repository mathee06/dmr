package com.appspot.dmrserver;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
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
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by setup on 11/27/13.
 */
public class SecretaryDoctorAppt extends Activity {

    private DrawerLayout dLayout;
    private ListView dList;
    private ActionBarDrawerToggle dToggle;
    private CharSequence dTitle;
    private CharSequence winTitle;
    private String [] secretaryDocApptsMenu;
    private Fragment fragment;
    private ListView apptTimes;
    Context mContext;
    public static final String PREFS_NAME = "DMRPrefsFile";
    private SharedPreferences loginPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Creating the Screen
        super.onCreate(savedInstanceState);
        setContentView(R.layout.secretary_view_homepage);
        mContext = this;
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

        winTitle = dTitle = "Doctor List";
        secretaryDocApptsMenu = getResources().getStringArray(R.array.secretaryDocApptsMenu);
        dLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        dList = (ListView) findViewById(R.id.left_drawer);

        // Set the adapter for the list view
        dList.setAdapter(new ArrayAdapter<String>(this,
                R.layout.drawer_list_item, secretaryDocApptsMenu));
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
        if (position == 1) {
            Intent secretaryIntent = new Intent(this, SecretaryActivity.class);
            startActivity(secretaryIntent);
        }
        else if (position == 2) {
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
            setTitle(secretaryDocApptsMenu[position]);
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
            String planet = getResources().getStringArray(R.array.secretaryDocApptsMenu)[i];
            System.out.println(planet);
            System.out.println(i);

            getActivity().setTitle(planet);

            // MY STUFF
            apptTimes = (ListView) rootView.findViewById(R.id.listView);



            String docUsername = null;
            String docLastname = null;
            Bundle extras = getIntent().getExtras();
            if (extras != null) {
                docUsername = extras.getString("docUsername");
                docUsername = docUsername.replace(" ", "");
                docLastname = extras.getString("docLastName");
                docLastname = docLastname.replace(" ", "");

            }

            final String doctorLastName = docLastname;


            List<Map<String, String>> data = new ArrayList<Map<String, String>>();

            ServerComm.DocApptList docApptList = new ServerComm.DocApptList();
            docApptList.execute(docUsername);
            JSONObject resp = null;
            try
            {
                resp = docApptList.get();
            }
            catch (Exception e)
            {
                //Handle Exception
            }

            JSONArray patientApptNames = null;
            JSONArray patientApptTimes = null;
            JSONArray patientApptKeys = null;
            JSONArray patientApptCheckIn = null;
            try
            {
                patientApptTimes = resp.getJSONArray("time");
                patientApptNames = resp.getJSONArray("patientName");
                patientApptKeys = resp.getJSONArray("patientKey"); // or patientKey
                patientApptCheckIn = resp.getJSONArray("checkin");
                for (int ij=0; ij<patientApptNames.length(); ij++) {
                    Map <String, String> hm = new HashMap<String, String>(2);
                    try {
                        String checkin = "Not Checked in";
                        if (patientApptCheckIn.getBoolean(ij))
                            checkin = "Checked in";

                        hm.put ("text1", patientApptNames.getString(ij));
                        hm.put("text2", patientApptTimes.getString(ij) + " - " + checkin);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    data.add(hm);
                }

            }
            catch (JSONException e)
            {
                e.printStackTrace();
            }

            SimpleAdapter adapter = new SimpleAdapter(getActivity(), data,
                    android.R.layout.simple_list_item_2,
                    new String[] {"text1", "text2"},
                    new int[] {android.R.id.text1,
                            android.R.id.text2});
            apptTimes.setAdapter(adapter);

            final JSONArray patientNames = patientApptNames;
            final JSONArray patientKeys = patientApptKeys;
            final JSONArray patientTimes = patientApptTimes;
            final JSONArray patientCheckIn = patientApptCheckIn;


            apptTimes.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> parent, View view,
                                        int position, long id) {

                    // ListView Clicked item index
                    int itemPosition     = position;

                    // ListView Clicked item value
                    String patientName = "";
                    String patientKey = "";
                    String apptTime = "";;
                    try
                    {
                        patientName = patientNames.getString(position);
                        patientKey = patientKeys.getString(position);
                        apptTime = patientTimes.getString(position);

                    }
                    catch (JSONException e)
                    {

                    }

                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(SecretaryDoctorAppt.this);
                    alertDialog.setTitle("Appointment");
                    alertDialog.setMessage("Patient: " + patientName + "\n"
                                          + "with Dr. " + doctorLastName + " @ " + apptTime);

                    alertDialog.setPositiveButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                        }
                    });

                    alertDialog.setNegativeButton("Edit", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                        }
                    });

                    alertDialog.setNeutralButton("Remove", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                        }
                    });


                    alertDialog.show();
                }
            });

            return rootView;
        }
    }
}

