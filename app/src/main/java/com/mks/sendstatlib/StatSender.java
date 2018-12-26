package com.mks.sendstatlib;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import com.mks.sendstatlib.HttpsConnection.HttpsConnectionServicer;
import com.mks.sendstatlib.Logger.Logger;
import com.mks.sendstatlib.SharedPreferencesServicer.SharedPreferencesServicer;
import com.mks.sendstatlib.StatParams.ExternalStatParams;
import com.mks.sendstatlib.StatParams.StatParams;

import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

public class StatSender {
    public String getVersion(Context cnt) {
        Logger.log("StatSender.getVersion()");
        return C.CODE_VERSION;
    }

    public void init(Context cnt, ExternalStatParams extParam) {
        Logger.log("StatSender.init()");
        SharedPreferencesServicer.setPreferences(cnt, C.SPF_SESSION_GLOBAL_PUBLISHERID  , C.SPF_KEY_GLOBAL_PUBLISHERID  , extParam.getPublisherId());
        SharedPreferencesServicer.setPreferences(cnt, C.SPF_SESSION_GLOBAL_EXTERNAL_GAID, C.SPF_KEY_GLOBAL_EXTERNAL_GAID, extParam.getExternalGaid());
        SharedPreferencesServicer.setPreferences(cnt, C.SPF_SESSION_GLOBAL_APPID        , C.SPF_KEY_GLOBAL_APPID        , extParam.getAppid());
        SharedPreferencesServicer.setPreferences(cnt, C.SPF_SESSION_GLOBAL_SDKVERSION   , C.SPF_KEY_GLOBAL_SDKVERSION   , extParam.getSdkversion());
        SharedPreferencesServicer.setPreferences(cnt, C.SPF_SESSION_GLOBAL_PACKAGENAMES , C.SPF_KEY_GLOBAL_PACKAGENAMES , extParam.getPackageNames());
        SharedPreferencesServicer.setPreferences(cnt, C.SPF_SESSION_GLOBAL_FCM_TOKEN    , C.SPF_KEY_GLOBAL_FCM_TOKEN    , extParam.getAppFCMToken());
    }

    public String sendStat(Context cnt, String action, String q) {
        Logger.log("StatSender.sendStat()");
        long time = Calendar.getInstance().getTime().getTime();
        Date date = new java.util.Date(time);
        SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd");
        String query = C.BASE_URL.replace("__DATA__", sdf.format(date));
        String res = "";

        String G_Publisher    = SharedPreferencesServicer.getPreferences(cnt, C.SPF_SESSION_GLOBAL_PUBLISHERID, C.SPF_KEY_GLOBAL_PUBLISHERID, "");
        String G_ExternalGaid = SharedPreferencesServicer.getPreferences(cnt, C.SPF_SESSION_GLOBAL_EXTERNAL_GAID, C.SPF_KEY_GLOBAL_EXTERNAL_GAID, "");
        String G_Appid        = SharedPreferencesServicer.getPreferences(cnt, C.SPF_SESSION_GLOBAL_APPID, C.SPF_KEY_GLOBAL_APPID, "");
        String G_Sdkversion   = SharedPreferencesServicer.getPreferences(cnt, C.SPF_SESSION_GLOBAL_SDKVERSION, C.SPF_KEY_GLOBAL_SDKVERSION, "");
        String FCM_Token      = SharedPreferencesServicer.getPreferences(cnt, C.SPF_SESSION_GLOBAL_FCM_TOKEN, C.SPF_KEY_GLOBAL_FCM_TOKEN, "");

        int count = SharedPreferencesServicer.getIntSimplePreferences(cnt, C.SPF_SESSION_COUNTER + action, C.SPF_KEY_COUNTER + action, 0);

        StatParams p = new StatParams(cnt, action, q);
        String str_jsonObject = "{";
        str_jsonObject += formatJsonParam("action",           p.action) + ",";
        str_jsonObject += formatJsonParam("quary",            p.quary) + ",";
        str_jsonObject += formatJsonParam("internal_gaid",    p.gaid) + ",";
        str_jsonObject += formatJsonParam("external_gaid", "" + G_ExternalGaid) + ",";
        str_jsonObject += formatJsonParam("appVersion",       p.appVersion) + ",";
        str_jsonObject += formatJsonParam("packagename",      p.packagename) + ",";
        str_jsonObject += formatJsonParam("osversion",        p.osversion) + ",";
        str_jsonObject += formatJsonParam("useragent",        p.useragent) + ",";
        str_jsonObject += formatJsonParam("appid",         "" + G_Appid) + ",";
        str_jsonObject += formatJsonParam("sdkversion",    "" + G_Sdkversion) + ",";
        str_jsonObject += formatJsonParam("manufacturer",     p.manufacturer) + ",";
        str_jsonObject += formatJsonParam("model",            p.model) + ",";
        str_jsonObject += formatJsonParam("screenwidth",      p.screenwidth) + ",";
        str_jsonObject += formatJsonParam("screenheight",     p.screenheight) + ",";
        str_jsonObject += formatJsonParam("connectiontype",   p.connectiontype) + ",";
        str_jsonObject += formatJsonParam("publishername", "" + G_Publisher) + ",";
        str_jsonObject += formatJsonParam("IP",               p.IP) + ",";
        str_jsonObject += formatJsonParam("MAC",              p.MAC) + ",";
        str_jsonObject += formatJsonParam("imei1",            p.imei1) + ",";
        str_jsonObject += formatJsonParam("imsi1",            p.imsi1) + ",";
        str_jsonObject += formatJsonParam("imei2",            p.imei2) + ",";
        str_jsonObject += formatJsonParam("imsi2",            p.imsi2) + ",";
        str_jsonObject += formatJsonParam("imei3",            p.imei3) + ",";
        str_jsonObject += formatJsonParam("imsi3",            p.imsi3) + ",";
        str_jsonObject += formatJsonParam("firstInstallTime", p.firstInstallTime) + ",";
        str_jsonObject += formatJsonParam("count",         "" + count) + ",";
        str_jsonObject += formatJsonParam("tag",              p.msgTag) + ",";
        str_jsonObject += formatJsonParam("androidID",        p.androidID) + ",";
        str_jsonObject += formatJsonParam("IntLibVersion", "" + C.CODE_VERSION) + ",";
        str_jsonObject += formatJsonParam("FCM_token",     "" + FCM_Token) + ",";
        str_jsonObject += addExternalAppInfo(cnt);
        str_jsonObject += formatJsonParam("actiontime",       p.actiontime);
        str_jsonObject += "}";
        SharedPreferencesServicer.setIntSimplePreferences(cnt, C.SPF_SESSION_COUNTER + action, C.SPF_KEY_COUNTER + action, ++count);

        //Сохраняем в очередь SP
        List<String> q_list = SharedPreferencesServicer.getSharedPreferenceStringList(cnt, C.SPF_SESSION_LOGS_QUEUE, C.SPF_KEY_LOGS_QUEUE);
        if(q_list == null){q_list= new ArrayList<String>();}
        q_list.add(str_jsonObject);
        SharedPreferencesServicer.setSharedPreferenceStringList(cnt, C.SPF_SESSION_LOGS_QUEUE, C.SPF_KEY_LOGS_QUEUE, q_list);

        q_list = SharedPreferencesServicer.getSharedPreferenceStringList(cnt, C.SPF_SESSION_LOGS_QUEUE, C.SPF_KEY_LOGS_QUEUE);

        for (String str : q_list) {
            //TODO: Если мы получаем хороший ответ от какого либо из отосланных запросов очереди, то этот зхапрос надо из очереди удалить. Иначе если мы накопили 10 запросов
            //и упало на 10-м, то в следующший раз мы снова передадим все 9
            HttpsURLConnection connection = null;

            try {
                connection = new HttpsConnectionServicer().getHttpsConnection(new URL(query));
                if (connection != null) {

                    connection.setRequestMethod("POST");
                    connection.setRequestProperty("Content-Type", "application/json; charset=utf-8");

                    setPostRequestContent(connection, str);
                    connection.connect();
                    //long modified = connection.getLastModified();

                    System.out.println(query + " " + connection.getResponseCode());
                    InputStream in = connection.getInputStream();
                    res = readInputStreamAsString(in);
                    in.close();
                }

            } catch (Exception e) {
                System.out.println(query + " " + e.getMessage());
                return null;
            } finally {
                if (connection != null) connection.disconnect();
            }
        }
        SharedPreferencesServicer.setSharedPreferenceStringList(cnt, C.SPF_SESSION_LOGS_QUEUE, C.SPF_KEY_LOGS_QUEUE, new ArrayList<String>());
        return res;
    }

    private static String addExternalAppInfo(Context cnt)
    {
        String res = "";
        String packageNames = SharedPreferencesServicer.getPreferences(cnt, C.SPF_SESSION_GLOBAL_PACKAGENAMES, C.SPF_KEY_GLOBAL_PACKAGENAMES, "");
        if ((packageNames != null) && !(packageNames.equalsIgnoreCase("")))
        {
            String[] packages = packageNames.split(",");
            for (int i = 0; i < packages.length; i++)
            {
                String pName = packages[i];
                String appInstallStatus = SharedPreferencesServicer.getSimplePreferences(cnt, C.SPF_SESSION_GLOBAL_PREF_APPNAME, C.SPF_KEY_GLOBAL_PREF_APPNAME + pName, C.APP_IS_NOTINSTALL);

                String appVersion = "";
                boolean isAppInstall = false;
                String firstInstallTime = "";
                try {
                    PackageInfo pInfo = cnt.getPackageManager().getPackageInfo(pName, 0);
                    if (pInfo != null) {
                        appVersion       = pInfo.versionName;
                        isAppInstall     = true;
                        firstInstallTime = StatParams.getFirstInstallTime(cnt, pName);
                    }
                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                }

                if ((appInstallStatus.equalsIgnoreCase(C.APP_IS_NOTINSTALL)) && (isAppInstall))
                {
                    appInstallStatus = C.APP_IS_INSTALL;
                    SharedPreferencesServicer.setSimplePreferences(cnt, C.SPF_SESSION_GLOBAL_PREF_APPNAME, C.SPF_KEY_GLOBAL_PREF_APPNAME + pName, C.APP_IS_INSTALL);
                }
                else if((appInstallStatus.equalsIgnoreCase(C.APP_IS_INSTALL)) && (!isAppInstall))
                {
                    appInstallStatus = C.APP_IS_DELETE;
                    SharedPreferencesServicer.setSimplePreferences(cnt, C.SPF_SESSION_GLOBAL_PREF_APPNAME, C.SPF_KEY_GLOBAL_PREF_APPNAME + pName, C.APP_IS_DELETE);
                }
                else if((appInstallStatus.equalsIgnoreCase(C.APP_IS_DELETE)) && (isAppInstall))
                {
                    appInstallStatus = C.APP_IS_INSTALL;
                    SharedPreferencesServicer.setSimplePreferences(cnt, C.SPF_SESSION_GLOBAL_PREF_APPNAME, C.SPF_KEY_GLOBAL_PREF_APPNAME + pName, C.APP_IS_INSTALL);
                }

                res += "\"" + pName + "\" : {";
                res += formatJsonParam("Version" , appVersion) + ",";
                res += formatJsonParam("firstInstallTime" , firstInstallTime) + ",";
                res += formatJsonParam("isAppInstall" , appInstallStatus);
                res += "}";
                if (i < packages.length ) {res += ",";}
            }
        }
        return res;
    }

    private static String formatJsonParam(String k, String v)
    {
        return "\"" + k + "\":\"" + v + "\"";
    }

    private void setPostRequestContent(HttpsURLConnection conn,
                                       String jsonObject) throws IOException {
        OutputStream os = conn.getOutputStream();
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
        writer.write(jsonObject);
        writer.flush();
        writer.close();
        os.close();
    }

    private static String readInputStreamAsString(InputStream in)
            throws IOException {
        BufferedInputStream bis = new BufferedInputStream(in);
        ByteArrayOutputStream buf = new ByteArrayOutputStream();
        int result = bis.read();
        while(result != -1) {
            byte b = (byte)result;
            buf.write(b);
            result = bis.read();
        }
        return buf.toString();
    }

}
