package mate.jdbc.util;

import java.util.Random;

public class Tools {
    private static Random random = new Random();

    public static String numberLicenseGenerator() {
        return Integer.toString(random.nextInt(1000000000));
    }
}
