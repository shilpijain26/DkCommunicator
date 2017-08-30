package com.dk.dkcommunicator;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;

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
import com.dk.dkcommunicator.gcm.GCMRegistrationIntentService;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener{

    SharedData shareData;

    private String token = "";
    private static final int REQUEST_READ_PHONE_STATE = 200;
    String mobileNumber = "";
    public static String intentFilterStr = "token.google.cloud.messaging";
    private IntentFilter intentFilter;
    BroadcastReceiver mRegistrationBroadcastReceiver;

    Button signBtn;
    EditText mobileTxt;
    Snackbar snackBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_layout);

        shareData = ApplicationClass.get().getSharePref();

        mobileTxt = (EditText)findViewById(R.id.phoneText);

        signBtn = (Button)findViewById(R.id.signBtn);
        signBtn.setEnabled(false);
        signBtn.setOnClickListener(this);

        if (CheckInternetConnection.isNetworkAvailable(LoginActivity.this)){

            Intent i = new Intent(LoginActivity.this, GCMRegistrationIntentService.class);
            startService(i);
            createSnackBar("Fetching Token");

        }else{
            showSnackNoIntetnet();
        }

        intentFilter = new IntentFilter();
        intentFilter.addAction(LoginActivity.intentFilterStr);

        if (Build.VERSION.SDK_INT >= 23) {
            int permissionCheck = ActivityCompat.checkSelfPermission(this, android.Manifest.permission.READ_PHONE_STATE);
            if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.READ_PHONE_STATE}, REQUEST_READ_PHONE_STATE);
            } else {
                //TODO
                getMobileNumber();
            }
        }else{
            getMobileNumber();
        }

        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals(GCMRegistrationIntentService.REGISTRATION_ERROR)) {
                    createSnackBar("Could Get Token. Please try again later");
                } else {
                    token = intent.getStringExtra("token");
                    signBtn.setEnabled(true);
                    snackBar.dismiss();
                }
            }
        };

        Bundle bundle = getIntent().getExtras();
        if(bundle != null && bundle.containsKey("Show Snack")){
            createSnackBar("You have changed your mobile number. Please login again.");
        }

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

    private void setMobile(){

        if(mobileNumber != null){
            mobileTxt.setText(mobileNumber);
        }
    }

    private void getMobileNumber(){
        TelephonyManager tm = (TelephonyManager)getSystemService(TELEPHONY_SERVICE);
        mobileNumber = tm.getLine1Number();
        setMobile();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_READ_PHONE_STATE:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getMobileNumber();
                } else {
                    finish();
                    System.exit(0);
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(mRegistrationBroadcastReceiver, intentFilter);
    }

    private void loginProcess() {
        try {
            RequestQueue requestQueue1 = Volley.newRequestQueue(this);
            String loginUrl = Config.url+"api/auth";
            final JSONObject jsonBody = new JSONObject();
            jsonBody.put("authPassword", "PassRemoteLoginN");
            jsonBody.put("action", "login");
            jsonBody.put("authUsername", "UserRemoteLoginN");
            jsonBody.put("sender_mobile_number", mobileTxt.getText());
            jsonBody.put("token", token);


            final String mRequestBody = jsonBody.toString();

            StringRequest stringRequest = new StringRequest(Request.Method.POST, loginUrl, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    snackBar.dismiss();
                    Log.d("responseMsgReceive", response);

                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        String status = jsonObject.getString("status");
                        if(status.equals("1")){
                            snackBar.dismiss();
                            shareData.AddData("mobile", mobileTxt.getText().toString());
                            shareData.AddData("token", token);
                            Intent intent = new Intent(LoginActivity.this, OTP_Activity.class);
                            startActivity(intent);
                            LoginActivity.this.finish();
                        }else{
                            createSnackBar(jsonObject.getString("message"));
                        }
                    } catch (JSONException e) {
                        Log.d("exceptioncau", "Inside Exception");
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                    //get status code here
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
            int socketTimeout = 30000; //30 seconds - change to what you want
            RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
            stringRequest.setRetryPolicy(policy);
            requestQueue1.add(stringRequest);
        } catch (JSONException e) {
            e.printStackTrace();
        }


    }

    @Override
    public void onClick(View v) {

        boolean showSnack = false;
        if(mobileTxt.getText().toString().isEmpty()){
            createSnackBar("Please fill mobile number");
            showSnack = true;
        }

        if(mobileTxt.getText().toString().length() < 10){
            createSnackBar("Please Enter Valid Mobile Number");
            showSnack = true;
        }

        if(showSnack){
            return;
        }

        if(CheckInternetConnection.isNetworkAvailable(this)){
            createSnackBar("Please wait. Working on request");
            loginProcess();
        }else{
            showSnackNoIntetnet();
        }


    }

    private void showSnackNoIntetnet(){
        createSnackBar("No Internet Connection");
        View sbView = snackBar.getView();
        TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
        textView.setTextColor(Color.parseColor("#2EACD8"));
    }
}
