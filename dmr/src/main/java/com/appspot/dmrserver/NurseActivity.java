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
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.appspot.dmrserver.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NurseActivity extends Activity {

    private ListView checkList;
    private Button checkListButton;
    private Button refreshButton;
    private DrawerLayout dLayout;
    private ListView dList;
    private ActionBarDrawerToggle dToggle;
    private CharSequence dTitle;
    private CharSequence winTitle;
    private String [] nurseMenu;
    private Fragment fragment;
    private SharedPreferences loginPreferences;
    public static final String PREFS_NAME = "DMRPrefsFile";

    ArrayList<String> listItems = new ArrayList<String>();
    ArrayList<String> patients = new ArrayList<String>();
    ArrayList<String> keys = new ArrayList<String>();
    ArrayList<String> users = new ArrayList<String>();

    private ItemsAdapter checkListAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.nurse_view_check_list);

        loginPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        final String userid = (loginPreferences.getString("userid", null));

        ServerComm.GetNurseTasks nurseTasks = new ServerComm.GetNurseTasks();
        nurseTasks.execute(userid);
        JSONObject resp = null;
        try
        {
            resp = nurseTasks.get();
        }
        catch (Exception e)
        {
            //Handle Exception
        }

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
        nurseMenu = getResources().getStringArray(R.array.nurseMenu);
        dLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        dList = (ListView) findViewById(R.id.left_drawer);

        // Set the adapter for the menu list view
        dList.setAdapter(new ArrayAdapter<String>(this,
                R.layout.drawer_list_item, nurseMenu));
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
            selectItem(0);
        }

        checkList = (ListView)findViewById(R.id.CheckList);
        checkListButton = (Button)findViewById(R.id.CheckListButton);
        refreshButton = (Button)findViewById(R.id.refreshButton);

        try {
            JSONArray jTasks = resp.getJSONArray("description");
            JSONArray jPatients = resp.getJSONArray("patientName");
            JSONArray jKeys = resp.getJSONArray("taskKey");
            JSONArray jUsernames = resp.getJSONArray("patientUsername");
            for(int i = 0; i < jTasks.length(); i++){
                listItems.add(jTasks.get(i).toString());
                patients.add(jPatients.get(i).toString());
                keys.add(jKeys.get(i).toString());
                users.add(jUsernames.get(i).toString());

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        checkListAdapter = new ItemsAdapter(this, listItems, users, patients);
        checkList.setAdapter(checkListAdapter);

        checkList.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                    long arg3) {
                CheckedTextView tv = (CheckedTextView)arg1;
                toggle(tv);
            }

        });

        checkListButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                for(int i = checkList.getChildCount() - 1; i >= 0; i--)
                {
                    View view = checkList.getChildAt(i);
                    CheckedTextView cv =(CheckedTextView)view.findViewById(R.id.checkList);
                    if(cv.isChecked())
                    {
                        listItems.remove(i);
                        cv.setChecked(false);
                    }
                }
                checkListAdapter.notifyDataSetChanged();
            }
        });

        refreshButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                listItems.clear();
                ServerComm.GetNurseTasks nurseTasks = new ServerComm.GetNurseTasks();
                nurseTasks.execute(userid);
                JSONObject resp = null;
                try
                {
                    resp = nurseTasks.get();
                }
                catch (Exception e)
                {
                    //Handle Exception
                }
                try {
                    JSONArray jTasks = resp.getJSONArray("description");
                    JSONArray jPatients = resp.getJSONArray("patientName");
                    JSONArray jKeys = resp.getJSONArray("taskKey");
                    JSONArray jUsernames = resp.getJSONArray("patientUsername");
                    for(int i = 0; i < jTasks.length(); i++){
                        listItems.add(jTasks.get(i).toString());
                        patients.add(jPatients.get(i).toString());
                        keys.add(jKeys.get(i).toString());
                        users.add(jUsernames.get(i).toString());

                    }
                }
                catch (JSONException e) {
                    e.printStackTrace();
                }
                checkListAdapter.notifyDataSetChanged();
            }
        });

    }

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

    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            selectItem(position);
        }
    }

    private void selectItem(int position) {

        if (position == 1) {
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
            setTitle(nurseMenu[position]);
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
            String planet = getResources().getStringArray(R.array.nurseMenu)[i];

            getActivity().setTitle(planet);

            return rootView;
        }
    }

    private class ItemsAdapter extends BaseAdapter {
        ArrayList<String> items;
        ArrayList<String> usernames;
        ArrayList<String> patientNames;

        public ItemsAdapter(Context context, ArrayList<String> item, ArrayList<String> user, ArrayList<String> patient) {
            for(int i = 0; i < item.size(); i++){
                items = item;
                usernames = user;
                patientNames = patient;
            }
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            View v = convertView;
            if (v == null) {
                LayoutInflater vi = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                v = vi.inflate(R.layout.check_list_items, null);
            }
            CheckedTextView post = (CheckedTextView) v.findViewById(R.id.checkList);
            post.setText("Patient: " + patientNames.get(position) + "\nuserid: " + usernames.get(position) + "\n\n" + items.get(position));

            return v;
        }

        public int getCount() {
            return items.size();
        }

        public Object getItem(int position) {
            return position;
        }

        public long getItemId(int position) {
            return position;
        }
    }

    public void toggle(CheckedTextView v)
    {
        if (v.isChecked())
        {
            v.setChecked(false);
        }
        else
        {
            v.setChecked(true);
        }
    }
}