package com.mks.sendstatlib.CryptoProvider;

import java.io.UnsupportedEncodingException;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class SimpleBLOWFISHCryptoProvider extends BaseCryptoProvider implements ICryptoProvider {
    private String seed = "ZcQWU8xYQsfB1Rasde1plofL78g91R0TRfUbW7qwVmn=";
    public void setSeed(String param)
    {
        this.seed = param;
    }

    @Override
    public String deCript(String src)
    {
        if (src == null) {return null;}
        try{
            SecretKeySpec key = new SecretKeySpec(seed.getBytes("utf-8"), "BLOWFISH");
            Cipher ecipher = Cipher.getInstance("BLOWFISH");
            ecipher.init(Cipher.DECRYPT_MODE, key);
            byte[] dec = ecipher.doFinal(toByte(src));
            return new String(dec);
        }
        catch(Exception exc )
        {
            try{
                exc.printStackTrace();
            }catch(Exception exc2){}

        }
        return src;
    }

    @Override
    public String cript(String src)
    {
        if (src == null) {return null;}
        try{
            SecretKeySpec key = new SecretKeySpec(seed.getBytes("utf-8"), "BLOWFISH");
            Cipher ecipher = Cipher.getInstance("BLOWFISH");
            ecipher.init(Cipher.ENCRYPT_MODE, key);
            byte[] utf8 = src.getBytes();
            byte[] enc = ecipher.doFinal(utf8);
            return toHex(enc);
        }
        catch(Exception exc )
        {
            try{
                exc.printStackTrace();
            }catch(Exception exc2){}

        }
        return src;
    }

    private String toHex(String txt)
    {
        try {
            return toHex(txt.getBytes("UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return toHex(txt.getBytes());
    }

    private String fromHex(String hex) {
        return new String(toByte(hex));
    }

    private byte[] toByte(String hexString) {
        int len = hexString.length()/2;
        byte[] result = new byte[len];
        for (int i = 0; i < len; i++)
            result[i] = Integer.valueOf(hexString.substring(2*i, 2*i+2), 16).byteValue();
        return result;
    }

    private String toHex(byte[] buf) {
        if (buf == null)
            return "";
        StringBuffer result = new StringBuffer(2*buf.length);
        for (int i = 0; i < buf.length; i++) {
            appendHex(result, buf[i]);
        }
        return result.toString();
    }
    private final String HEX = "0123456789ABCDEF";
    private void appendHex(StringBuffer sb, byte b) {
        sb.append(HEX.charAt((b>>4)&0x0f)).append(HEX.charAt(b&0x0f));
    }
}

