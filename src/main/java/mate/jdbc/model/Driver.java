package mate.jdbc.model;

import java.util.Objects;

public class Driver {
    private Long id;
    private String driverName;
    private String licenseNumber;

    public Driver() {
    }

    public Driver(String driverName, String licenseNumber) {
        this.driverName = driverName;
        this.licenseNumber = licenseNumber;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return driverName;
    }

    public void setName(String name) {
        this.driverName = name;
    }

    public String getLicenseNumber() {
        return licenseNumber;
    }

    public void setLicenseNumber(String licenseNumber) {
        this.licenseNumber = licenseNumber;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Driver driver = (Driver) o;
        return Objects.equals(id, driver.id)
                && Objects.equals(driverName, driver.driverName)
                && Objects.equals(licenseNumber, driver.licenseNumber);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, driverName, licenseNumber);
    }

    @Override
    public String toString() {
        return "Driver{"
                + "id=" + id
                + ", name='" + driverName + '\''
                + ", licenseNumber='" + licenseNumber + '\''
                + '}';
    }
}
