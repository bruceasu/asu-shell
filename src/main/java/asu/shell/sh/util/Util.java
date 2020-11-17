package asu.shell.sh.util;

import asu.shell.sh.AsuShellException;
import org.apache.commons.lang.StringUtils;

public class Util {

    public static String systemProperty(String paramString) {
        return System.getProperty(paramString);
    }

    public static void systemProperty(String paramString1, String paramString2) {
        System.getProperties().put(paramString1, paramString2);
    }

    public static void systemProperty(String key, String value, boolean ifEmpty) {
        if (ifEmpty) {
            String v = systemProperty(key);
            if (StringUtils.isEmpty(v)) {
                System.getProperties().put(key, value);
            }
        } else {
            System.getProperties().put(key, value);
        }
    }

    public static String removeEscapes(String paramString) {
        char[] arrayOfChar = paramString.toCharArray();
        int i = 0;
        int j = 0;
        while (i < arrayOfChar.length) {
            char k = arrayOfChar[(i++)];
            if (k == 92) {
                if (i == arrayOfChar.length) {
                    throw new AsuShellException("Malformed Command-line argument: " + paramString);
                }
                k = arrayOfChar[(i++)];
            }
            arrayOfChar[(j++)] = k;
        }
        return new String(arrayOfChar, 0, j);
    }

}
