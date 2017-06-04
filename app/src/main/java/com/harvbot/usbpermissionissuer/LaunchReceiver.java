package com.harvbot.usbpermissionissuer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.res.XmlResourceParser;
import android.hardware.usb.IUsbManager;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.os.IBinder;
import android.os.ServiceManager;
import android.util.Log;
import android.widget.Toast;

import org.xmlpull.v1.XmlPullParserException;

import com.harvbot.usbpermissionissuer.logging.FileLogger;
import com.harvbot.usbpermissionissuer.logging.LogManager;

public class LaunchReceiver extends BroadcastReceiver
{
    private final String TAG = "com.harvbot.usb";

    private LogManager logger;

    public void onReceive( Context context, Intent intent )
    {
        logger = new LogManager(context);

        String action = intent.getAction();
        if( action != null && action.equals( Intent.ACTION_BOOT_COMPLETED ) )
        {
            logger.log("Boot Completed event processing.");
            try
            {
                PackageManager pm = context.getPackageManager();
                List<ApplicationInfo> apps = pm.getInstalledApplications(PackageManager.GET_META_DATA);

                logger.log("List of installed apps were loaded. Number of apps: "+
                        (new Integer(apps.size())).toString());

                UsbManager manager = (UsbManager) context.getSystemService(Context.USB_SERVICE);
                IBinder b = ServiceManager.getService(Context.USB_SERVICE);
                IUsbManager service = IUsbManager.Stub.asInterface(b);
                HashMap<String, UsbDevice> deviceList = manager.getDeviceList();

                logger.log("List of usb devices was loaded. Number of attached devices: " +
                        new Integer(deviceList.size()).toString());

                List<UsbDeviceDescriptor> filteredDevices = this.getFilteredUsbDevices(context);

                logger.log("List of usb devices descriptors was loadded. Number of devices in filter: " +
                        new Integer(filteredDevices.size()).toString());

                if(filteredDevices.size() > 0) {
                    for (int i = 0; i < apps.size(); i++) {

                        ApplicationInfo ai = apps.get(i);
                        if (ai.packageName.startsWith("com.harvbot.")) {

                            logger.log("The usb permission will be granted for application " + ai.packageName);

                            Iterator<UsbDevice> deviceIterator = deviceList.values().iterator();
                            while (deviceIterator.hasNext()) {
                                UsbDevice device = deviceIterator.next();

                                for (int j = 0; j < filteredDevices.size(); j++) {
                                    if (device.getVendorId() == filteredDevices.get(j).vendorId &&
                                            (device.getProductId() == filteredDevices.get(j).productId || filteredDevices.get(j).productId == 0)) {

                                        try {
                                            service.grantDevicePermission(device, ai.uid);
                                            service.setDevicePackage(device, ai.packageName, ai.uid);

                                            logger.log("Usb permission is granted for application " +
                                                    ai.packageName + " for device " + device.toString() +
                                                    " and user " + ai.uid + " is granted");
                                        }
                                        catch(SecurityException se)
                                        {
                                            logger.log(se.toString());
                                            Log.e(TAG, se.toString());
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
            catch(Exception e)
            {
                logger.log(e.toString());
                Log.e(TAG, e.toString());
            }
        }
    }

    private List<UsbDeviceDescriptor> getFilteredUsbDevices(Context context)
    {
        List<UsbDeviceDescriptor> result = new ArrayList<UsbDeviceDescriptor>();

        int eventType = -1;
        XmlResourceParser document = context.getResources().getXml(R.xml.device_filter);

        while(eventType != XmlResourceParser.END_DOCUMENT)
        {
            String name = document.getText();

            try {
                if (document.getEventType() == XmlResourceParser.START_TAG) {
                    String s = document.getName();

                    if (s.equals("usb-device")) {
                        UsbDeviceDescriptor item = new UsbDeviceDescriptor();
                        item.vendorId = Integer.parseInt(document.getAttributeValue(null, "vendor-id"));

                        try {
                            item.productId = Integer.parseInt(document.getAttributeValue(null, "product-id"));
                        }
                        catch(NumberFormatException nfe)
                        {
                            nfe.printStackTrace();
                        }

                        result.add(item);
                    }
                }

                eventType = document.next();
            } catch (XmlPullParserException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return result;
    }
}