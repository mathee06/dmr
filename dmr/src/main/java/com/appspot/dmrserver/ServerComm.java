package com.appspot.dmrserver;

import android.app.Activity;
import android.os.AsyncTask;
import android.view.View;
import android.widget.ProgressBar;

import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Taranveer Virk on 31/10/13.
 * This is the Server Communication Class. It has classes
 * and methods for communicating with the server
 */
public class ServerComm {

    private static final String LOGIN_URL = "https://www.dmrserver.appspot.com/login";
    private static final String ADD_NEW_USER_URL = "https://www.dmrserver.appspot.com/addnewuser";
    private static final String LOCKOUT_USER_URL = "https://www.dmrserver.appspot.com/lockoutuser";
    private static final String DOCTOR_LIST_URL = "https://www.dmrserver.appspot.com/doctorlist";
    private static final String PATIENT_LIST_URL = "https://www.dmrserver.appspot.com/patientlist";
    private static final String PATIENT_APPT_LIST_URL = "https://www.dmrserver.appspot.com/patientappts";
    private static final String PATIENT_PERSONAL_INFO_URL = "https://www.dmrserver.appspot.com/patpersonalinfo";
    private static final String UPDATE_PAT_INFO_URL = "https://www.dmrserver.appspot.com/updatepatinfo";
    private static final String CHECKIN_URL = "https://www.dmrserver.appspot.com/checkin";
    private static final String DOC_APPTS_URL = "https://www.dmrserver.appspot.com/docappts";
    private static final String USER_LIST_URL = "https://www.dmrserver.appspot.com/userlist";
    private static final String REMOVE_USER_URL = "https://www.dmrserver.appspot.com/removeuser";
    private static final String NURSE_TASKS_URL = "https://www.dmrserver.appspot.com/getnursetask";
    private static final String ADD_NURSE_TASKS_URL = "https://www.dmrserver.appspot.com/addnursetask";
    private static final String REMOVE_NURSE_TASKS_URL = "https://www.dmrserver.appspot.com/removenursetasks";
    private static final String ADD_PAT_RECORD_URL = "https://www.dmrserver.appspot.com/addpatientrecord";
    private static final String GET_PAT_RECORD_URL = "https://www.dmrserver.appspot.com/patgetrecord";
    private static final String GRAPH_DATA_URL = "https://www.dmrserver.appspot.com/graphdata";
    private static final String ADD_APPT_URL = "https://www.dmrserver.appspot.com/addappointment";
    private static final String REMOVE_APPT_URL = "https://www.dmrserver.appspot.com/removeappointment";


    /**
     * Creates a HTTP Client with SSL Encryption to communicate with the server.
     * @return
     */
    private static HttpClient createHttpClient()
    {
        HttpParams params = new BasicHttpParams();
        HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
        HttpProtocolParams.setContentCharset(params, HTTP.DEFAULT_CONTENT_CHARSET);
        HttpProtocolParams.setUseExpectContinue(params, true);

        SchemeRegistry schReg = new SchemeRegistry();
        schReg.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
        schReg.register(new Scheme("https", SSLSocketFactory.getSocketFactory(), 443));
        ClientConnectionManager conMgr = new ThreadSafeClientConnManager(params, schReg);

        return new DefaultHttpClient(conMgr, params);
    }

    /**
     * Class to convert inputStream to a String.
     * @param is
     * @return
     */
    private static String inputStreamToString(InputStream is) {
        // Variables Declared
        String line = "";
        StringBuilder total = new StringBuilder();

        // Wrap a BufferedReader around the InputStream
        BufferedReader rd = new BufferedReader(new InputStreamReader(is));

        // Read response until the end
        try {
            while ((line = rd.readLine()) != null) {
                total.append(line);
            }
        }

        catch (IOException e) {
            // TODO Auto-generated catch block
        }

        // Return full string
        return String.valueOf(total);
    }

    /**
     * Creates a new Asynchronous Task to Add New User into the Server
     */
    public static class AddNewUserToServer extends AsyncTask <String, Integer, String> {

        // Variables for Class
        //private final Activity callerActivity;

        /**
         * Constructor. Takes in the Calling Activity. This allows access to Calling
         * Activity UI.
         * @param act
         */
        AddNewUserToServer(){
        }

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected String doInBackground(String... strings) {
            // Variables Declared
            HttpClient httpclient = createHttpClient();
            HttpPost httppost = new HttpPost(ADD_NEW_USER_URL);
            String serverResponse = null;

            try {
                // Add your data
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
                nameValuePairs.add(new BasicNameValuePair("firstName", strings[0]));
                nameValuePairs.add(new BasicNameValuePair("lastName", strings[1]));
                nameValuePairs.add(new BasicNameValuePair("userName", strings[2]));
                nameValuePairs.add(new BasicNameValuePair("password", strings[3]));
                nameValuePairs.add(new BasicNameValuePair("userType", strings[4]));
                if (strings[4].equals("Patient")) {
                    nameValuePairs.add(new BasicNameValuePair("doctorUserName", strings[5]));
                    nameValuePairs.add(new BasicNameValuePair("healthCard", strings[6]));
                    nameValuePairs.add(new BasicNameValuePair("birthYear", strings[7]));
                    nameValuePairs.add(new BasicNameValuePair("birthMonth", strings[8]));
                    nameValuePairs.add(new BasicNameValuePair("birthDay", strings[9]));
                    nameValuePairs.add(new BasicNameValuePair("address", strings[10]));
                    nameValuePairs.add(new BasicNameValuePair("city", strings[11]));
                    nameValuePairs.add(new BasicNameValuePair("province", strings[12]));
                    nameValuePairs.add(new BasicNameValuePair("postalCode", strings[13]));
                    nameValuePairs.add(new BasicNameValuePair("phoneNumber", strings[14]));
                }

                httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

                // Execute HTTP Post Request
                HttpResponse response = httpclient.execute(httppost);

                // Convert Server Response to String
                serverResponse = inputStreamToString(response.getEntity().getContent());

            } catch (Exception e) {
                e.printStackTrace();
            }

            // Return Server Response
            return serverResponse;
        }

        @Override
        protected void onProgressUpdate(Integer... progress){
        }

        @Override
        protected void onPostExecute(String result){
        }
    }

    /**
     * Creates a new Asynchronous Task to Lockout a User Account
     */
    public static class LockoutUser extends AsyncTask<String, Integer, String> {

        @Override
        protected String doInBackground(String... strings) {
            // Variables Declared
            HttpClient httpclient = createHttpClient();
            HttpPost httppost = new HttpPost(LOCKOUT_USER_URL);
            String serverResponse = null;

            try {
                // Add your data
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
                nameValuePairs.add(new BasicNameValuePair("user", strings[0]));
                httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

                // Execute HTTP Post Request
                HttpResponse response = httpclient.execute(httppost);

                // Convert Server Response to String
                serverResponse = inputStreamToString(response.getEntity().getContent());

            } catch (Exception e) {
                e.printStackTrace();
            }

            // Return Server Response
            return serverResponse;
        }
    }

    /**
     * Creates a new Asynchronous Task to Retrieve Doctor Name List
     */
    public static class DoctorNameListServer extends AsyncTask <String, Integer, JSONObject> {

        // Variables for Class

        /**
         * Constructor. Takes in the Calling Activity. This allows access to Calling
         * Activity UI.
         */
        DoctorNameListServer(){
            super();
        }

        @Override
        protected JSONObject doInBackground(String... strings) {
            // Variables Declared
            HttpClient httpclient = createHttpClient();
            HttpPost httppost = new HttpPost(DOCTOR_LIST_URL);
            String serverResponse;
            JSONObject serverJSON = null;

            try {
                // Add your data
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
                nameValuePairs.add(new BasicNameValuePair("username", strings[0]));
                httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

                // Execute HTTP Post Request
                HttpResponse response = httpclient.execute(httppost);

                // Convert Server Response to String
                serverResponse = inputStreamToString(response.getEntity().getContent());

                serverJSON = new JSONObject(serverResponse);

            } catch (Exception e) {
                e.printStackTrace();
            }

            // Return Server Response
            return serverJSON;
        }
    }

    public static class PatientPersonalInfo extends AsyncTask <String, Integer, JSONObject> {

        // Variables for Class

        /**
         * Constructor. Takes in the Calling Activity. This allows access to Calling
         * Activity UI.
         */
        PatientPersonalInfo(){
        }

        @Override
        protected JSONObject doInBackground(String... strings) {
            // Variables Declared
            HttpClient httpclient = createHttpClient();
            HttpPost httppost = new HttpPost(PATIENT_PERSONAL_INFO_URL);
            String serverResponse;
            JSONObject serverJSON = null;

            try {
                // Add your data
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
                nameValuePairs.add(new BasicNameValuePair("userid", strings[0]));
                httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

                // Execute HTTP Post Request
                HttpResponse response = httpclient.execute(httppost);

                // Convert Server Response to String
                serverResponse = inputStreamToString(response.getEntity().getContent());

                serverJSON = new JSONObject(serverResponse);

            } catch (Exception e) {
                e.printStackTrace();
            }

            System.out.println(serverJSON);

            // Return Server Response
            return serverJSON;
        }
    }

    public static class UpdatePatInfo extends AsyncTask <String, Integer, JSONObject> {

        // Variables for Class

        /**
         * Constructor. Takes in the Calling Activity. This allows access to Calling
         * Activity UI.
         */
        UpdatePatInfo(){
        }

        @Override
        protected JSONObject doInBackground(String... strings) {
            // Variables Declared
            HttpClient httpclient = createHttpClient();
            HttpPost httppost = new HttpPost(UPDATE_PAT_INFO_URL);
            String serverResponse;
            JSONObject serverJSON = null;

            try {
                // Add your data
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
                nameValuePairs.add(new BasicNameValuePair("userid", strings[0]));
                nameValuePairs.add(new BasicNameValuePair("address", strings[1]));
                nameValuePairs.add(new BasicNameValuePair("city", strings[2]));
                nameValuePairs.add(new BasicNameValuePair("province", strings[3]));
                nameValuePairs.add(new BasicNameValuePair("postalCode", strings[4]));
                nameValuePairs.add(new BasicNameValuePair("phoneNumber", strings[5]));
                httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

                // Execute HTTP Post Request
                HttpResponse response = httpclient.execute(httppost);

                // Convert Server Response to String
                serverResponse = inputStreamToString(response.getEntity().getContent());

                serverJSON = new JSONObject(serverResponse);

            } catch (Exception e) {
                e.printStackTrace();
            }

            // Return Server Response
            return serverJSON;
        }
    }

    /**
     * Creates a new Asynchronous Task to Retrieve Doctor Name List
     */
    public static class PatientListServer extends AsyncTask <String, Integer, JSONObject> {

        /**
         * Constructor. Takes in the Calling Activity. This allows access to Calling
         * Activity UI.
         */
        PatientListServer(){
            super();
        }

        @Override
        protected JSONObject doInBackground(String... strings) {
            // Variables Declared
            HttpClient httpclient = createHttpClient();
            HttpPost httppost = new HttpPost(PATIENT_LIST_URL);
            String serverResponse;
            JSONObject serverJSON = null;

            try {
                // Add your data
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
                nameValuePairs.add(new BasicNameValuePair("username", strings[0]));
                httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

                // Execute HTTP Post Request
                HttpResponse response = httpclient.execute(httppost);

                // Convert Server Response to String
                serverResponse = inputStreamToString(response.getEntity().getContent());

                serverJSON = new JSONObject(serverResponse);

            } catch (Exception e) {
                e.printStackTrace();
            }

            // Return Server Response
            return serverJSON;
        }
    }

    public static class DocApptList extends AsyncTask <String, Integer, JSONObject> {

        /**
         * Constructor. Takes in the Calling Activity. This allows access to Calling
         * Activity UI.
         */
        DocApptList(){
        }

        @Override
        protected JSONObject doInBackground(String... strings) {
            // Variables Declared
            HttpClient httpclient = createHttpClient();
            HttpPost httppost = new HttpPost(DOC_APPTS_URL);
            String serverResponse;
            JSONObject serverJSON = null;

            try {
                // Add your data
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
                nameValuePairs.add(new BasicNameValuePair("username", strings[0]));
                httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

                // Execute HTTP Post Request
                HttpResponse response = httpclient.execute(httppost);

                // Convert Server Response to String
                serverResponse = inputStreamToString(response.getEntity().getContent());

                serverJSON = new JSONObject(serverResponse);

            } catch (Exception e) {
                e.printStackTrace();
            }

            // Return Server Response
            return serverJSON;
        }
    }

    public static class Checkin extends AsyncTask <String, Integer, JSONObject> {

        /**
         * Constructor. Takes in the Calling Activity. This allows access to Calling
         * Activity UI.
         */
        Checkin(){
        }

        @Override
        protected JSONObject doInBackground(String... strings) {
            // Variables Declared
            HttpClient httpclient = createHttpClient();
            HttpPost httppost = new HttpPost(CHECKIN_URL);
            String serverResponse;
            JSONObject serverJSON = null;

            try {
                // Add your data
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
                nameValuePairs.add(new BasicNameValuePair("userid", strings[0]));
                httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

                // Execute HTTP Post Request
                HttpResponse response = httpclient.execute(httppost);

                // Convert Server Response to String
                serverResponse = inputStreamToString(response.getEntity().getContent());

                serverJSON = new JSONObject(serverResponse);

            } catch (Exception e) {
                e.printStackTrace();
            }

            // Return Server Response
            return serverJSON;
        }
    }

    public static class UserList extends AsyncTask <String, Integer, JSONObject> {

        /**
         * Constructor. Takes in the Calling Activity. This allows access to Calling
         * Activity UI.
         * @param act
         */
        UserList(){
        }

        @Override
        protected JSONObject doInBackground(String... strings) {
            // Variables Declared
            HttpClient httpclient = createHttpClient();
            HttpPost httppost = new HttpPost(USER_LIST_URL);
            String serverResponse;
            JSONObject serverJSON = null;

            try {
                // Add your data
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
                httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

                // Execute HTTP Post Request
                HttpResponse response = httpclient.execute(httppost);

                // Convert Server Response to String
                serverResponse = inputStreamToString(response.getEntity().getContent());

                serverJSON = new JSONObject(serverResponse);

            } catch (Exception e) {
                e.printStackTrace();
            }

            // Return Server Response
            return serverJSON;
        }
    }

    public static class RemoveUser extends AsyncTask <String, Integer, JSONObject> {

        /**
         * Constructor. Takes in the Calling Activity. This allows access to Calling
         * Activity UI.
         * @param act
         */
        RemoveUser(){
        }

        @Override
        protected JSONObject doInBackground(String... strings) {
            // Variables Declared
            HttpClient httpclient = createHttpClient();
            HttpPost httppost = new HttpPost(REMOVE_USER_URL);
            String serverResponse;
            JSONObject serverJSON = null;

            try {
                // Add your data
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
                nameValuePairs.add(new BasicNameValuePair("userid", strings[0]));
                nameValuePairs.add(new BasicNameValuePair("removeid", strings[1]));
                httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

                // Execute HTTP Post Request
                HttpResponse response = httpclient.execute(httppost);

                // Convert Server Response to String
                serverResponse = inputStreamToString(response.getEntity().getContent());

                serverJSON = new JSONObject(serverResponse);

            } catch (Exception e) {
                e.printStackTrace();
            }

            // Return Server Response
            return serverJSON;
        }
    }

    public static class GetNurseTasks extends AsyncTask <String, Integer, JSONObject> {

        /**
         * Constructor. Takes in the Calling Activity. This allows access to Calling
         * Activity UI.
         * @param act
         */
        GetNurseTasks(){
        }

        @Override
        protected JSONObject doInBackground(String... strings) {
            // Variables Declared
            HttpClient httpclient = createHttpClient();
            HttpPost httppost = new HttpPost(NURSE_TASKS_URL);
            String serverResponse;
            JSONObject serverJSON = null;

            try {
                // Add your data
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
                nameValuePairs.add(new BasicNameValuePair("userid", strings[0]));
                httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

                // Execute HTTP Post Request
                HttpResponse response = httpclient.execute(httppost);

                // Convert Server Response to String
                serverResponse = inputStreamToString(response.getEntity().getContent());

                serverJSON = new JSONObject(serverResponse);

            } catch (Exception e) {
                e.printStackTrace();
            }

            // Return Server Response
            return serverJSON;
        }
    }

    public static class AddNurseTasks extends AsyncTask <String, Integer, JSONObject> {

        /**
         * Constructor. Takes in the Calling Activity. This allows access to Calling
         * Activity UI.
         * @param act
         */
        AddNurseTasks(){
        }

        @Override
        protected JSONObject doInBackground(String... strings) {
            // Variables Declared
            HttpClient httpclient = createHttpClient();
            HttpPost httppost = new HttpPost(ADD_NURSE_TASKS_URL);
            String serverResponse;
            JSONObject serverJSON = null;

            try {
                // Add your data
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
                nameValuePairs.add(new BasicNameValuePair("userid", strings[0]));
                nameValuePairs.add(new BasicNameValuePair("username", strings[1]));
                nameValuePairs.add(new BasicNameValuePair("description", strings[2]));
                httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

                // Execute HTTP Post Request
                HttpResponse response = httpclient.execute(httppost);

                // Convert Server Response to String
                serverResponse = inputStreamToString(response.getEntity().getContent());

                serverJSON = new JSONObject(serverResponse);

            } catch (Exception e) {
                e.printStackTrace();
            }

            // Return Server Response
            return serverJSON;
        }
    }

    public static class AddPatientRecord extends AsyncTask <String, Integer, JSONObject> {

        /**
         * Constructor. Takes in the Calling Activity. This allows access to Calling
         * Activity UI.
         * @param act
         */
        AddPatientRecord(){
        }

        @Override
        protected JSONObject doInBackground(String... strings) {
            // Variables Declared
            HttpClient httpclient = createHttpClient();
            HttpPost httppost = new HttpPost(ADD_PAT_RECORD_URL);
            String serverResponse;
            JSONObject serverJSON = null;

            try {
                // Add your data
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
                nameValuePairs.add(new BasicNameValuePair("docid", strings[0]));
                nameValuePairs.add(new BasicNameValuePair("patid", strings[1]));
                nameValuePairs.add(new BasicNameValuePair("notes", strings[2]));
                nameValuePairs.add(new BasicNameValuePair("weight", strings[3]));
                nameValuePairs.add(new BasicNameValuePair("height", strings[4]));
                httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

                // Execute HTTP Post Request
                HttpResponse response = httpclient.execute(httppost);

                // Convert Server Response to String
                serverResponse = inputStreamToString(response.getEntity().getContent());

                serverJSON = new JSONObject(serverResponse);

            } catch (Exception e) {
                e.printStackTrace();
            }

            // Return Server Response
            return serverJSON;
        }
    }

    public static class GetPatientRecord extends AsyncTask <String, Integer, JSONObject> {

        /**
         * Constructor. Takes in the Calling Activity. This allows access to Calling
         * Activity UI.
         * @param act
         */
        GetPatientRecord(){
        }

        @Override
        protected JSONObject doInBackground(String... strings) {
            // Variables Declared
            HttpClient httpclient = createHttpClient();
            HttpPost httppost = new HttpPost(GET_PAT_RECORD_URL);
            String serverResponse;
            JSONObject serverJSON = null;

            try {
                // Add your data
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
                nameValuePairs.add(new BasicNameValuePair("patid", strings[0]));
                httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

                // Execute HTTP Post Request
                HttpResponse response = httpclient.execute(httppost);

                // Convert Server Response to String
                serverResponse = inputStreamToString(response.getEntity().getContent());

                serverJSON = new JSONObject(serverResponse);

            } catch (Exception e) {
                e.printStackTrace();
            }

            // Return Server Response
            return serverJSON;
        }
    }

    public static class RemoveNurseTasks extends AsyncTask <String, Integer, JSONObject> {

        /**
         * Constructor. Takes in the Calling Activity. This allows access to Calling
         * Activity UI.
         * @param act
         */
        RemoveNurseTasks(){
        }

        @Override
        protected JSONObject doInBackground(String... strings) {
            // Variables Declared
            HttpClient httpclient = createHttpClient();
            HttpPost httppost = new HttpPost(REMOVE_NURSE_TASKS_URL);
            String serverResponse;
            JSONObject serverJSON = null;

            try {
                // Add your data
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
                nameValuePairs.add(new BasicNameValuePair("removekeys", strings[0]));
                httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

                // Execute HTTP Post Request
                HttpResponse response = httpclient.execute(httppost);

                // Convert Server Response to String
                serverResponse = inputStreamToString(response.getEntity().getContent());

                serverJSON = new JSONObject(serverResponse);

            } catch (Exception e) {
                e.printStackTrace();
            }

            // Return Server Response
            return serverJSON;
        }
    }

    public static class GraphData extends AsyncTask <String, Integer, JSONObject> {

        /**
         * Constructor. Takes in the Calling Activity. This allows access to Calling
         * Activity UI.
         * @param act
         */
        GraphData(){
        }

        @Override
        protected JSONObject doInBackground(String... strings) {
            // Variables Declared
            HttpClient httpclient = createHttpClient();
            HttpPost httppost = new HttpPost(GRAPH_DATA_URL);
            String serverResponse;
            JSONObject serverJSON = null;

            try {
                // Add your data
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
                nameValuePairs.add(new BasicNameValuePair("patid", strings[0]));
                httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

                // Execute HTTP Post Request
                HttpResponse response = httpclient.execute(httppost);

                // Convert Server Response to String
                serverResponse = inputStreamToString(response.getEntity().getContent());

                serverJSON = new JSONObject(serverResponse);

            } catch (Exception e) {
                e.printStackTrace();
            }

            // Return Server Response
            return serverJSON;
        }
    }

    public static class AddAppointment extends AsyncTask <String, Integer, JSONObject> {

        /**
         * Constructor. Takes in the Calling Activity. This allows access to Calling
         * Activity UI.
         * @param act
         */
        AddAppointment(){
        }

        @Override
        protected JSONObject doInBackground(String... strings) {
            // Variables Declared
            HttpClient httpclient = createHttpClient();
            HttpPost httppost = new HttpPost(ADD_APPT_URL);
            String serverResponse;
            JSONObject serverJSON = null;

            try {
                // Add your data
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
                nameValuePairs.add(new BasicNameValuePair("userid", strings[0]));
                nameValuePairs.add(new BasicNameValuePair("year", strings[1]));
                nameValuePairs.add(new BasicNameValuePair("month", strings[2]));
                nameValuePairs.add(new BasicNameValuePair("day", strings[3]));
                nameValuePairs.add(new BasicNameValuePair("hour", strings[4]));
                nameValuePairs.add(new BasicNameValuePair("minute", strings[5]));
                nameValuePairs.add(new BasicNameValuePair("patientKey", strings[6]));
                nameValuePairs.add(new BasicNameValuePair("doctorKey", strings[7]));
                nameValuePairs.add(new BasicNameValuePair("duration", strings[8]));
                httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

                // Execute HTTP Post Request
                HttpResponse response = httpclient.execute(httppost);

                // Convert Server Response to String
                serverResponse = inputStreamToString(response.getEntity().getContent());

                serverJSON = new JSONObject(serverResponse);

            } catch (Exception e) {
                e.printStackTrace();
            }

            // Return Server Response
            return serverJSON;
        }
    }


    /**
     * Creates a new Asynchronous Task to Retrieve Doctor Name List
     */
    public static class PatientApptList extends AsyncTask <String, Integer, JSONObject> {

        /**
         * Constructor. Takes in the Calling Activity. This allows access to Calling
         * Activity UI.
         * @param act
         */
        PatientApptList(){
            super();
        }

        @Override
        protected JSONObject doInBackground(String... strings) {
            // Variables Declared
            HttpClient httpclient = createHttpClient();
            HttpPost httppost = new HttpPost(PATIENT_APPT_LIST_URL);
            String serverResponse;
            JSONObject serverJSON = null;

            try {
                // Add your data
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
                nameValuePairs.add(new BasicNameValuePair("userid", strings[0]));
                httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

                // Execute HTTP Post Request
                HttpResponse response = httpclient.execute(httppost);

                // Convert Server Response to String
                serverResponse = inputStreamToString(response.getEntity().getContent());

                serverJSON = new JSONObject(serverResponse);

            } catch (Exception e) {
                e.printStackTrace();
            }

            // Return Server Response
            return serverJSON;
        }
    }

    /**
     * Creates a new Asynchronous Task to Log User into the Server
     */
    public static class LoginToServer extends AsyncTask <String, Integer, JSONObject> {

        // Variables for Class
        private final Activity callerActivity;
        private ProgressBar pbar;

        /**
         * Constructor. Takes in the Calling Activity. This allows access to Calling
         * Activity UI.
         * @param act
         */
        LoginToServer(Activity act){
            super();
            this.callerActivity = act;
            pbar = (ProgressBar) callerActivity.findViewById(R.id.progressBar1);
            System.out.println ("CREATED SUCCESSFULLY");
        }

        @Override
        protected void onPreExecute() {
            // Making Progress Bar Visible
            pbar.setVisibility(View.VISIBLE);
        }

        @Override
        protected JSONObject doInBackground(String... strings) {
            // Variables Declared
            HttpClient httpclient = createHttpClient();
            HttpPost httppost = new HttpPost(LOGIN_URL);
            String serverResponse;
            JSONObject serverJSON = null;

            try {
                // Add your data
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
                nameValuePairs.add(new BasicNameValuePair("user", strings[0]));
                nameValuePairs.add(new BasicNameValuePair("pass", strings[1]));
                httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

                // Execute HTTP Post Request
                HttpResponse response = httpclient.execute(httppost);

                // Convert Server Response to String
                serverResponse = inputStreamToString(response.getEntity().getContent());

                serverJSON = new JSONObject(serverResponse);

            } catch (Exception e) {
                e.printStackTrace();
            }

            // Return Server Response
            return serverJSON;
        }

        @Override
        protected void onProgressUpdate(Integer... progress){
            // Updating Progress Bar
            pbar.setProgress(progress[0]);
        }

        @Override
        protected void onPostExecute(JSONObject result){
            // Hiding Prorgess Bar
            pbar.setVisibility(View.GONE);
        }
    }
}