package com.dk.dkcommunicator;

import android.app.Activity;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.telephony.SmsManager;
import android.util.Log;

import java.io.UnsupportedEncodingException;

import java.util.Calendar;

/**
 * Created by dell on 8/17/2017.
 */

import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


/**
 * Created by Sushant Chauhan on 8/2/2017.
 */

public class SmsService extends Service {

    private static final String TAG = "LocationService---";

    AlarmManager alarm;
    PendingIntent pintent;
    SharedData sharedData;
    String mobileN ;



    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "StartService");
        sharedData = new SharedData(this);
        mobileN = sharedData.getValue("mobile");
        Log.d("mobileN",mobileN);

        postMessage();
        return START_STICKY; // or whatever your flag
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void postMessage() {

        try {

            RequestQueue requestQueue = Volley.newRequestQueue(this);
            String loginUrl = "http://demoavailable.com/communicator/api/auth";
            final JSONObject jsonBody = new JSONObject();
            jsonBody.put("authPassword", "PassRemoteLoginN");
            jsonBody.put("action", "mobileNumberList");
            jsonBody.put("authUsername", "UserRemoteLoginN");
            jsonBody.put("sender_mobile_number", mobileN);
            final String mRequestBody = jsonBody.toString();
            Log.d("mRequestBody", jsonBody.toString());

            StringRequest stringRequest = new StringRequest(Request.Method.POST, loginUrl, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    Log.d("mRequestResponse", response);

                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        JSONArray jsonArray = jsonObject.getJSONArray("list");
                        Log.d("jsonArray", String.valueOf(jsonArray.length()));

                        if (jsonArray.length() != 0) {

                            Log.d("jsonIf", "jsonIf");

                            for (int i = 0; i < jsonArray.length(); i++) {


                                JSONObject jsonObject1 = jsonArray.getJSONObject(i);

                                String m_number = jsonObject1.getString("m_number");

                                String nameS = jsonObject1.getString("name");

                                String device_types = jsonObject1.getString("divice_type");

                                String message = jsonObject1.getString("message");

                                String id = jsonObject1.getString("id");

                                sendSms(m_number, message, nameS, device_types, id);

                            }
                        } else {

                            Toast.makeText(SmsService.this, "No Message found", Toast.LENGTH_SHORT).show();
                        }

                    } catch (JSONException e) {

                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.d("mRequestVolley", error.toString());
                }
            }) {
                @Override
                public String getBodyContentType() {
                    return "application/json; charset=utf-8";
                }

                @Override
                public byte[] getBody() throws AuthFailureError {
                    try {
                        Log.d("AuthFailureError", "AuthFailureError");
                        return mRequestBody == null ? null : mRequestBody.getBytes("utf-8");
                    } catch (UnsupportedEncodingException uee) {
                        VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s", mRequestBody, "utf-8");
                        return null;
                    }
                }

            };
            int socketTimeout = 30000; //30 seconds - change to what you want
            RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
            stringRequest.setRetryPolicy(policy);
            requestQueue.add(stringRequest);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void sendSms(final String edt_phoneNo, final String et_message, final String nameS, final String deviceType, final String id) {

        Log.d("edt_phoneNo", edt_phoneNo);
        Log.d("edt_message", et_message);
        Log.d("edt_name", nameS);
        Log.d("edt_device", deviceType);
        Log.d("edt_id", id);

        String SMS_SENT = "SMS_SENT";
        String SMS_DELIVERED = "SMS_DELIVERED";

        PendingIntent sentPendingIntent = PendingIntent.getBroadcast(this, 0, new Intent(SMS_SENT), 0);
        PendingIntent deliveredPendingIntent = PendingIntent.getBroadcast(this, 0, new Intent(SMS_DELIVERED), 0);

// For when the SMS has been sent
        registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                switch (getResultCode()) {
                    case Activity.RESULT_OK:
                        Toast.makeText(context, "SMS sent successfully", Toast.LENGTH_SHORT).show();
                        break;
                    case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                        Toast.makeText(context, "Generic failure cause", Toast.LENGTH_SHORT).show();
                        break;
                    case SmsManager.RESULT_ERROR_NO_SERVICE:
                        Toast.makeText(context, "Service is currently unavailable", Toast.LENGTH_SHORT).show();
                        break;
                    case SmsManager.RESULT_ERROR_NULL_PDU:
                        Toast.makeText(context, "No pdu provided", Toast.LENGTH_SHORT).show();
                        break;
                    case SmsManager.RESULT_ERROR_RADIO_OFF:
                        Toast.makeText(context, "Radio was explicitly turned off", Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        }, new IntentFilter(SMS_SENT));

// For when the SMS has been delivered
        registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                switch (getResultCode()) {
                    case Activity.RESULT_OK:
                        Toast.makeText(getBaseContext(), "SMS delivered", Toast.LENGTH_SHORT).show();
                        break;
                    case Activity.RESULT_CANCELED:
                        Toast.makeText(getBaseContext(), "SMS not delivered", Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        }, new IntentFilter(SMS_DELIVERED));

// Get the default instance of SmsManager
        SmsManager smsManager = SmsManager.getDefault();
// Send a text based SMS
        smsManager.sendTextMessage(edt_phoneNo, null, et_message, sentPendingIntent, deliveredPendingIntent);

        saveMessage(edt_phoneNo, et_message, nameS, deviceType, id);

    }

    private void saveMessage(String edt_phoneNo, String et_message, String NameS1, String deviceTypeS, String id) {

        Log.d("edt_phoneNo1", edt_phoneNo);
        Log.d("et_message1", et_message);
        Log.d("et_NameS1", NameS1);
        Log.d("et_deviceTypeS", deviceTypeS);
        Log.d("et_id", id);

        try {

            RequestQueue requestQueue = Volley.newRequestQueue(this);
            String loginUrl = "http://demoavailable.com/communicator/api/auth";
            final JSONObject jsonBody = new JSONObject();
            jsonBody.put("authPassword", "PassRemoteLoginN");
            jsonBody.put("action", "messagesave");
            jsonBody.put("authUsername", "UserRemoteLoginN");
            jsonBody.put("mobile_number", edt_phoneNo);
            jsonBody.put("mobile_message", et_message);
            jsonBody.put("imei_no", "353324063496209");
            jsonBody.put("name", NameS1);
            jsonBody.put("divice_type", deviceTypeS);
            jsonBody.put("sender_mobile_number",mobileN);
            jsonBody.put("ref_id", id);
            final String mRequestBody = jsonBody.toString();
            Log.d("requestMsgSave", jsonBody.toString());

            StringRequest stringRequest = new StringRequest(Request.Method.POST, loginUrl, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    Log.d("responseMsgSave", response);

                    try {
                        JSONObject jsonObject = new JSONObject(response);

                        String status = jsonObject.getString("status");

                        String message = jsonObject.getString("message");


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.d("mRequestVolley", error.toString());
                }
            }) {
                @Override
                public String getBodyContentType() {
                    return "application/json; charset=utf-8";
                }

                @Override
                public byte[] getBody() throws AuthFailureError {
                    try {
                        Log.d("AuthFailureError", "AuthFailureError");
                        return mRequestBody == null ? null : mRequestBody.getBytes("utf-8");
                    } catch (UnsupportedEncodingException uee) {
                        VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s", mRequestBody, "utf-8");
                        return null;
                    }
                }

            };
            int socketTimeout = 30000; //30 seconds - change to what you want
            RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
            stringRequest.setRetryPolicy(policy);
            requestQueue.add(stringRequest);
        } catch (JSONException e) {
            e.printStackTrace();
        }


    }

    @Override
    public void onDestroy() {
        Toast.makeText(this, "MyService Stopped", Toast.LENGTH_LONG).show();
        super.onDestroy();
        alarm.cancel(pintent);

    }

}