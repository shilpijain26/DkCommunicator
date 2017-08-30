package com.dk.dkcommunicator;

import android.content.Intent;
import android.graphics.Color;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

public class OTP_Activity extends AppCompatActivity implements View.OnClickListener{

    SharedData sharedData;

    Button signBtn;
    TextView resendOTP;
    EditText mobileTxt;
    Snackbar snackBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.otp_layout);

        sharedData = ApplicationClass.get().getSharePref();

        mobileTxt = (EditText)findViewById(R.id.phoneText);

        signBtn = (Button)findViewById(R.id.signBtn);
        resendOTP = (TextView) findViewById(R.id.resendotp);
        resendOTP.setClickable(true);
        resendOTP.setOnClickListener(this);
        signBtn.setOnClickListener(this);

    }

    private void createSnackBar(String msg){
        snackBar = Snackbar.make(findViewById(R.id.main_layout), msg, Snackbar.LENGTH_INDEFINITE).setAction("OK", new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                snackBar.dismiss();
            }
        });
        snackBar.show();
    }



    @Override
    protected void onResume() {
        super.onResume();

    }

    private void resendOTP(){
        try {
            RequestQueue requestQueue1 = Volley.newRequestQueue(this);
            String loginUrl = Config.url+"api/auth";
            final JSONObject jsonBody = new JSONObject();
            jsonBody.put("authPassword", "PassRemoteLoginN");
            jsonBody.put("action", "resendOTP");
            jsonBody.put("authUsername", "UserRemoteLoginN");
            jsonBody.put("sender_mobile_number", sharedData.getValue("mobile"));


            final String mRequestBody = jsonBody.toString();

            StringRequest stringRequest = new StringRequest(Request.Method.POST, loginUrl, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    Log.d("responseMsgRecei", response);
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        String status = jsonObject.getString("status");
                        if(status.equals("1")){
                            snackBar.dismiss();
                        }else{
                            createSnackBar(jsonObject.getString("message"));
                        }
                    } catch (JSONException e) {

                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    if(error.networkResponse == null){
                        if (error.getClass().equals(TimeoutError.class)) {
                            // Show timeout error message
                            createSnackBar("Oops. Connection timeout !!");
                        }
                    }else{
                        String statusCode = String.valueOf(error.networkResponse.statusCode);
                        //get response body and parse with appropriate encoding
                        createSnackBar("Server Error Response: "+statusCode);
                    }
                }
            }) {
                @Override
                public String getBodyContentType() {
                    return "application/json; charset=utf-8";
                }

                @Override
                public byte[] getBody() throws AuthFailureError {
                    try {
                        return mRequestBody == null ? null : mRequestBody.getBytes("utf-8");
                    } catch (UnsupportedEncodingException uee) {
                        VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s", mRequestBody, "utf-8");
                        return null;
                    }
                }

            };
            int socketTimeout = 60000; //30 seconds - change to what you want
            RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
            stringRequest.setRetryPolicy(policy);
            requestQueue1.add(stringRequest);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void verifyOTP() {
        try {
            RequestQueue requestQueue1 = Volley.newRequestQueue(this);
            String loginUrl = Config.url+"api/auth";
            final JSONObject jsonBody = new JSONObject();
            jsonBody.put("authPassword", "PassRemoteLoginN");
            jsonBody.put("action", "validateOTP");
            jsonBody.put("authUsername", "UserRemoteLoginN");
            jsonBody.put("sender_mobile_number", sharedData.getValue("mobile"));
            jsonBody.put("otp", mobileTxt.getText());


            final String mRequestBody = jsonBody.toString();

            StringRequest stringRequest = new StringRequest(Request.Method.POST, loginUrl, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    Log.d("responseMsgRecei", response);
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        String status = jsonObject.getString("status");
                        if(status.equals("1")){
                            snackBar.dismiss();
                            sharedData.AddData("OTP_Verified", "1");
                            Intent intent = new Intent(OTP_Activity.this, MainActivity.class);
                            startActivity(intent);
                            OTP_Activity.this.finish();

                        }else{
                            createSnackBar(jsonObject.getString("message"));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    if(error.networkResponse == null){
                        if (error.getClass().equals(TimeoutError.class)) {
                            // Show timeout error message
                            createSnackBar("Oops. Connection timeout !!");
                        }
                    }else{
                        String statusCode = String.valueOf(error.networkResponse.statusCode);
                        //get response body and parse with appropriate encoding
                        createSnackBar("Server Error Response: "+statusCode);
                    }
                }
            }) {
                @Override
                public String getBodyContentType() {
                    return "application/json; charset=utf-8";
                }

                @Override
                public byte[] getBody() throws AuthFailureError {
                    try {
                        return mRequestBody == null ? null : mRequestBody.getBytes("utf-8");
                    } catch (UnsupportedEncodingException uee) {
                        VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s", mRequestBody, "utf-8");
                        return null;
                    }
                }

            };
            int socketTimeout = 60000; //30 seconds - change to what you want
            RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
            stringRequest.setRetryPolicy(policy);
            requestQueue1.add(stringRequest);
        } catch (JSONException e) {
            e.printStackTrace();
        }


    }

    @Override
    public void onClick(View v) {

        int id = v.getId();

        switch(id){
            case R.id.resendotp:
                if(CheckInternetConnection.isNetworkAvailable(this)){
                    createSnackBar("Please wait. Processing Resend Request");
                    resendOTP();
                }else{
                    showSnackNoIntetnet();
                }
            break;

            case R.id.signBtn:
                boolean showSnack = false;
                if(mobileTxt.getText().toString().isEmpty()){
                    createSnackBar("Please fill OTP");
                    showSnack = true;
                }

                if(showSnack){

                    return;
                }

                if(CheckInternetConnection.isNetworkAvailable(this)){
                    createSnackBar("Please wait. Verifying OTP");
                    verifyOTP();
                }else{
                    showSnackNoIntetnet();
                }
            break;
        }
    }

    private void showSnackNoIntetnet(){
        createSnackBar("No Internet Connection");
        View sbView = snackBar.getView();
        TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
        textView.setTextColor(Color.parseColor("#2EACD8"));
    }
}
