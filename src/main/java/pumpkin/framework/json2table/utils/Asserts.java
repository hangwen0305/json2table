package pumpkin.framework.json2table.utils;

public class Asserts {
    public static void hasArg(final Object object, final String message) {
        if (object == null) {
            throw new IllegalArgumentException(message);
        }
    }
}
