package com.harvbot.usbpermissionissuer.logging;

import android.content.Context;
import android.widget.Toast;

import com.harvbot.usbpermissionissuer.logging.ILogger;

public class ToastLogger implements ILogger {

    private Context context;

    public ToastLogger(Context context)
    {
        this.context = context;
    }

    @Override
    public void log(String message) {
        Toast toast = Toast.makeText(context.getApplicationContext(), message, Toast.LENGTH_LONG);
        toast.show();
    }
}
