package com.muv.technicalplan;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class Internet
{
    public Internet() {
    }

    public boolean isOnline(Context context)
    {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        boolean online = false;
        if (netInfo != null && netInfo.isConnectedOrConnecting())
        {
            online = true;
        }
        return online;
    }
}
