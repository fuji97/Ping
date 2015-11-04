package com.pingweb.ping;

import java.util.Arrays;

/**
 * Created by Federico on 04/11/2015.
 */
public class AuthTypeParser {
    public static final String READ = "read";
    public static final String WRITE = "write";
    public static final String VIEW_PROFILE = "view_profile";
    public static final String EDIT_PROFILE = "edit_profile";
    public static final String SETTINGS = "settings";
    public static final String FULL_ACCESS = READ + ":" + WRITE + ":" + VIEW_PROFILE + ":" + EDIT_PROFILE + ":" + SETTINGS;

    public static String toString(String[] mPermissions) {
        if (mPermissions == null || mPermissions.length < 1)
            return "";
        String string = mPermissions[0];
        for (String elem:mPermissions) {
            string += "/" + elem;
        }
        return string;
    }

    public static String[] parse(String mString) {
        if (mString == null)
            return null;
        String[] parsed = mString.split(":");
        return parsed;
    }

    public static boolean checkPermission(String mString, String[] mPermissions) {
        final String[] parsed = parse(mString);
        boolean isPresent;

        for (String permission:mPermissions) {
            if (!Arrays.asList(parsed).contains(permission))
                return false;
        }
        return true;
    }
}
