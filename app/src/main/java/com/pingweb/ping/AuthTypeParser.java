package com.pingweb.ping;

import java.util.Arrays;

/**
 * Created by Federico on 04/11/2015.
 */
public class AuthTypeParser {
    public static final String BASIC_INFO = "basic";
    public static final String READ = "read";
    public static final String WRITE = "write";
    public static final String VIEW_PROFILE = "view_profile";
    public static final String EDIT_PROFILE = "edit_profile";
    public static final String SETTINGS = "settings";
    public static final String FULL_ACCESS = "full_access";

    /**
     * Convert an array of permissions in a authToken string type
     * @param mPermissions Array of permissions you want to convert
     * @return The authToken type string
     */
    public static String toString(String[] mPermissions) {
        if (mPermissions == null || mPermissions.length < 1)
            return "";
        String string = mPermissions[0];
        for (String elem:mPermissions) {
            string += "/" + elem;
        }
        return string;
    }

    /**
     * Parse an authToken type string in an array with the permissions contained in the authToken type string
     * @param authTokenType The authToken type string to parse
     * @return Array contains the permissions parsed
     */
    public static String[] parse(String authTokenType) {
        if (authTokenType == null)
            return null;
        String[] parsed = authTokenType.split(":");
        return parsed;
    }

    /**
     *  Check if permissions are contained in the authToken type
     * @param authTokenType AuthToken type to check
     * @param requestedPermission Array of permissions to check
     * @return The result of check
     */
    public static boolean checkPermission(String authTokenType, String[] requestedPermission) {
        final String[] authTokenTypeParsed = parse(authTokenType);
        return checkPermission(authTokenTypeParsed, requestedPermission);
    }

    /**
     *  Check if permissions are contained in the permissions of authToken type
     * @param authTokenTypeParsed AuthToken type permissions to check
     * @param requestedPermission Array of permissions to check
     * @return The result of check
     */
    public static boolean checkPermission(String[] authTokenTypeParsed, String[] requestedPermission) {
        for (String permission:requestedPermission) {
            if (!Arrays.asList(authTokenTypeParsed).contains(permission))
                return false;
        }
        return true;
    }

    public static boolean isValid(String[] mPermissions) {
        for (String p:mPermissions) {
            if (!(p == BASIC_INFO ||
                    p == READ ||
                    p == WRITE ||
                    p == VIEW_PROFILE ||
                    p == EDIT_PROFILE ||
                    p == SETTINGS ||
                    p == FULL_ACCESS
                    )) {
                return false;
            }
        }

        return true;
    }
}
