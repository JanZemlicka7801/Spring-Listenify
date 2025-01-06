package listenify.config;

import java.util.Arrays;
import java.util.List;

public class cardService {

    /**
     * Validates if a credit cards number belongs to either Visa or MasterCard.
     *
     * @param creditCard The credit card number as a String either with or without spaces.
     * @return true if the card is either Mastercard or Visa card.
     * @auther Jan Zemlicka
     */
    public static boolean cardRegister(String creditCard) {
        String sanitizedCard = creditCard.replaceAll("\\s+", "");

        boolean isVisa = isBrandVisa(sanitizedCard);
        boolean isMaster = isBrandMaster(sanitizedCard);

        return isVisa || isMaster;
    }

    //represents a new range of mastercard possible numbers
    public static List<String> MASTERCARD_NEW_RANGE = Arrays.asList("222100","272099");

    /**
     * Checks if a given credit card number is a valid Mastercard number.
     *
     * @param number A credit card number as a String.
     * @return true if the card is valid Mastercard.
     * @auther Jan Zemlicka
     */
    public static boolean isBrandMaster(final String number) {
        return number != null && number.length() == 16 && (number.matches("5[1-5][0-9]{14}") || isValidMasterBin(number));
    }

    /**
     * Checks if a given credit card number is a valid Visa number.
     *
     * @param number A credit card number as a String.
     * @return true if the card is valid Visa.
     * @auther Jan Zemlicka
     */
    public static boolean isBrandVisa(final String number) {
        if (number == null || number.length() == 0 || (!number.startsWith("4")) || (number.length() != 13 && number.length() != 16)) {
            return false;
        }

        return isValidLuhn(number);
    }

    /**
     * Validates if the first six digits of a Mastercard number fall within new range of BINs.
     *
     * @param number The credit card number as a String.
     * @return true if the first six digits are within the valid Mastercard range.
     * @auther Jan Zemlicka
     */
    public static boolean isValidMasterBin(String number) {
        if (number.length() < 6) {
            return false;
        }

        int parsedNumber = Integer.parseInt(number.substring(0, 6));

        int startingRange = Integer.parseInt(MASTERCARD_NEW_RANGE.get(0));
        int endingRange = Integer.parseInt(MASTERCARD_NEW_RANGE.get(1));

        return parsedNumber >= startingRange && parsedNumber <= endingRange;
    }

    /**
     * Validates a credit number using Luhn algorithm.
     *
     * @param number A credit card number as a String.
     * @return true if the number is valid according to the Luhn algorithm.
     * @auther Jan Zemlicka
     */
    public static boolean isValidLuhn(String number) {
        int sum = 0;
        boolean alternate = false;

        for (int i = number.length() - 1; i >= 0; i--) {
            int n = Character.getNumericValue(number.charAt(i));

            if (alternate) {
                n *= 2;
                if (n > 9) {
                    n -= 9;
                }
            }

            sum += n;
            alternate = !alternate;
        }

        return sum % 10 == 0;
    }
}
