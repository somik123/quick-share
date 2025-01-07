package org.somik.quick_share.utils;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.StringTokenizer;
import java.util.logging.Logger;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import jakarta.servlet.http.HttpServletRequest;

public class CommonUtils {
    private static final Logger LOG = Logger.getLogger(CommonUtils.class.getName());

    public static String encryptPassword(String password) {
        return new BCryptPasswordEncoder().encode(password);
    }

    public static boolean isPasswordCorrect(String encryptedPassword, String userInputPassword) {
        return new BCryptPasswordEncoder().matches(userInputPassword, encryptedPassword);
    }

    public static String getClientIpAddress(HttpServletRequest request) {
        String xForwardedForHeader = request.getHeader("X-Forwarded-For");
        if (xForwardedForHeader == null) {
            return request.getRemoteAddr();
        } else {
            return new StringTokenizer(xForwardedForHeader, ",").nextToken().trim();
        }
    }

    // Log errors
    public static void logErrors(Logger LOG, Exception e) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        e.printStackTrace(pw);
        String sStackTrace = sw.toString();
        System.out.println(sStackTrace);
        LOG.warning(sStackTrace);
    }

    public static String generateIdenticonHash(String data) {
        try {
            data = data.toLowerCase();

            MessageDigest md = MessageDigest.getInstance("SHA3-224");
            byte[] dataArr = md.digest(data.getBytes("UTF-8"));

            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < dataArr.length; ++i) {
                sb.append(Integer.toHexString((dataArr[i] & 0xFF) | 0x100).substring(1, 3));
            }
            return sb.toString();

        } catch (NoSuchAlgorithmException e) {
            logErrors(LOG, e);
        } catch (UnsupportedEncodingException e) {
            logErrors(LOG, e);
        }
        return null;
    }
}
