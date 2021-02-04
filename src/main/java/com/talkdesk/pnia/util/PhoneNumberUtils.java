package com.talkdesk.pnia.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class PhoneNumberUtils {

    /**
     * Business Rules:
     * A number is considered valid if it contains only digits,
     * an optional leading + and whitespace anywhere except immediately after the +.
     * A valid number has exactly 3 digits or more than 6 and less than 13.
     * 00 is acceptable as replacement for the leading +.
     */

    private static final String NUMBER_PATTERN = "^(\\+|00)(((\\d\\s?){6,13})|((\\d\\s?){3}))$";
    private static final String PREFIX_PATTERN = "^[0-9]*$";

    public static boolean isValidNumber(final String phoneNumber) {
        return phoneNumber.matches(NUMBER_PATTERN);
    }

    public static boolean isValidPrefix(final String prefix) {
        return prefix.matches(PREFIX_PATTERN);
    }

    public static String extractActualNumber(final String phoneNumber) {
        final Pattern p = Pattern.compile(NUMBER_PATTERN);
        final Matcher m = p.matcher(phoneNumber);

        /* Quick explanation on regex matching groups:
            m.group(0) -> full string
            m.group(1) -> + or 00 condition specified by regex part (\+|00)
            m.group(2) -> actual number specified by regex part (((\d\s?){6,13})|((\d\s?){3}))
         in this case, we only want to extract the number and trim the whitespaces.
         */
        return m.matches() ? m.group(2).replaceAll("\\s+", "") : null;
    }
}
