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
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
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

import com.androidplot.xy.LineAndPointFormatter;
import com.androidplot.xy.SimpleXYSeries;
import com.androidplot.xy.XYPlot;
import com.androidplot.xy.XYSeries;
import com.androidplot.xy.XYStepMode;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.FieldPosition;
import java.text.Format;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.concurrent.ExecutionException;

/**
 * Created by Taranveer on 28/11/13.
 */
public class PatGraphActivity extends Activity {
    private DrawerLayout dLayout;
    private ListView dList;
    private ActionBarDrawerToggle dToggle;
    private CharSequence dTitle;
    private CharSequence winTitle;
    private String [] patientMenu;
    private Fragment fragment;
    private SharedPreferences loginPreferences;
    private String userid;
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
            selectItem(3);
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

        else if (position == 1) {
            Intent patientIntent = new Intent(this, PatientViewInfo.class);
            startActivity(patientIntent);
        }

        else if (position == 2) {
            Intent patientIntent = new Intent(this, LoginActivity.class);
            startActivity(patientIntent);
        }

        /*else if (position == 3) {
            Intent patientIntent = new Intent(this, GraphActivity.class);
            startActivity(patientIntent);
        }*/

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
            View rootView = getActivity().getLayoutInflater().inflate(R.layout.graph_plot, container, false);
            XYPlot plot1 = (XYPlot) rootView.findViewById(R.id.plot1);
            int i = ARG_PLANET_NUMBER;
            String planet = getResources().getStringArray(R.array.patientMenu)[i];

            getActivity().setTitle(planet);

            // MY STUFF

            // MAKE CALL HERE FOR SERVER DATA
            // ******************************


            // GRAPH DATA HERE
            //plot1 = (XYPlot) findViewById(R.id.plot1);
            Number[] numSightings = {89.0, 90.0, 100.0, 85.0, 87.0, 88.3};

            // an array of years in milliseconds: (one year = 31536000)
            Number[] years = {
                    1230595200, //2008
                    1262131200, //2009
                    1293667200, //2010
                    1325203200, //2011
                    1356739200, //2012
                    1388275200  //2013
            };
            // create our series from our array of nums:
            XYSeries series2 = new SimpleXYSeries(
                    Arrays.asList(years),
                    Arrays.asList(numSightings),
                    "");


            //plot1.getGraphWidget().getGridBackgroundPaint().setColor(Color.WHITE);
            try {
                plot1.getGraphWidget().getDomainGridLinePaint().setColor(Color.BLACK);
                plot1.getGraphWidget().getDomainGridLinePaint().
                        setPathEffect(new DashPathEffect(new float[]{1, 1}, 1));
                plot1.getGraphWidget().getRangeGridLinePaint().setColor(Color.BLACK);
                plot1.getGraphWidget().getRangeGridLinePaint().
                        setPathEffect(new DashPathEffect(new float[]{1, 1}, 1));
                plot1.getGraphWidget().getDomainOriginLinePaint().setColor(Color.BLACK);
                plot1.getGraphWidget().getRangeOriginLinePaint().setColor(Color.BLACK);
                System.out.println("SUCCESSFULLY HIT FIRST TRY AND FINISHED");
            } catch (Exception e) {
                e.printStackTrace();
            }


            // Create a formatter to use for drawing a series using LineAndPointRenderer:
            LineAndPointFormatter series1Format = new LineAndPointFormatter(
                    Color.rgb(0, 100, 0),                   // line color
                    Color.rgb(0, 100, 0),                   // point color
                    Color.rgb(100, 200, 0), null);          // fill color


            // setup our line fill paint to be a slightly transparent gradient:
            Paint lineFill = new Paint();
            lineFill.setAlpha(200);

            // ugly usage of LinearGradient. unfortunately there's no way to determine the actual size of
            // a View from within onCreate.  one alternative is to specify a dimension in resources
            // and use that accordingly.  at least then the values can be customized for the device type and orientation.
            //lineFill.setShader(new LinearGradient(0, 0, 200, 200, Color.WHITE, Color.GREEN, Shader.TileMode.CLAMP));

            LineAndPointFormatter formatter  =
                    new LineAndPointFormatter(Color.rgb(0, 0,0), Color.RED, Color.RED, null);
            formatter.setFillPaint(lineFill);
            try {
                plot1.getGraphWidget().setPaddingRight(2);
                plot1.addSeries(series2, formatter);

                // draw a domain tick for each year:
                plot1.setDomainStep(XYStepMode.SUBDIVIDE, years.length);

                // customize our domain/range labels
                plot1.setDomainLabel("Year");
                plot1.setRangeLabel("Patient Weight (lbs)");
            } catch (Exception e) {
                e.printStackTrace();
            }


            // get rid of decimal points in our range labels:
            //plot1.setRangeValueFormat(new DecimalFormat("0"));

            try {
                plot1.setDomainValueFormat(new Format() {

                    // create a simple date format that draws on the year portion of our timestamp.
                    // see http://download.oracle.com/javase/1.4.2/docs/api/java/text/SimpleDateFormat.html
                    // for a full description of SimpleDateFormat.
                    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy");

                    @Override
                    public StringBuffer format(Object obj, StringBuffer toAppendTo, FieldPosition pos) {

                        // because our timestamps are in seconds and SimpleDateFormat expects milliseconds
                        // we multiply our timestamp by 1000:
                        long timestamp = ((Number) obj).longValue() * 1000;
                        Date date = new Date(timestamp);
                        return dateFormat.format(date, toAppendTo, pos);
                    }

                    @Override
                    public Object parseObject(String source, ParsePosition pos) {
                        return null;

                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }


            // by default, AndroidPlot displays developer guides to aid in laying out your plot.
            // To get rid of them call disableAllMarkup():
            //plot1.disableAllMarkup();


            return rootView;
        }
    }
}
