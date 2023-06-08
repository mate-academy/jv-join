package mate.jdbc.model;

import java.util.Objects;

public class Driver {
    private Long id;
    private String name;
    private String licenseNumber;

    public Driver(String name, String licenseNumber) {
        this.name = name;
        this.licenseNumber = licenseNumber;
    }

    public Driver(Long id, String name, String licenseNumber) {
        this(name, licenseNumber);
        this.id = id;
        this.name = name;
    }

    public Driver() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLicenseNumber() {
        return licenseNumber;
    }

    public void setLicenseNumber(String licenseNumber) {
        this.licenseNumber = licenseNumber;
    }

    @Override
    public boolean equals(Object driver) {
        if (this == driver) {
            return true;
        }
        if (driver == null || getClass() != driver.getClass()) {
            return false;
        }
        Driver that = (Driver) driver;
        return Objects.equals(id, that.id)
                && Objects.equals(name, that.name)
                && Objects.equals(licenseNumber, that.licenseNumber);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, licenseNumber);
    }

    @Override
    public String toString() {
        return "Driver{"
                + "id=" + id
                + ", name='" + name + '\''
                + ", licenseNumber='" + licenseNumber + '\''
                + '}';
    }
}
