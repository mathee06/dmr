package com.appspot.dmrserver;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.sIntentFilter;
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
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

/**
 * Created by Home on 11/29/2013.
 */

public class AdminUserListActivity extends Activity {
    private DrawerLayout dLayout;
    private ListView dList;
    private ActionBarDrawerToggle dToggle;
    private CharSequence dTitle;
    private CharSequence winTitle;
    private String [] adminMenu;
    private Fragment fragment;
    private ListView userList;
    public static final String PREFS_NAME = "DMRPrefsFile";
    private SharedPreferences loginPreferences;
    List<Map<String, String>> data = new ArrayList<Map<String, String>>();
    SimpleAdapter adapter;
    boolean firstLoad = true;
    String userid = "";
    String userKey = "";

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
            selectItem(0);
        }
        firstLoad = false;
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
            Intent adminUserListIntent = new Intent(this, AdminUserListActivity.class);
            startActivity(adminUserListIntent);
        }*/

        if (position == 1) {
            Intent adminAddUserIntent = new Intent(this, AdminAddNewUserActivity.class);
            startActivity(adminAddUserIntent);
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
    public class PlanetFragment extends Fragment {
        //public static final String ARG_PLANET_NUMBER = "planet_number";
        int ARG_PLANET_NUMBER;

        public PlanetFragment(int position) {
            ARG_PLANET_NUMBER = position;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = getActivity().getLayoutInflater().inflate(R.layout.admin_view_user_list, container, false);
            int i = ARG_PLANET_NUMBER;
            String planet = getResources().getStringArray(R.array.adminMenu)[i];
            System.out.println(planet);
            System.out.println(i);

            getActivity().setTitle(planet);

            // MY STUFF
            userList = (ListView) rootView.findViewById(R.id.listView);

            loginPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
            userid = (loginPreferences.getString("userid", null));
            System.out.println ("USERID" + userid);

            // MAKE SERVER CALL FOR DATA FOR APPOINTMENTS HERE!!
            // *************************************************
            //List<Map<String, String>> data = new ArrayList<Map<String, String>>();

            data = new ArrayList<Map<String, String>>();

            ServerComm.UserList variableName = new ServerComm.UserList();
            variableName.execute();
            JSONObject response = null;
            try {
                response = variableName.get();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }

            JSONArray userNames = null;
            JSONArray userPersonNames = null;
            JSONArray userKeys = null;

            try {
                userNames = response.getJSONArray("userName");
                userPersonNames = response.getJSONArray("personName");
                userKeys = response.getJSONArray("userKey");
            } catch (JSONException e) {
                e.printStackTrace();
            }

            for (int ij=0; ij < userNames.length(); ij++) {
                Map <String, String> hm = new HashMap<String, String>(2);
                try {
                    hm.put("text1", userPersonNames.getString(ij));
                    hm.put("text2", userNames.getString(ij));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                data.add(hm);
            }

            //SimpleAdapter
            adapter = new SimpleAdapter(getActivity(), data,
                    android.R.layout.simple_list_item_2,
                    new String[] {"text1", "text2"},
                    new int[] {android.R.id.text1, android.R.id.text2});
            userList.setAdapter(adapter);


            final JSONArray userKeysF = userKeys;
            final JSONArray userNameF = userNames;


            userList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> parent, View view,
                                        int position, long id) {

                    // ListView Clicked item index
                    final int itemPosition = position;

                    // ListView Clicked item value
                    String userName = "";
                    try {
                        userKey = userKeysF.getString(position);
                        userName = userNameF.getString(position);
                        System.out.println(userKey);
                        System.out.println(userid);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(AdminUserListActivity.this);
                    alertDialog.setTitle("Delete User");
                    alertDialog.setMessage("Delete " + userName + "?\nAll user data will be lost!");

                    alertDialog.setPositiveButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            //Do nothing
                        }
                    });

                    alertDialog.setNegativeButton("Delete", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            ServerComm.RemoveUser removeUser = new ServerComm.RemoveUser();
                            removeUser.execute(userid, userKey);
                            data.remove(itemPosition);
                            adapter.notifyDataSetChanged();
                        }
                    });
                    alertDialog.show();
                }
            });
            return rootView;
        }
    }
}

