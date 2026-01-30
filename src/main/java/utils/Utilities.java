package utils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Utilities {

    public static final int IMPLICIT_WAIT_TIME = 10;
    public static final int PAGE_LOAD_TIME = 10;

    public static String generateEmailWithTimeStamp() {
        return "user_" +
                LocalDateTime.now().format(
                        DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"))
                + "@example.com";
    }
}
