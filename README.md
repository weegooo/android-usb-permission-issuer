# android-usb-permission-issuer
The application grant access to all Arduino usb devices connected to Android device.

To application must be published to system folder as it should be runned as system service.
Pay attanetion that application must be installed to /system/priv-app/ folder, not system/app.
Otherwise, you will have the error: Neither user 10059 nor current process has android.permission.MANAGE_USB.

The command line example to install application on device:
adb push usb-permission-issuer.apk /system/priv-app/
adb shell chmod 644 /system/priv-app/usb-permission-issuer.apk

Tested on Odroid-XU4 device.