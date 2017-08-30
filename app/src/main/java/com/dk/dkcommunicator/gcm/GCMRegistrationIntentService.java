package com.dk.dkcommunicator.gcm;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import com.dk.dkcommunicator.LoginActivity;
import com.dk.dkcommunicator.MainActivity;
import com.dk.dkcommunicator.R;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;


/**
 * Created by Sushant Chauhan on 1/4/2017.
 */

public class GCMRegistrationIntentService extends IntentService {

    public static final String REGISTRATION_SUCCESS = "RegistrationSuccess";
    public static final String REGISTRATION_ERROR = "RegistrationError";
    public static final String TAG = "GCMSUCCESS";
    private static final String TAG1 = "GCMRegistrationIntentService";
    //private final IBinder binder = new LocalBinder();
    //public ServiceCallbacks serviceCallbacks;

    public GCMRegistrationIntentService() {
        super("GCMRegistrationIntentService");

    }

    @Override
    protected void onHandleIntent(Intent intent) {
        registerGCM();
    }

    private void registerGCM() {
        SharedPreferences sharedPreferences = getSharedPreferences("GCM", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Intent registrationComplete = null;
        String token = null;
        try {
            InstanceID instanceID = InstanceID.getInstance(getApplicationContext());
            Log.d("SenderID", getString(R.string.gcm_defaultSenderId));
            token = instanceID.getToken(getString(R.string.gcm_defaultSenderId), GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);
            Log.v("GCMRegIntentService", "TOKEN DONE:" + token);
            registrationComplete = new Intent(REGISTRATION_SUCCESS);
            registrationComplete.setAction(LoginActivity.intentFilterStr);
            registrationComplete.putExtra("token", token);
            String oldToken = sharedPreferences.getString(TAG, "");


            //only request to save token when token is new
            if (!"".equals(token) && !oldToken.equals(token)) {
//                saveTokenToServer(token);
                //save new token to shared preference
                editor.putString(TAG, token);
                editor.commit();
               // serviceCallbacks.userLogin(token);


            } else {
                Log.v("GCMRegistrationService", "Old token");
//                if(serviceCallbacks != null){
//                    Log.d("servicecallback", "Not Null");
//                }else{
//                    Log.d("servicecallback", "Null found");
             //   }
               // serviceCallbacks.userLogin(token);
                //serviceCallbacks.doSome(token);

            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.v("GCMRegIntentService", "RegistrationError");
            registrationComplete = new Intent(REGISTRATION_ERROR);
        }


        //send broadcast
            sendBroadcast(registrationComplete);
        //serviceCallbacks.userLogin(token);
    }

}
