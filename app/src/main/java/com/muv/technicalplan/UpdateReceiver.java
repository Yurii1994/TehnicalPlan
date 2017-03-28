package com.muv.technicalplan;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class UpdateReceiver extends BroadcastReceiver
{
    @Override
    public void onReceive(Context context, Intent intent)
    {
        context.sendBroadcast(new Intent(intent));
    }
}
