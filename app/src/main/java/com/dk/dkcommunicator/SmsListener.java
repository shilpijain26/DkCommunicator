package com.dk.dkcommunicator;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

/**
 * Created by apple on 28/08/17.
 */

public class SmsListener extends BroadcastReceiver {
    SharedData sharedData;
    String mobileN;

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("receivercalled", "Called Message Receiver");
        sharedData = new SharedData(context);
        mobileN = sharedData.getValue("mobile");
        Log.d("mobileN",mobileN);
        final Bundle bundle = intent.getExtras();

        try {

            if (bundle != null) {

                final Object[] pdusObj = (Object[]) bundle.get("pdus");

                String smsMessageStr = "";
                String smsBody = "";
                String address = "";
                String address1 = "";

                for (int i = 0; i < pdusObj.length; i++) {

                    SmsMessage currentMessage = SmsMessage.createFromPdu((byte[]) pdusObj[i]);
                    String phoneNumber = currentMessage.getDisplayOriginatingAddress();

                    address = phoneNumber;
                    smsBody = currentMessage.getDisplayMessageBody();
                    address1 = address.substring(3);

                    Log.d("MReceived", "phoneNumber: "+ phoneNumber + ", message: " + smsBody);
                    // Show Alert
                    int duration = Toast.LENGTH_LONG;
//                    Toast toast = Toast.makeText(context,
//                            "senderNum: "+ address1 + ", message: " + smsBody, duration);
//                    toast.show();

                    updateList(smsBody, address1, context);


                } // end for loop
            } // bundle is null

        } catch (Exception e) {
            Log.e("SmsReceiver", "Exception smsReceiver" +e);

        }
    }

    private void updateList(final String smsMessage, String address, Context context) {

        Log.d("smsMessage", smsMessage);
        Log.d("smsAddress", address);

        try {

            RequestQueue requestQueue1 = Volley.newRequestQueue(context);
            String loginUrl = "http://demoavailable.com/communicator/api/auth";
            final JSONObject jsonBody = new JSONObject();
            jsonBody.put("authPassword", "PassRemoteLoginN");
            jsonBody.put("action", "messagesave");
            jsonBody.put("authUsername", "UserRemoteLoginN");
            jsonBody.put("sender_mobile_number", address);
            jsonBody.put("mobile_number", mobileN);
            jsonBody.put("mobile_message", smsMessage);
            jsonBody.put("imei_no", "353324063496209");
            jsonBody.put("name", "NA");
            jsonBody.put("divice_type", "NA");
            jsonBody.put("ref_id","NA");

            final String mRequestBody = jsonBody.toString();
            Log.d("requestMsgReceive", jsonBody.toString());

            StringRequest stringRequest = new StringRequest(Request.Method.POST, loginUrl, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    Log.d("responseMsgReceive", "responseMsgReceive");
                    Log.d("responseMsgReceive", response);


                    try {
                        JSONObject jsonObject = new JSONObject(response);

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
            requestQueue1.add(stringRequest);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
