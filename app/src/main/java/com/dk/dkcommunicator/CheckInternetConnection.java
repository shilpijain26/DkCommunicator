package com.dk.dkcommunicator;

import android.content.Context;
import android.net.ConnectivityManager;
import android.widget.Toast;

/**
 * Created by dell on 8/23/2017.
 */

public class CheckInternetConnection {
    public static boolean isNetworkAvailable(Context context) {
        boolean internetFlag;
        if (((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo() != null) {
            internetFlag = true;
        } else {
            internetFlag = false;
        }
        return internetFlag;
    }
}
