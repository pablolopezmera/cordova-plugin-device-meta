package com.ozexpert.devicemeta;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.wifi.WifiManager;
import android.telephony.TelephonyManager;
import android.os.Build;

import java.util.Formatter;

import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CallbackContext;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.ozexpert.devicemeta.Utils;
import android.provider.Settings;

/**
 * This class echoes a string called from JavaScript.
 */
public class DeviceMeta extends CordovaPlugin {

    private static Context ctx;

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {

        ctx = this.cordova.getActivity().getApplicationContext();

        if (action.equals("getDeviceMeta")) {
            
            JSONObject r = new JSONObject();
            r.put("debug", this.isDebug());
            r.put("networkProvider", this.getNetworkProvider());
            r.put("ip", this.getIpAddress());
            r.put("manufacturer", this.getManufacturer());
            r.put("manufacturer", this.getManufacturer());
            r.put("DEVELOPMENT_SETTINGS_ENABLED", this.isADBModeEnabled());
            r.put("ADB_ENABLED", this.isDevelopmentSettingsEnabled());

            callbackContext.success(r);
        } else {
            return false;
        }
        return true;
    }

    /**
     * checks if ADB mode is on
     * especially for debug mode check
     */
    public boolean isADBModeEnabled(){
        boolean result = false;
        try {
            result = getADBMode() == 1;
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }
        Log.d(TAG, "ADB mode enabled: " + result);
        return result;
    }

    /**
     * get device ADB mode info
     */
    public int getADBMode(){
        int mode;
        if (Build.VERSION.SDK_INT >= 17){ // Jelly_Bean_MR1 and above
            mode = Settings.Global.getInt(ctx.getContentResolver(), Settings.Global.ADB_ENABLED, 0);
        } else { // Pre-Jelly_Bean_MR1
            mode = Settings.Secure.getInt(ctx.getContentResolver(), Settings.Secure.ADB_ENABLED, 0);
        }
        return mode;
    }

    /**
     * get device ADB mode info
     */
    public int getDevelopmentSettingsEnabled(){
        int mode;
        if (Build.VERSION.SDK_INT >= 17){ // Jelly_Bean_MR1 and above
            mode = Settings.Global.getInt(ctx.getContentResolver(), Settings.Global.DEVELOPMENT_SETTINGS_ENABLED, 0);
        } else { // Pre-Jelly_Bean_MR1
            mode = Settings.Secure.getInt(ctx.getContentResolver(), Settings.Secure.DEVELOPMENT_SETTINGS_ENABLED, 0);
        }
        return mode;
    }

    /**
     * checks if ADB mode is on
     * especially for debug mode check
     */
    public boolean isDevelopmentSettingsEnabled(){
        boolean result = false;
        try {
            result = getDevelopmentSettingsEnabled() == 1;
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }
        Log.d(TAG, "ADB mode enabled: " + result);
        return result;
    }

    private boolean isDebug() {
        try {
            if ((ctx.getPackageManager().getPackageInfo(
                ctx.getPackageName(), 0).applicationInfo.flags & 
                ApplicationInfo.FLAG_DEBUGGABLE) != 0) {
                //Debug and development mode
                return true;
            }
        } catch (NameNotFoundException e){
            // do nothing
        }
        return false;
    }

    private String getIpAddress() {
        return Utils.getIPAddress(true);
    }

    private String getNetworkProvider() {
        TelephonyManager tm = (TelephonyManager) ctx.getSystemService(Context.TELEPHONY_SERVICE);
        return tm.getNetworkOperatorName();
    }

    private String getManufacturer() {
        return Build.MANUFACTURER;
    }

    // private void getDeviceMeta(String message, CallbackContext callbackContext) {
    //     if (message != null && message.length() > 0) {
    //         callbackContext.success(message);
    //     } else {
    //         callbackContext.error("Expected one non-empty string argument.");
    //     }
    // }
}
