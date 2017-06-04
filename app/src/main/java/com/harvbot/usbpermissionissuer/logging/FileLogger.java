package com.harvbot.usbpermissionissuer.logging;

import android.content.Context;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class FileLogger implements ILogger {

    private Context context;

    public FileLogger(Context context)
    {
        this.context = context;
        this.cleanupLog();
    }

    @Override
    public void log(String text)
    {
        File logFile = getLogFile();
        if (!logFile.exists())
        {
            try
            {
                logFile.createNewFile();
            }
            catch (IOException e)
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        try
        {
            //BufferedWriter for performance, true to set append to file flag
            BufferedWriter buf = new BufferedWriter(new FileWriter(logFile, true));
            buf.append(text);
            buf.newLine();
            buf.close();
        }
        catch (IOException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private void cleanupLog()
    {
        try {
            File logFile = getLogFile();
            if (logFile.exists()) {
                logFile.delete();
            }
        }catch(Exception e) {
            e.printStackTrace();
        }
    }

    private File getLogFile()
    {
        return new File(context.getFilesDir(), "log.txt");
    }
}
