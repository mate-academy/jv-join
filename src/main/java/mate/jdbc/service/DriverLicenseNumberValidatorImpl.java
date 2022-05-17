package mate.jdbc.service;

import mate.jdbc.lib.Service;

@Service
public class DriverLicenseNumberValidatorImpl implements DriverLicenseNumberValidator {
    private static final String VALID_SYMBOLS = "\\d+";
    private static final int VALID_LENGTH = 12;

    @Override
    public String validate(String licenseNumber) {
        if (!licenseNumber.matches(VALID_SYMBOLS)
                || licenseNumber.length() != VALID_LENGTH) {
            throw new RuntimeException("Wrong license number format: "
                    + licenseNumber
                    + ". It's allowed to use digits only and length should be "
                    + VALID_LENGTH);
        }
        return licenseNumber;
    }
}
