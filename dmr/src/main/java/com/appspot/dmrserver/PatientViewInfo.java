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
public class PatientViewInfo extends Activity {
    private DrawerLayout dLayout;
    private ListView dList;
    private ActionBarDrawerToggle dToggle;
    private CharSequence dTitle;
    private CharSequence winTitle;
    private String [] patientMenu;
    private EditText firstNameEdit, lastNameEdit, addressEdit, cityEdit, postalCodeEdit, phoneEdit;
    private String firstNameStr, lastNameStr, addressStr, cityStr, postalCodeStr, phoneStr, provinceStr, userid;
    private Spinner provinceSpinner;
    private Fragment fragment;
    private Button editButton, saveButton, cancelButton;
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
        patientMenu = getResources().getStringArray(R.array.patientMenu);
        dLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        dList = (ListView) findViewById(R.id.left_drawer);
        loginPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        userid = loginPreferences.getString("userid", null);

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
            Intent patientApptIntent = new Intent(this, PatientApptActivity.class);
            startActivity(patientApptIntent);
        }

        /*if (position == 1) {
            Intent patientIntent = new Intent(this, PatientViewInfo.class);
            startActivity(patientIntent);
        }*/

        else if (position == 2) {
            Intent patientIntent = new Intent(this, LoginActivity.class);
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
            fragment = new PlanetFragment(position  );
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
    public class PlanetFragment extends Fragment implements View.OnClickListener {
        //public static final String ARG_PLANET_NUMBER = "planet_number";
        int ARG_PLANET_NUMBER;

        public PlanetFragment(int position) {
            ARG_PLANET_NUMBER = position;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = getActivity().getLayoutInflater().inflate(R.layout.patient_personal_info_fragment, container, false);
            int i = ARG_PLANET_NUMBER;
            String planet = getResources().getStringArray(R.array.patientMenu)[i];

            getActivity().setTitle(planet);

            // MY STUFF
            firstNameEdit = (EditText) rootView.findViewById (R.id.firstNameEdit);
            lastNameEdit = (EditText) rootView.findViewById (R.id.lastNameEdit);
            addressEdit = (EditText) rootView.findViewById (R.id.addressEdit);
            cityEdit = (EditText) rootView.findViewById (R.id.cityEdit);
            postalCodeEdit = (EditText) rootView.findViewById (R.id.postalCodeEdit);
            phoneEdit = (EditText) rootView.findViewById (R.id.phoneNumberEdit);
            provinceSpinner = (Spinner) rootView.findViewById(R.id.provinceSpinner);
            editButton = (Button) rootView.findViewById(R.id.editButton);
            saveButton = (Button) rootView.findViewById(R.id.saveButton);
            cancelButton = (Button) rootView.findViewById(R.id.cancelButton);

            editButton.setOnClickListener(this);
            saveButton.setOnClickListener(this);
            cancelButton.setOnClickListener(this);

            ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity().getBaseContext(),
                    R.array.provinces_array, android.R.layout.simple_spinner_item);

            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            // Apply the adapter to the spinner
            provinceSpinner.setAdapter(adapter);

            firstNameEdit.setEnabled(false);
            lastNameEdit.setEnabled(false);
            addressEdit.setEnabled(false);
            cityEdit.setEnabled(false);
            postalCodeEdit.setEnabled(false);
            phoneEdit.setEnabled(false);
            provinceSpinner.setEnabled(false);

            ServerComm.PatientPersonalInfo personalInfo = new ServerComm.PatientPersonalInfo();
            System.out.println(userid);

            personalInfo.execute(userid);
            JSONObject resp = null;
            try {
                resp = personalInfo.get();
                System.out.println("FETCHED PERSONAL INFO");
                System.out.println(resp.getString("address"));
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (NullPointerException e) {
                e.printStackTrace();
            }

            try {
                firstNameStr = resp.getString("firstName");
                System.out.println(firstNameStr);
                lastNameStr = resp.getString("lastName");
                addressStr = resp.getString("address");
                postalCodeStr = resp.getString("postalCode");
                phoneStr = resp.getString("phoneNumber");
                cityStr = resp.getString("city");
                provinceStr = resp.getString("province");
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (NullPointerException e) {
                e.printStackTrace();
            }


            firstNameEdit.setText(firstNameStr, TextView.BufferType.NORMAL);
            lastNameEdit.setText(lastNameStr, TextView.BufferType.NORMAL);
            addressEdit.setText(addressStr, TextView.BufferType.NORMAL);
            phoneEdit.setText(phoneStr, TextView.BufferType.NORMAL);
            postalCodeEdit.setText(postalCodeStr, TextView.BufferType.NORMAL);
            cityEdit.setText(cityStr, TextView.BufferType.NORMAL);
            provinceSpinner.setSelection(provinceNumber(provinceStr));

            saveButton.setVisibility(View.GONE);
            cancelButton.setVisibility(View.GONE);

            //String Text = provinceSpinner.getSelectedItem().toString();
            //System.out.println(Text);

            return rootView;
        }

        @Override
        public void onClick(View v) {
            if (v.getId() == editButton.getId()) {
                editButton.setVisibility(View.GONE);
                saveButton.setVisibility(View.VISIBLE);
                cancelButton.setVisibility(View.VISIBLE);

                addressEdit.setEnabled(true);
                cityEdit.setEnabled(true);
                postalCodeEdit.setEnabled(true);
                phoneEdit.setEnabled(true);
                provinceSpinner.setEnabled(true);
            }

            else if (v.getId() == cancelButton.getId()) {
                // RESTORE ORIGINAL VALUES IN FIELD
                firstNameEdit.setText(firstNameStr, TextView.BufferType.NORMAL);
                lastNameEdit.setText(lastNameStr, TextView.BufferType.NORMAL);
                addressEdit.setText(addressStr, TextView.BufferType.NORMAL);
                phoneEdit.setText(phoneStr, TextView.BufferType.NORMAL);
                postalCodeEdit.setText(postalCodeStr, TextView.BufferType.NORMAL);
                cityEdit.setText(cityStr, TextView.BufferType.NORMAL);
                provinceSpinner.setSelection(provinceNumber(provinceStr));

                editButton.setVisibility(View.VISIBLE);
                cancelButton.setVisibility(View.GONE);
                saveButton.setVisibility(View.GONE);
                addressEdit.setEnabled(false);
                cityEdit.setEnabled(false);
                postalCodeEdit.setEnabled(false);
                phoneEdit.setEnabled(false);
                provinceSpinner.setEnabled(false);
            }

            else if (v.getId() == saveButton.getId()) {
                Boolean valid = true;
                if (addressEdit.getText().toString().length() == 0 || cityEdit.getText().toString().length() == 0 || postalCodeEdit.getText().toString().length() == 0 || phoneEdit.getText().toString().length() == 0) {
                    valid = false;
                }

                if (valid) {
                    // MAKE REQUEST TO SERVER TO SAVE THE NEW INFORMATION!
                    addressStr = addressEdit.getText().toString();
                    cityStr = cityEdit.getText().toString();
                    postalCodeStr = postalCodeEdit.getText().toString();
                    phoneStr = phoneEdit.getText().toString();
                    provinceStr = provinceSpinner.getSelectedItem().toString();

                    editButton.setVisibility(View.VISIBLE);
                    cancelButton.setVisibility(View.GONE);
                    saveButton.setVisibility(View.GONE);
                    addressEdit.setEnabled(false);
                    cityEdit.setEnabled(false);
                    postalCodeEdit.setEnabled(false);
                    phoneEdit.setEnabled(false);
                    provinceSpinner.setEnabled(false);

                    ServerComm.UpdatePatInfo updateInfo = new ServerComm.UpdatePatInfo();
                    updateInfo.execute(loginPreferences.getString("userid", null), addressStr, cityStr, provinceStr, postalCodeStr, phoneStr);
                    JSONObject resp = null;
                    try {
                        resp = updateInfo.get();
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
