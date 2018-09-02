package com.sprojects.atf;

import android.content.Context;

public class ATFUtils
{

    // #####################################################################

    private static SecurePreferences getAppDataInstance(Context context)
    {
        if(context != null) return  new SecurePreferences(context, "app_prefs", "du9Ms9bNdv3A3b1dVce8x3Q0d4c3rlF8", true);
        return null;
    }


    // #####################################################################

    public static void saveAppData(Context context, String k, String v)
    {
        SecurePreferences preferences = getAppDataInstance(context);
        if(preferences != null) preferences.put(k, v);
    }


    // #####################################################################

    public static String getAppData(Context context, String k)
    {
        SecurePreferences preferences = getAppDataInstance(context);
        if(preferences != null) return preferences.getString(k);

        return null;
    }
}
