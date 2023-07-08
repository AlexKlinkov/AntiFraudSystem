package antifraud.utilities;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class IPAddressValidator {
    private static final String IP_ADDRESS_PATTERN =
            "^(25[0-5]|2[0-4][0-9]|[0-1][0-9]{2}|[0-9]{2}|[0-9])" +
                    "(\\.(25[0-5]|2[0-4][0-9]|[0-1][0-9]{2}|[0-9]{2}|[0-9])){3}$";

    public static boolean isValidIPAddress(String ipAddress) {
        Pattern pattern = Pattern.compile(IP_ADDRESS_PATTERN);
        Matcher matcher = pattern.matcher(ipAddress);
        return matcher.matches();
    }
}
