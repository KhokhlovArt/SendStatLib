package com.mks.sendstatlib.SharedPreferencesServicer;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.Signature;

import com.mks.sendstatlib.CryptoProvider.SimpleBLOWFISHCryptoProvider;
import com.mks.sendstatlib.Logger.Logger;

import java.util.ArrayList;
import java.util.List;

public class SharedPreferencesServicer {

    //Метод сохранения в SharedPreferences с кодированием строки
    public static void setPreferences(Context cnt, String session, String key, String value)
    {
        if (value == null){return;}
        SimpleBLOWFISHCryptoProvider cryptoProvider = new SimpleBLOWFISHCryptoProvider();
        try {
            Signature[] sigs = cnt.getPackageManager().getPackageInfo(cnt.getPackageName(), PackageManager.GET_SIGNATURES).signatures;
            if ((sigs.length > 0) && (sigs != null))
            {
                cryptoProvider.setSeed(String.valueOf(sigs[0].hashCode()));
            }
        } catch (PackageManager.NameNotFoundException e) {
            Logger.log("" + e.getMessage());
        }

        String codeText = cryptoProvider.cript(value);

//        String codeText = CryptoProviderServicer.cript(value);
        cnt.getSharedPreferences(session, Context.MODE_PRIVATE).edit().putString(key, codeText).apply();
    }

    //Метод чтения из SharedPreferences с раскодированием строки
    public static String getPreferences(Context cnt, String session, String key, String default_res)
    {
        String res = cnt.getSharedPreferences(session, Context.MODE_PRIVATE).getString(key, default_res);
        if (res == null){return null;}
        String decodeText = null;
        SimpleBLOWFISHCryptoProvider cryptoProvider = new SimpleBLOWFISHCryptoProvider();
        try {
            Signature[] sigs = cnt.getPackageManager().getPackageInfo(cnt.getPackageName(), PackageManager.GET_SIGNATURES).signatures;
            if ((sigs.length > 0) && (sigs != null))
            {
                cryptoProvider.setSeed(String.valueOf(sigs[0].hashCode()));
            }
        } catch (PackageManager.NameNotFoundException e) {
            Logger.log("" + e.getMessage());
        }
        decodeText = cryptoProvider.deCript(res);
//        String decodeText = CryptoProviderServicer.deCript(res);
        return decodeText;
    }

    //Метод сохранения в SharedPreferences без кодированием строки
    public static void setSimplePreferences(Context cnt, String session, String key, String value)
    {
        if (value == null){return;}
        cnt.getSharedPreferences(session, Context.MODE_PRIVATE).edit().putString(key, value).apply();
    }

    //Метод чтения из SharedPreferences без раскодированием строки
    public static String getSimplePreferences(Context cnt, String session, String key, String default_res)
    {
        String res = cnt.getSharedPreferences(session, Context.MODE_PRIVATE).getString(key, default_res);
        if (res == null){return null;}
        return res;
    }


    public static void setIntSimplePreferences(Context cnt, String session, String key, int value)
    {
        cnt.getSharedPreferences(session, Context.MODE_PRIVATE).edit().putInt(key, value).apply();
    }

    public static int getIntSimplePreferences(Context cnt, String session, String key, int default_res)
    {
        int res = cnt.getSharedPreferences(session, Context.MODE_PRIVATE).getInt(key, default_res);
        return res;
    }



    public static void setSharedPreferenceStringList(Context cnt, String session, String pKey, List<String> pData) {
        SharedPreferences.Editor editor = cnt.getSharedPreferences(session, cnt.MODE_PRIVATE).edit();
        editor.putInt(pKey + "size", pData.size());
        editor.commit();

        for (int i = 0; i < pData.size(); i++) {
            SharedPreferences.Editor editor1 = cnt.getSharedPreferences(session, cnt.MODE_PRIVATE).edit();
            editor1.putString(pKey + i, (pData.get(i)));
            editor1.commit();
        }
    }

    public static List<String> getSharedPreferenceStringList(Context cnt, String session, String pKey) {
        int size = cnt.getSharedPreferences(session, cnt.MODE_PRIVATE).getInt(pKey + "size", 0);
        List<String> list = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            list.add(cnt.getSharedPreferences(session, cnt.MODE_PRIVATE).getString(pKey + i, ""));
        }
        return list;
    }
}
