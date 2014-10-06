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
import android.widget.EditText;
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
 * Created by Mathee on 12/2/2013.
 */
public class DoctorAddRecordActivity extends Activity {
    private SharedPreferences loginPreferences;
    public static final String PREFS_NAME = "DMRPrefsFile";
    private EditText patWeight, patHeight, patNotes;
    private Button submitButton, cancelButton;
    private EditText weight, height, notes;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.doctor_add_rec);

        String patientKey = null;
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            patientKey = extras.getString("patientKey");
        }

        final String PATkey = patientKey;

        submitButton = (Button) findViewById(R.id.submitButton);
        cancelButton = (Button) findViewById(R.id.cancelButton);
        weight = (EditText) findViewById(R.id.patWeight);
        height = (EditText) findViewById(R.id.patHeight);
        notes = (EditText) findViewById(R.id.patNotes);


        submitButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // ADD APPOINTMENT SERVERCOMM CALL

                loginPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
                String userid = (loginPreferences.getString("userid", null));
                String w = weight.getText().toString();
                String h = height.getText().toString();
                String n = notes.getText().toString();

                ServerComm.AddPatientRecord addPatientRecord = new ServerComm.AddPatientRecord();
                addPatientRecord.execute(userid,PATkey,n,w,h); //docID,patID,notes,weight, height
                finish();
            }
        });
        cancelButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                finish();
            }
        });
    }
}
