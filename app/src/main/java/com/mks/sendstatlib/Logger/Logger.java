package com.mks.sendstatlib.Logger;

import android.util.Log;

import com.mks.sendstatlib.C;

public class Logger {
    static String TAG = "SendStatLib";
    static public void log(String msg) {
        if (C.NEED_LOG)
        {
            Log.d(TAG,"v " + C.CODE_VERSION + ": " + msg);
        }
    }
}
