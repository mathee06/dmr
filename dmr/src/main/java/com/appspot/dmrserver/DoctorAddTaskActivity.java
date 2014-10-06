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
import android.content.res.Resources;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.concurrent.ExecutionException;

/**
 * Created by Taranveer on 28/11/13.
 */
public class DoctorAddTaskActivity extends Activity {
    private DrawerLayout dLayout;
    private ListView dList;
    private ActionBarDrawerToggle dToggle;
    private CharSequence dTitle;
    private CharSequence winTitle;
    private String [] doctorMenu;
    private EditText usernameEdit, descriptionEdit;
    private String usernameStr, descriptionStr, userid;
    private Spinner provinceSpinner;
    private Fragment fragment;
    private Button saveButton, cancelButton;
    private SharedPreferences loginPreferences;
    public static final String PREFS_NAME = "DMRPrefsFile";

    public void onCreate(Bundle savedInstanceState) {
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

        //firstNameEdit = (EditText)

        winTitle = dTitle = getTitle();
        doctorMenu = getResources().getStringArray(R.array.doctorMenu);
        dLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        dList = (ListView) findViewById(R.id.left_drawer);
        loginPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        userid = loginPreferences.getString("userid", null);

        // Set the adapter for the list view
        dList.setAdapter(new ArrayAdapter<String>(this,
                R.layout.drawer_list_item, doctorMenu));
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
        };

        // Set the drawer toggle as the DrawerListener
        dLayout.setDrawerListener(dToggle);
        if (savedInstanceState == null) {
            selectItem(1);
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
        System.out.println("VIEW INFO");
        if (position == 0) {
            Intent doctorApptIntent = new Intent(this, DoctorApptActivity.class);
            startActivity(doctorApptIntent);
        }

//        if (position == 1) {
//            Intent doctorAddTaskActivityIntent = new Intent(this, DoctorAddTaskActivity.class);
//            startActivity(doctorAddTaskActivityIntent);
//        }

        else if (position == 2) {
            Intent doctorRequestAccessIntent = new Intent(this, DoctorRequestAccessActivity.class);
            startActivity(doctorRequestAccessIntent);
        }

        else if (position == 3) {
            Intent logoutIntent = new Intent(this, Logout.class);
            startActivity(logoutIntent);
        }

        else {
            // update the main content by replacing fragments
            fragment = new PlanetFragment(position  );
            //Bundle args = new Bundle();
            //args.putInt(PlanetFragment.ARG_PLANET_NUMBER, position);
            //fragment.setArguments(args);
            FragmentManager fragmentManager = getFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();

            // update selected item and title, then close the drawer
            dList.setItemChecked(position, true);
            setTitle(doctorMenu[position]);
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
    public class PlanetFragment extends Fragment implements View.OnClickListener {
        //public static final String ARG_PLANET_NUMBER = "planet_number";
        int ARG_PLANET_NUMBER;

        public PlanetFragment(int position) {
            ARG_PLANET_NUMBER = position;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = getActivity().getLayoutInflater().inflate(R.layout.doctor_add_task, container, false);
            int i = ARG_PLANET_NUMBER;
            String planet = getResources().getStringArray(R.array.doctorMenu)[i];

            getActivity().setTitle(planet);

            // MY STUFF
            usernameEdit = (EditText) rootView.findViewById (R.id.usernameEdit);
            descriptionEdit = (EditText) rootView.findViewById (R.id.descriptionEdit);

            saveButton = (Button) rootView.findViewById(R.id.saveButton);
            cancelButton = (Button) rootView.findViewById(R.id.cancelButton);

            saveButton.setOnClickListener(this);
            cancelButton.setOnClickListener(this);

            usernameEdit.setEnabled(true);
            descriptionEdit.setEnabled(true);

            saveButton.setVisibility(View.VISIBLE);
            cancelButton.setVisibility(View.VISIBLE);

            return rootView;
        }

        @Override
        public void onClick(View v) {
            if (v.getId() == cancelButton.getId()) {
                // RESTORE ORIGINAL VALUES IN FIELD

                Intent doctorApptIntent = new Intent(DoctorAddTaskActivity.this, DoctorApptActivity.class);
                startActivity(doctorApptIntent);

            }

            else if (v.getId() == saveButton.getId()) {
                Boolean valid = true;
                if (usernameEdit.getText().toString().length() == 0 || descriptionEdit.getText().toString().length() == 0) {
                    valid = false;
                }

                if (valid) {
                    // MAKE REQUEST TO SERVER TO SAVE THE NEW INFORMATION!
                    usernameStr = usernameEdit.getText().toString();
                    descriptionStr = descriptionEdit.getText().toString();

                    Intent doctorApptIntent = new Intent(DoctorAddTaskActivity.this, DoctorApptActivity.class);
                    startActivity(doctorApptIntent);

                    ServerComm.AddNurseTasks logMeIn = new ServerComm.AddNurseTasks();
                    logMeIn.execute(userid, usernameStr, descriptionStr);
                    JSONObject resp = null;
                    try {
                        resp = logMeIn.get();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    }

                    try {
                        Toast.makeText(getApplicationContext(), resp.getString("status"), Toast.LENGTH_SHORT).show();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                else {
                    Toast.makeText(getApplicationContext(), "You Are Missing a Field! Try Again!", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    public int provinceNumber (String province) {
        Resources res = getResources();
        String [] provinces = res.getStringArray(R.array.provinces_array);
        return Arrays.asList(provinces).indexOf(province);
    }
}
