package com.mks.sendstatlib.HttpsConnection;

import android.util.Base64;

import com.mks.sendstatlib.C;

import java.io.IOException;
import java.net.Authenticator;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

public class HttpsConnectionServicer {

    public static ArrayList<Proxy> AdditionalProxys;
    int timeout = 0;

    TrustManager[] trustAllCerts = new TrustManager[]{
            new X509TrustManager() {
                public java.security.cert.X509Certificate[] getAcceptedIssuers(){return null;}
                public void checkClientTrusted(java.security.cert.X509Certificate[] certs, String authType){}
                public void checkServerTrusted(java.security.cert.X509Certificate[] certs, String authType){}
            }
    };

    private static byte[] toByte(String hexString) {
        int len = hexString.length()/2;
        byte[] result = new byte[len];
        for (int i = 0; i < len; i++)
            result[i] = Integer.valueOf(hexString.substring(2*i, 2*i+2), 16).byteValue();
        return result;
    }

    public HttpsURLConnection getHttpsConnection(URL url)
    {
        try {
            setProxy();
            return getSimpleHttpsConnection(url);
        } catch (CertificateException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    //Метод который возвращает HttpsURLConnection, при этом не переключает Proxy
    public HttpsURLConnection getSimpleHttpsConnection(URL url) throws CertificateException, NoSuchAlgorithmException, IOException {
        HttpsURLConnection connection = null;
        try {


            // TODO: Сделать по людски! Это решение - огромная дыра в безопасности:
            // см. https://developer.android.com/training/articles/security-ssl.html#CommonHostnameProbs
            //     https://stackoverflow.com/questions/6659360/how-to-solve-javax-net-ssl-sslhandshakeexception-error?utm_medium=organic&utm_source=google_rich_qa&utm_campaign=google_rich_qa
            //---------------------------------------------------------------------->
            HostnameVerifier hostnameVerifier = new HostnameVerifier() {
                @Override
                public boolean verify(String hostname, SSLSession session) {
                    return true;
                }
            };
//            SSLContext sc = SSLContext.getInstance("SSL");
//            //sc.init(kms, trustAllCerts, new java.security.SecureRandom());
//            sc.init(null, trustAllCerts, new java.security.SecureRandom());
//            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());


            connection = (HttpsURLConnection) url.openConnection();
            if (timeout != 0) {
                connection.setConnectTimeout(timeout);
            }
            connection.setHostnameVerifier(hostnameVerifier);


            connection.setRequestMethod("POST");

            String authString = "intlib" + ":" + "dfs$3dDw23";

            String authStringEnc = Base64.encodeToString( authString.getBytes(), Base64.DEFAULT );

            connection.addRequestProperty("Authorization", "Basic " + authStringEnc);
            //<----------------------------------------------------------------------
//        } catch (KeyStoreException e) {
//            e.printStackTrace();
//        } catch (UnrecoverableKeyException e) {
//            e.printStackTrace();
//        } catch (KeyManagementException e) {
//            e.printStackTrace();
//        }
        }catch (Error e)
        {
            e.printStackTrace();
        } /*/catch (KeyManagementException e) {
            e.printStackTrace();
        }*/


        return connection;
    }

    public boolean checkInternetConnection() {
        Boolean result = false;
        HttpsURLConnection connection = null;
        try {
            connection = getSimpleHttpsConnection(new URL(C.PROXY_BASE_URL));
            if (connection != null) {
                connection.connect();
                result = (connection.getResponseCode() == HttpURLConnection.HTTP_UNAUTHORIZED); //HttpURLConnection.HTTP_OK
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (connection != null) {
                try {
                    connection.disconnect();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return result;
    }

    private void setProxy()
    {
        List<Proxy> proxys = getProxyArray();
        if (!proxys.isEmpty())
        {
            for (Iterator<Proxy> elem = proxys.iterator(); elem.hasNext();) {
                if (checkInternetConnection()) {return;}
                Proxy p = elem.next();
                timeout                = p.getTimeout();
                String host            = p.getHost();
                String port            = p.getPort();
                String getHTTPUsername = p.getUsername();
                String getHTTPPassword = p.getPassword();
                Properties properties  = System.getProperties();
                properties.setProperty("http.proxyHost", host);
                properties.setProperty("http.proxyPort", port);
                properties.setProperty("https.proxyHost", host);
                properties.setProperty("https.proxyPort", port);

                System.getProperties().put("http.proxyUser", getHTTPUsername);
                System.getProperties().put("http.proxyPassword", getHTTPPassword);
                Authenticator.setDefault(new ProxyAuthenticator(getHTTPUsername, getHTTPPassword));
            }
        }
    }

    private List<Proxy> getProxyArray()
    {
        List<Proxy> proxys = new ArrayList<Proxy>();
//        proxys.add(new Proxy().setHost("proxy01.appclick.org").setPort("80").setUsername("fakegaid").setPassword("jMs1pfIw9tz?").setTimeout(0));
//        proxys.add(new Proxy().setHost("proxy02.appclick.org").setPort("8080").setUsername("fakegaid").setPassword("jMs1pfIw9tz?").setTimeout(0));
//        if ((AdditionalProxys != null) &&(!AdditionalProxys.isEmpty())) {
//            proxys.addAll(AdditionalProxys);
//        }

        return proxys;
    }
}
