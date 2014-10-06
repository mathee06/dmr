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
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by labanar on 12/2/2013.
 */
public class SecretaryAddAppt extends Activity {
    private SharedPreferences loginPreferences;
    public static final String PREFS_NAME = "DMRPrefsFile";
    private Spinner doctorListSpinner, patientListSpinner;
    private Button submitButton, cancelButton;
    private DatePicker datePicker;
    private TimePicker timePicker;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.secretary_appointment_add);

        doctorListSpinner = (Spinner) findViewById(R.id.doctorList);
        /*patientListSpinner = (Spinner) findViewById(R.id.patientList);
        submitButton = (Button) findViewById(R.id.submitButton);
        cancelButton = (Button) findViewById(R.id.cancelButton);
        datePicker = (DatePicker) findViewById(R.id.datePicker);
        timePicker = (TimePicker) findViewById(R.id.timePicker);
*/
        submitButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // ADD APPOINTMENT SERVERCOMM CALL
                ServerComm.AddAppointment addAppointment = new ServerComm.AddAppointment();
                loginPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
                String userid = (loginPreferences.getString("userid", null));
                String year = Integer.toString(datePicker.getYear());
                String month = Integer.toString(datePicker.getMonth() + 1);
                String day = Integer.toString(datePicker.getDayOfMonth());
                String hour = Integer.toString(timePicker.getCurrentHour());
                String minute = Integer.toString(timePicker.getCurrentMinute());
                String patUsername = patientListSpinner.getSelectedItem().toString();
                String docUsername = doctorListSpinner.getSelectedItem().toString();

                addAppointment.execute(userid,year,month,day,hour,minute,patUsername,docUsername,Integer.toString(15)); //userid, year, month, day, hour, minute, patientusername, doctorusername, duration mins
                //System.out.println();
                JSONObject resp = null;
                try
                {
                    resp = addAppointment.get();
                }
                catch (Exception e)
                {
                   //Handle Exception
                }
                System.out.println(resp.toString());
                System.out.println(year + "\n" + month + "\n" + day);

                finish(); //Return to previous Activity upon completion
            }
        });
        cancelButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // return to previous Activity
                finish();
            }
        });

        //Populate Spinner with Doctor List
        loginPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        String username = (loginPreferences.getString("username", null));

        List<Map<String, String>> data = new ArrayList<Map<String, String>>();

        ServerComm.DoctorNameListServer doctorServerList = new ServerComm.DoctorNameListServer();
        ServerComm.PatientListServer patientListServer = new ServerComm.PatientListServer();
        doctorServerList.execute(username);
        patientListServer.execute(username);
        JSONObject resp = null;
        JSONObject resp2 = null;
        try
        {
            resp = doctorServerList.get();
            resp2 = patientListServer.get();
        }
        catch (Exception e)
        {
            //Handle Exception
        }

        JSONArray doctorNamesList = null;
        JSONArray doctorIDList = null;
        JSONArray doctorUsernameList = null;
        JSONArray patUsernameList = null;
        try
        {
            doctorUsernameList = resp.getJSONArray("docUsername");
            doctorNamesList = resp.getJSONArray("docNameList");
            doctorIDList = resp.getJSONArray("userIdList");
            patUsernameList = resp2.getJSONArray("patUserName");
            System.out.println("DOCTOR LIST SIZE = " + doctorNamesList.length());
            System.out.println("PATIENT LIST SIZE = " + patUsernameList.length());


            List<String> list = new ArrayList<String>();

            for(int i = 0; i < doctorUsernameList.length(); i++){
                list.add(doctorUsernameList.getString(i));
            }

            List<String> list2 = new ArrayList<String>();

            for(int i = 0; i < patUsernameList.length(); i++){
                list2.add(patUsernameList.getString(i));
            }

            ArrayAdapter adapter = new ArrayAdapter(this,
                    android.R.layout.simple_spinner_item, list);
            doctorListSpinner.setAdapter(adapter);

            ArrayAdapter adapter2 = new ArrayAdapter(this,
                    android.R.layout.simple_spinner_item, list2);
            patientListSpinner.setAdapter(adapter2);

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
}
