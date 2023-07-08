package antifraud.utilities;

public class LuhnAlgorithmChecker {
    public static boolean isLuhnValid(String number) {
        StringBuilder reversedNumber = new StringBuilder(number).reverse();
        int sum = 0;
        boolean doubleDigit = false;
        for (int i = 0; i < reversedNumber.length(); i++) {
            int digit = Character.getNumericValue(reversedNumber.charAt(i));
            if (doubleDigit) {
                digit *= 2;
                if (digit > 9) {
                    digit = digit / 10 + digit % 10;
                }
            }
            sum += digit;
            doubleDigit = !doubleDigit;
        }
        return sum % 10 == 0;
    }
}
