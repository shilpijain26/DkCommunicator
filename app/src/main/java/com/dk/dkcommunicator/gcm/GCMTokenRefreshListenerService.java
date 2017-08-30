package com.dk.dkcommunicator.gcm;

import android.content.Intent;

import com.google.android.gms.iid.InstanceIDListenerService;

/**
 * Created by Sushant Chauhan on 10/12/2016.
 */
public class GCMTokenRefreshListenerService extends InstanceIDListenerService {

    public void onTokenRefresh(){
        Intent intent=new Intent(this,GCMRegistrationIntentService.class);
        startService(intent);
    }
}
