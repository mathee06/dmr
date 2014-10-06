package com.appspot.dmrserver;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;



import android.app.PendingIntent;
import android.content.Context;
import android.content.IntentFilter;
import android.nfc.NdefMessage;
import android.nfc.NfcAdapter;
import android.nfc.NfcEvent;
import android.os.Vibrator;
import android.util.Log;


import com.pushbots.push.Pushbots;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.ExecutionException;
import static com.appspot.dmrserver.ServerComm.*;

/**
 * This is the main class for LoginActivity (Login Screen)
 * Taranveer Virk
 */
public class LoginActivity extends Activity {

    private static final String SENDER_ID = "785527193334";
    private static final String PUSHBOTS_APPLICATION_ID = "529c442a4deeae0108000083";
    // Variables Declared
    private ProgressBar pb;
    private Button loginButton;
    private EditText username, password;
    private TextView message;
    private String user, pass;
    private int loginAttempts = 0;
    private Boolean validLogin = false;
    private String [] previousAttempts = new String [5];
    public static final String PREFS_NAME = "DMRPrefsFile";
    private static final String PREF_USERNAME = "username";
    private static final String PREF_PASSWORD = "password";
    private static final String PREF_USERTYPE = "userType";
    private boolean launchedCheckIn = false;
    private SharedPreferences loginPreferences;
    private SharedPreferences.Editor loginPrefsEditor;

    private static String TAG = LoginActivity.class.getSimpleName();

    protected NfcAdapter nfcAdapter;
    protected PendingIntent nfcPendingIntent;

    /*  loginPrefsEditor.clear();
        loginPrefsEditor.commit();

        USE THIS TO LOGOUT USER AND CLEAR SHAREDPREFERENCES
    */

    /*
    This method creates the Main Login Screen for the User.
    Also creates UI variables and sets some UI Properties.
    If user has saved credentials then they are logged back in.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) throws NullPointerException {
        // Creating the Screen
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Pushbots.init(this, SENDER_ID, PUSHBOTS_APPLICATION_ID);

        // initialize NFC
        nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        nfcPendingIntent = PendingIntent.getActivity(this, 0, new Intent(this, this.getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);


        if(getIntent().hasExtra(NfcAdapter.EXTRA_TAG)) {
            Toast.makeText(getApplicationContext(), "Attempting NFC Check-in",
                    Toast.LENGTH_SHORT).show();
            launchedCheckIn = true;
            vibrate(); // signal detected tag :-)
        }

        // SharedPreferences for Credential Storage
        loginPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        loginPrefsEditor = loginPreferences.edit();

        // Checking if credential already stored
        user = loginPreferences.getString("username", null);
        pass = loginPreferences.getString("password", null);

        // UI Elements
        pb = (ProgressBar) findViewById(R.id.progressBar1);
        username = (EditText) findViewById(R.id.username);
        password = (EditText) findViewById(R.id.password);
        message = (TextView) findViewById(R.id.test);
        loginButton = (Button) findViewById(R.id.button);

        // Restoring Saved Login Data if any
        if (user != null && pass != null) {
            System.out.println("LOGIN RESTORED");
            Toast.makeText(getApplicationContext(), "Logging In ...", Toast.LENGTH_SHORT).show();
            username.setText(user, TextView.BufferType.EDITABLE);
            password.setText(pass, TextView.BufferType.EDITABLE);


            if(launchedCheckIn)
            {
                String utype = loginPreferences.getString("userType", null);
                if (utype.equals("Patient")) {
                    Toast.makeText(getApplicationContext(), "Checking in via NFC",
                            Toast.LENGTH_SHORT).show();
                    //CHECK IN LOGIC
                    Checkin check = new Checkin();
                    String userid = loginPreferences.getString("userid", null);
                    check.execute(userid);
                    JSONObject resp = null;
                    try {
                        resp = check.get();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    }
                    try {
                        Toast.makeText(getApplicationContext(), resp.getString("status"),
                                Toast.LENGTH_LONG).show();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                else {
                    Toast.makeText(getApplicationContext(), "Only Patients Can Check In via NFC!",
                            Toast.LENGTH_LONG).show();
                    loginButton.performClick();
                }

                launchedCheckIn = false;

                //CHECK IN LOGIC GOES HERE!!!!!!!!!
            }

            //Toast.makeText(getApplicationContext(), "Logging In ...", Toast.LENGTH_SHORT).show();
            loginButton.performClick();

        }
        else
        {
            if(launchedCheckIn)
            {
                Toast.makeText(getApplicationContext(), "Please login before checking in",
                        Toast.LENGTH_SHORT).show();
                vibrate();
                launchedCheckIn = false;
            }
        }

        // Setting properties of UI Elements
        pb.setVisibility(View.GONE);
    }

    public void enableForegroundMode() {
        Log.d(TAG, "enableForegroundMode");

        // foreground mode gives the current active application priority for reading scanned tags
        IntentFilter tagDetected = new IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED); // filter for tags
        IntentFilter[] writeTagFilters = new IntentFilter[] {tagDetected};
        nfcAdapter.enableForegroundDispatch(this, nfcPendingIntent, writeTagFilters, null);
    }

    public void disableForegroundMode() {
        Log.d(TAG, "disableForegroundMode");

        nfcAdapter.disableForegroundDispatch(this);
    }

    @Override
    protected void onResume() {
        super.onResume();

        Log.d(TAG, "onResume");

        // enableForegroundMode();
    }

    @Override
    protected void onPause() {
        super.onResume();

        Log.d(TAG, "onPause");

        // disableForegroundMode();
    }

    @Override
    public void onNewIntent(Intent intent) { // this method is called when an NFC tag is scanned
        Log.d(TAG, "onNewIntent");
        if (NfcAdapter.ACTION_TAG_DISCOVERED.equals(intent.getAction())) {
            Log.d(TAG, "A tag was scanned!");
            //TextView textView = (TextView) findViewById(R.id.title);
            //textView.setText("Hello NFC tag!");
            vibrate(); // signal detected tag :-)
        }
    }


    public NdefMessage createNdefMessage(NfcEvent event) {

        Log.d(TAG, "createNdefMessage");

        throw new IllegalArgumentException("Not implemented");
    }

    public void onNdefPushComplete(NfcEvent arg0) {
        Log.d(TAG, "onNdefPushComplete");


        throw new IllegalArgumentException("Not implemented");
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        // Do some operations here
    }

    /*
    Menu Controller. Default Code.
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.login, menu);
        return true;
    }

    /*
    Submit Button Controller. Clicking the Button starts the Asynchronous Login Task.
     */
    public void submitButton (View v) throws NullPointerException, ExecutionException, InterruptedException, JSONException, InvocationTargetException, IllegalStateException {
        // Getting the Username and Password field!
        user = username.getText().toString();
        pass = password.getText().toString();

        System.out.println (user);
        System.out.println (pass);

        // Server Call to Login
        LoginToServer logMeIn = new LoginToServer(this);
        logMeIn.execute(user, pass);
        JSONObject resp = logMeIn.get();

        // Checking Server Response -- Valid Login
        if (resp.getString("status").equals("VALID LOGIN")) {
            // Set Variables & Toast
            validLogin = true;
            loginAttempts = 0;
            Toast.makeText(getApplicationContext(), "Successful Login", Toast.LENGTH_SHORT).show();

            // Storing Preferences for User
            loginPrefsEditor.putString(PREF_USERNAME, user);
            loginPrefsEditor.putString(PREF_PASSWORD, pass);
            loginPrefsEditor.putString("userid", resp.getString("userid"));
            loginPrefsEditor.putString(PREF_USERTYPE, resp.getString("userType"));
            System.out.println (resp.getString("userid"));
            loginPrefsEditor.commit();
        }

        // Checking Server Response -- Not Valid Login
        // loginAttempts increments by 1 if userName is in Database and hasn't been locked out yet.
        else if (!resp.getString("status").equals("NOT IN DATABASE") && !resp.getString("status").equals("USER LOCKED OUT")) {
            previousAttempts[loginAttempts] = resp.getString("username");
            loginAttempts++;
            password.setText("", TextView.BufferType.EDITABLE);

            if (loginAttempts < 5) {
                Toast.makeText(getApplicationContext(), "Invalid Login! Try Again!", Toast.LENGTH_SHORT).show();
            }

            // Locking out User if previous 5 attempts were on same username
            if (loginAttempts == 5) {
                // Locking Out User
                if (sameUserAttempts(previousAttempts)) {
                    // Lockout out username for 15 minutes for 5 invalid attempts
                    LockoutUser lockout = new LockoutUser();
                    lockout.execute(user);

                    loginAttempts = 0;
                    previousAttempts = null;

                    // Lockout Notification
                    Toast.makeText(getApplicationContext(), "User Has Been Locked Out for 15 Minutes!", Toast.LENGTH_SHORT).show();
                }

                // Not same username for last 5 Attempts
                else {
                    loginAttempts = 4;

                    // Shifting Content of Array
                    for (int i=0; i<4; i++) {
                        previousAttempts[i] = previousAttempts[i+1];
                    }
                    previousAttempts[4] = null;
                }
            }
        }

        // Invalid Login -- User Already Locked Out
        else if (resp.getString("status").equals("USER LOCKED OUT")) {
            Toast.makeText(getApplicationContext(), "Account Has Been Locked Out! Try Later!", Toast.LENGTH_SHORT).show();
            password.setText("", TextView.BufferType.EDITABLE);
        }

        // Any Other Status -- Clear Password Field
        else {
            password.setText("", TextView.BufferType.EDITABLE);
        }

        System.out.println(validLogin);

        // Redirect User to new screen based on userType. Credentials must be correct
        if(validLogin){
            if(resp.getString("userType").equals("Admin")){
                Intent adminIntent = new Intent(this, AdminUserListActivity.class);
                startActivity(adminIntent);
            }

            else if(resp.getString("userType").equals("Doctor")){
                Intent doctorIntent = new Intent(this, DoctorApptActivity.class);
                startActivity(doctorIntent);
            }

            else if(resp.getString("userType").equals("Nurse")){
                Intent nurseIntent = new Intent(this, NurseActivity.class);
                startActivity(nurseIntent);
            }

            else if(resp.getString("userType").equals("Patient")){
                System.out.println("HIT");
                Intent patientIntent = new Intent(this, PatientApptActivity.class);
                startActivity(patientIntent);
                System.out.println("HIT AGAIN");
            }

            else if(resp.getString("userType").equals("Secretary")){
                Intent secretaryIntent = new Intent(this, SecretaryActivity.class);
                startActivity(secretaryIntent);
            }

            finish();
        }

        // Displaying Server Result
        message.setText(resp.toString());
    }   // submitButton()

    /*
    Method to check if previous 5 login attempts have been for the same username.
    Return true if previous 5 attempts were by same user. Return false otherwise.
     */
    public boolean sameUserAttempts (String [] attempts) {
        boolean valid = true;
        for (int i=1; i<5; i++){
            if (attempts[0].equals(attempts[i]) == false){
                valid = false;
            }
        }
        return valid;
    }   // sameUserAttempts()

    /**
     * Activate device vibrator for 500 ms
     * */

    private void vibrate() {
        Log.d(TAG, "vibrate");

        Vibrator vibe = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE) ;
        vibe.vibrate(250);
    }
}