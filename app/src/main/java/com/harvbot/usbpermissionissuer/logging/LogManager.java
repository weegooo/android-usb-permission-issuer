package com.harvbot.usbpermissionissuer.logging;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Oleg on 4/22/2017.
 */
public class LogManager {

    private Context context;

    private List<ILogger> loggers;

    public LogManager(Context context)
    {
        this.context = context;

        this.loggers = new ArrayList<ILogger>();
        this.loggers.add(new ToastLogger(context));
        this.loggers.add(new FileLogger(context));
    }

    public void log(String message) {
        for (int i = 0; i < this.loggers.size(); i++)
        {
            this.loggers.get(i).log(message);
        }
    }
}
