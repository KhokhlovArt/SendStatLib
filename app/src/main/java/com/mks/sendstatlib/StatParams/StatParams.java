package com.mks.sendstatlib.StatParams;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.provider.Settings;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyManager;
import android.view.Display;
import android.view.WindowManager;

import com.google.android.gms.ads.identifier.AdvertisingIdClient;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;

import java.io.IOException;
import java.lang.reflect.Method;
import java.math.BigInteger;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class StatParams {
    public String action;            // (приведены в таблице ниже)
    public String quary;             // Уточнение запроса
    public String actiontime;        //(время события)
    public String gaid;
    public String appVersion;        //storeversion; // (версия Стора) / приложения
    public String packagename;       //
    public String osversion;         //(версия операционной системы)
    public String useragent;         // (полное значение UA)
    public String manufacturer;      // (производитель устройства пользователя)
    public String model;             // (модель устройства пользователя)
    public String screenwidth;       //
    public String screenheight;
    public String connectiontype;    // (может принимать значения: WIFI, 4G, 3G, GPRS, WIMAX, OTHER)
    public String referrer;
    public String firstInstallTime;  // Время установки
    public String IP;                // (ip-адрес пользователя)
    public String MAC;               //МАК-адрес устройства
    public String imei1;             // (если в момент события пользователь дал соответствующее разрешение, если не дал - поле передается пустым)
    public String imsi1;             // (если в момент события пользователь дал соответствующее разрешение, если не дал - поле передается пустым)
    public String imei2;             // (если в момент события пользователь дал соответствующее разрешение, если не дал - поле передается пустым)
    public String imsi2;             // (если в момент события пользователь дал соответствующее разрешение, если не дал - поле передается пустым)
    public String imei3;             // (если в момент события пользователь дал соответствующее разрешение, если не дал - поле передается пустым)
    public String imsi3;             // (если в момент события пользователь дал соответствующее разрешение, если не дал - поле передается пустым)
    public String msgTag;            //Тег сообщения (рандомная строка), нуужна для отслеживания задваиваний передачи
    public String androidID;

    public StatParams(Context cnt, String action, String quary)
    {
        this.action        = action;
        this.quary         = quary;

        updateIMSI_IMEI(cnt);

        actiontime        = getActionTime();
        MAC               = getMAC(cnt);
        IP                = getIP(cnt);
        screenwidth       = getScreenwidth(cnt);
        screenheight      = getScreenheight(cnt);
        model             = getModel();
        manufacturer      = getManufactor();
        osversion         = getVersionOs();
        gaid              = getGAID(cnt);
        packagename       = getPackagename(cnt);
        appVersion        = getAppVersion(cnt);
        useragent         = getUseragent(cnt);
        connectiontype    = getConnectiontype(cnt);
        firstInstallTime  = getFirstInstallTime(cnt, packagename);
        msgTag            = getMsgTag(cnt);
        androidID         = getAndroidId(cnt);
    }


    private static String getConnectiontype(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        @SuppressLint("MissingPermission")
        NetworkInfo info = cm.getActiveNetworkInfo();
        if(info==null || !info.isConnected())
            return "-"; //not connected
        if(info.getType() == ConnectivityManager.TYPE_WIFI)
            return "WIFI";
        if(info.getType() == ConnectivityManager.TYPE_MOBILE){
            int networkType = info.getSubtype();
            switch (networkType) {
                case TelephonyManager.NETWORK_TYPE_GPRS:
                case TelephonyManager.NETWORK_TYPE_EDGE:
                case TelephonyManager.NETWORK_TYPE_CDMA:
                case TelephonyManager.NETWORK_TYPE_1xRTT:
                case TelephonyManager.NETWORK_TYPE_IDEN: //api<8 : replace by 11
                    return "2G";
                case TelephonyManager.NETWORK_TYPE_UMTS:
                case TelephonyManager.NETWORK_TYPE_EVDO_0:
                case TelephonyManager.NETWORK_TYPE_EVDO_A:
                case TelephonyManager.NETWORK_TYPE_HSDPA:
                case TelephonyManager.NETWORK_TYPE_HSUPA:
                case TelephonyManager.NETWORK_TYPE_HSPA:
                case TelephonyManager.NETWORK_TYPE_EVDO_B: //api<9 : replace by 14
                case TelephonyManager.NETWORK_TYPE_EHRPD:  //api<11 : replace by 12
                case TelephonyManager.NETWORK_TYPE_HSPAP:  //api<13 : replace by 15
                case TelephonyManager.NETWORK_TYPE_TD_SCDMA:  //api<25 : replace by 17
                    return "3G";
                case TelephonyManager.NETWORK_TYPE_LTE:    //api<11 : replace by 13
                case TelephonyManager.NETWORK_TYPE_IWLAN:  //api<25 : replace by 18
                case 19:  //LTE_CA
                    return "4G";
                default:
                    return "OTHER";
            }
        }
        return "?";
    }


    private String getAppVersion(Context cnt) {
        PackageManager pm = cnt.getPackageManager();
        try {
            return pm.getPackageInfo(packagename, 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            return "";
        }
    }

    private String getPackagename(Context cnt) {
        return cnt.getPackageName();
    }

    private String getGAID(Context cnt) {
        String realGAID = "";
        AdvertisingIdClient.Info adInfo = null;
        try {
            adInfo = AdvertisingIdClient.getAdvertisingIdInfo(cnt);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (GooglePlayServicesNotAvailableException e) {
            e.printStackTrace();
        } catch (GooglePlayServicesRepairableException e) {
            e.printStackTrace();
        }
        if (adInfo != null){
            String adInfoId = adInfo.getId();
            if ((adInfoId != null) && (!adInfoId.equals("")))
            {
                realGAID = adInfoId;
            }
        }
        return realGAID.equals("") ? null : realGAID;
    }

    private static String getVersionOs()
    {
        return "" + Build.VERSION.RELEASE;
    }

    private static String getModel(){return "" + Build.MODEL;}

    private static String getManufactor(){return "" + Build.MANUFACTURER;}



    private static String getDevice(){return "" + Build.DEVICE;}
    private static String getBrand(){return "" + Build.BRAND;}
    private static String getAndroidId(Context context){return (context != null) ? Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID) : null;}
    private static String getProductId(){return "" + Build.PRODUCT;}




    private static String getScreenheight(Context context){
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        return "" + size.y;
    }

    private static String getScreenwidth(Context context){
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        return "" + size.x;
    }

    private String getIP(Context cnt)
    {
        String IP = "";
        try {
            WifiManager manager = (WifiManager) cnt.getSystemService(Context.WIFI_SERVICE);
            WifiInfo wifiinfo = manager.getConnectionInfo();
            byte[] myIPAddress = BigInteger.valueOf(wifiinfo.getIpAddress()).toByteArray();
            InetAddress myInetIP = null;
            byte[] myIPAddress2 = {0,0,0,0};
            for(int i = 0; i< myIPAddress.length; i++)
            {
                myIPAddress2[myIPAddress.length-1-i] = myIPAddress[i];
            }
            myInetIP = InetAddress.getByAddress(myIPAddress);
            IP = myInetIP.getHostAddress();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        catch (Error e ){
            e.printStackTrace();
        }
        return IP;
    }

    private String getMAC(Context cnt)
    {
        try {
            List<NetworkInterface> all = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface nif : all) {
                if (!nif.getName().equalsIgnoreCase("wlan0")) continue;

                byte[] macBytes = nif.getHardwareAddress();
                if (macBytes == null) {
                    return "";
                }

                StringBuilder res1 = new StringBuilder();
                for (byte b : macBytes) {
                    res1.append(Integer.toHexString(b & 0xFF) + ":");
                }

                if (res1.length() > 0) {
                    res1.deleteCharAt(res1.length() - 1);
                }
                return res1.toString();
            }
        } catch (Exception ex) {
            //handle exception
        }
        return "";
    }

    private String getActionTime()
    {
        long time = Calendar.getInstance().getTime().getTime();
        Date date = new java.util.Date(time);
        SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sdf.format(date);
    }

    //Метод устанавливает imei1,2,3 и imsi1,2,3
    @SuppressLint("MissingPermission")
    private void updateIMSI_IMEI(Context context)
    {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
            if (context.checkCallingOrSelfPermission(Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED) {
                TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
                SubscriptionManager subscriptionManager = (SubscriptionManager) context.getSystemService(Context.TELEPHONY_SUBSCRIPTION_SERVICE);

                if (subscriptionManager.getActiveSubscriptionInfoCount() == 0) {
                    imei1 = telephonyManager.getDeviceId();
                } else if (subscriptionManager.getActiveSubscriptionInfoCount() == 1) {
                    if (SubscriptionManager.from(context).getActiveSubscriptionInfoForSimSlotIndex(0) != null) {
                        int subscriptionId1 = SubscriptionManager.from(context).getActiveSubscriptionInfoForSimSlotIndex(0).getSubscriptionId();
                        try {
                            Class c = Class.forName("android.telephony.TelephonyManager");
                            Method m = c.getMethod("getSubscriberId", int.class);
                            Object o = m.invoke(telephonyManager, subscriptionId1);
                            imsi1 = (String) o;
                            imei1 = getDeviceIdBySlot(context, "getDeviceId", 0);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                } else if (subscriptionManager.getActiveSubscriptionInfoCount() == 2) {

                    int subscriptionId1 = SubscriptionManager.from(context).getActiveSubscriptionInfoForSimSlotIndex(0).getSubscriptionId();
                    int subscriptionId2 = SubscriptionManager.from(context).getActiveSubscriptionInfoForSimSlotIndex(1).getSubscriptionId();

                    try {
                        Class c = Class.forName("android.telephony.TelephonyManager");
                        Method m = c.getMethod("getSubscriberId", int.class);
                        Object o = m.invoke(telephonyManager, subscriptionId1);

                        imsi1 = (String) o;
                        o = m.invoke(telephonyManager, subscriptionId2);
                        imsi2 = (String) o;

                        m = c.getMethod("getDeviceId", int.class);
                        o = m.invoke(telephonyManager, subscriptionId1);
                        imei1 = (String) o;

                        o = m.invoke(telephonyManager, subscriptionId2);
                        imei2 = (String) o;

                        imei1 = getDeviceIdBySlot(context, "getDeviceId", 0);
                        imei2 = getDeviceIdBySlot(context, "getDeviceId", 1);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    int subscriptionId1 = SubscriptionManager.from(context).getActiveSubscriptionInfoForSimSlotIndex(0).getSubscriptionId();
                    int subscriptionId2 = SubscriptionManager.from(context).getActiveSubscriptionInfoForSimSlotIndex(1).getSubscriptionId();
                    int subscriptionId3 = SubscriptionManager.from(context).getActiveSubscriptionInfoForSimSlotIndex(2).getSubscriptionId();

                    try {
                        Class c = Class.forName("android.telephony.TelephonyManager");
                        Method m = c.getMethod("getSubscriberId", int.class);

                        Object o = m.invoke(telephonyManager, subscriptionId1);
                        imsi1 = (String) o;
                        o = m.invoke(telephonyManager, subscriptionId2);
                        imsi2 = (String) o;
                        o = m.invoke(telephonyManager, subscriptionId3);
                        imsi3 = (String) o;

                        m = c.getMethod("getDeviceId", int.class);
                        o = m.invoke(telephonyManager, subscriptionId1);
                        imei1 = (String) o;
                        o = m.invoke(telephonyManager, subscriptionId2);
                        imei2 = (String) o;
                        o = m.invoke(telephonyManager, subscriptionId3);
                        imei3 = (String) o;

                        imei1 = getDeviceIdBySlot(context, "getDeviceId", 0);
                        imei2 = getDeviceIdBySlot(context, "getDeviceId", 1);
                        imei3 = getDeviceIdBySlot(context, "getDeviceId", 2);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

            } else {
            }
        }
    }
    private static String getDeviceIdBySlot(Context context, String predictedMethodName, int slotID) {

        String imei = null;

        TelephonyManager telephony = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);

        try {

            Class<?> telephonyClass = Class.forName(telephony.getClass().getName());

            Class<?>[] parameter = new Class[1];
            parameter[0] = int.class;
            Method getSimID = telephonyClass.getMethod(predictedMethodName, parameter);

            Object[] obParameter = new Object[1];
            obParameter[0] = slotID;
            Object ob_phone = getSimID.invoke(telephony, obParameter);

            if (ob_phone != null) {
                imei = ob_phone.toString();

            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

        return imei;
    }

    private String getUseragent(Context cnt) {
        //String ua=new WebView(cnt).getSettings().getUserAgentString();
        //return ua;
        return "";
    }

    public static String getFirstInstallTime(Context cnt, String packageName) {
        long time = Calendar.getInstance().getTime().getTime();
        PackageManager pm = cnt.getPackageManager();
        try {
            time = pm.getPackageInfo(packageName, 0).firstInstallTime;
        }
        catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        Date date = new java.util.Date(time);
        SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sdf.format(date);
    }

    private String getMsgTag(Context cnt) {
        return  UUID.randomUUID().toString();
    }

    public String getAndroidID(Context cnt) {
        return Settings.Secure.getString(cnt.getContentResolver(), Settings.Secure.ANDROID_ID);
    }
}
