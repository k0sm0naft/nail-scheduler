package fern.nail.art.nailscheduler.util;

public class PhoneNumberFormatter {
    public static final String UKR_LOCAL_NUMBER = "380";
    public static final String PLUS = "+";
    private static PhoneNumberFormatter formatter;

    private PhoneNumberFormatter() {
    }

    public static PhoneNumberFormatter getFormatter() {
        if (formatter == null) {
            formatter = new PhoneNumberFormatter();
        }
        return formatter;
    }

    public String normalize(String phoneNumber) {
        String numericOnly = phoneNumber.replaceAll("\\D", "");
        if (numericOnly.length() == 10) {
            numericOnly = UKR_LOCAL_NUMBER + numericOnly;
        }
        if (numericOnly.length() == 12 && numericOnly.startsWith(UKR_LOCAL_NUMBER)) {
            return PLUS + numericOnly;
        }
        throw new IllegalArgumentException("Invalid phone number format");
    }
}
