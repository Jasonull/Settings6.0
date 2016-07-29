package com.android.settings.visitor;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import android.content.Context;
import android.net.Uri;

public class VisitorModeUntil {
    public static final String DBNAME = "visitordb";
    public static final String TNAME = "visitor";
    public static final int  VERSION = 1;
    
    public static String TID = "tid";
    //public static final String PKGNAME = "packagename";
    //public static final String LOCK = "lock";
  
    public static final int LOCKAPP = 1;
    public static final int LOCKAPP_ID = 2;
    
    public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.visitor.database";  
    
    public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.visitor.database";
    
    public static final String AUTHORITY = "com.android.settings.visitor.authority";  
    public static final Uri CONTENT_URI = Uri.parse("content://"+ AUTHORITY +"/" + TNAME);
    
    public static String passwordsToHash(String inputString) {
        if (inputString == null)
            return null;
        String str1 = null;

        try {
            byte[] arrayOfByte1 = inputString.getBytes();
            str1 = "SHA-1";
            byte[] arrayOfByte2 = MessageDigest.getInstance(str1).digest(arrayOfByte1);
            str1 = "MD5";
            byte[] arrayOfByte3 = MessageDigest.getInstance(str1).digest(arrayOfByte1);
            String str2 = toHex(arrayOfByte2) + toHex(arrayOfByte3);
            return str2;
        } catch (NoSuchAlgorithmException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

    private static String toHex(byte[] paramArrayOfByte) {
        String str1 = "";
        for (int i = 0; i < paramArrayOfByte.length; i++) {
            String str2 = str1 + "0123456789ABCDEF".charAt(0xF & paramArrayOfByte[i] >> 4);
            str1 = str2 + "0123456789ABCDEF".charAt(0xF & paramArrayOfByte[i]);
        }
        return str1;
    }
}
