package com.appspot.dmrserver;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
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
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.concurrent.ExecutionException;

/**
 * Created by Taranveer on 02/12/13.
 */
public class AdminAddNewUserPatInfo extends Activity{
    private DrawerLayout dLayout;
    private ListView dList;
    private ActionBarDrawerToggle dToggle;
    private CharSequence dTitle;
    private CharSequence winTitle;
    private String [] adminMenu;
    private EditText addressEdit, cityEdit, postalCodeEdit, phoneNumberEdit, doctorEdit, healthCardEdit;
    private Spinner provinceSpinner;
    private String addressStr, cityStr, postalCodeStr, phoneNumberStr, provinceStr, doctorStr, firstNameStr, lastNameStr, userNameStr, passwordStr, userTypeStr, year, month, day, healthCardStr;
    private Fragment fragment;
    private Button submitButton, cancelButton;
    private DatePicker birthday;
    Bundle extras;

    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.patient_info_read);
        extras = getIntent().getExtras();

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
        adminMenu = getResources().getStringArray(R.array.adminMenu);
        dLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        dList = (ListView) findViewById(R.id.left_drawer);

        // Set the adapter for the list view
        dList.setAdapter(new ArrayAdapter<String>(this,
                R.layout.drawer_list_item, adminMenu));
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
            Intent adminUserListIntent = new Intent(this, AdminUserListActivity.class);
            startActivity(adminUserListIntent);
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
            setTitle(adminMenu[position]);
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
            View rootView = getActivity().getLayoutInflater().inflate(R.layout.admin_add_user_2, container, false);
            int i = ARG_PLANET_NUMBER;
            String planet = getResources().getStringArray(R.array.patientMenu)[i];

            getActivity().setTitle(planet);
            System.out.println("gficbkejgvbkjdsfbkjabfkrbmdgbasbf BUGSBUGSBUGS");

            // MY STUFF
            firstNameStr = extras.getString("firstName");
            lastNameStr = extras.getString("lastName");
            userNameStr = extras.getString("userName");
            passwordStr = extras.getString("password");
            userTypeStr = extras.getString("userType");

            addressEdit = (EditText) rootView.findViewById(R.id.addressEdit);
            cityEdit = (EditText) rootView.findViewById(R.id.cityEdit);
            postalCodeEdit = (EditText) rootView.findViewById(R.id.postalCodeEdit);
            phoneNumberEdit = (EditText) rootView.findViewById(R.id.phoneNumberEdit);
            doctorEdit = (EditText) rootView.findViewById(R.id.doctorEdit);
            birthday = (DatePicker) rootView.findViewById(R.id.datePicker);
            healthCardEdit = (EditText) rootView.findViewById(R.id.healthCardEdit);

            provinceSpinner = (Spinner) rootView.findViewById(R.id.provinceAddSpinner);

            submitButton = (Button) rootView.findViewById(R.id.submitButton);
            cancelButton = (Button) rootView.findViewById(R.id.cancelButton);

            submitButton.setOnClickListener(this);
            cancelButton.setOnClickListener(this);

            ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity().getBaseContext(),
                    R.array.provinces_array, android.R.layout.simple_spinner_item);

            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            // Apply the adapter to the spinner
            provinceSpinner.setAdapter(adapter);
            return rootView;
        }

        @Override
        public void onClick(View v) {
            if (v.getId() == cancelButton.getId()) {
                // RESTORE ORIGINAL VALUES IN FIELD
                // ********************************

                Intent adminUserListIntent = new Intent(this.getActivity(), AdminUserListActivity.class);
                startActivity(adminUserListIntent);
            }

            else if (v.getId() == submitButton.getId()) {
                addressStr = addressEdit.getText().toString();
                cityStr = cityEdit.getText().toString();
                postalCodeStr = postalCodeEdit.getText().toString();
                provinceStr = provinceSpinner.getSelectedItem().toString();
                phoneNumberStr = phoneNumberEdit.getText().toString();
                doctorStr = doctorEdit.getText().toString();
                healthCardStr = healthCardEdit.getText().toString();
                year = "" + birthday.getYear();
                month = "" + birthday.getMonth();
                day = "" + birthday.getDayOfMonth();
                System.out.println(year);
                System.out.println(month);
                System.out.println(day);


                Boolean valid = true;
                if (addressStr.length() == 0 || cityStr.length() == 0 || postalCodeStr.length() == 0 || provinceStr.length()==0 || phoneNumberStr.length()==0 || doctorStr.length()==0 || healthCardStr.length()==0) {
                    valid = false;
                }

                if (valid) {
                    // MAKE SERVER CALL TO ADD NEW USER
                    ServerComm.AddNewUserToServer addUser = new ServerComm.AddNewUserToServer();
                    addUser.execute(firstNameStr, lastNameStr, userNameStr, passwordStr, userTypeStr, doctorStr, healthCardStr, year, month, day, addressStr, cityStr, provinceStr, postalCodeStr, phoneNumberStr);
                    String resp = null;
                    try {
                        resp = addUser.get();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    }

                    Toast.makeText(getApplicationContext(), resp, Toast.LENGTH_SHORT).show();
                    Intent adminUserListIntent = new Intent(this.getActivity(), AdminUserListActivity.class);
                    startActivity(adminUserListIntent);
                }

                else {
                    Toast.makeText(getApplicationContext(), "You are missing fields! Try Again!", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
}