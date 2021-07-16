package mate.jdbc.model;

import java.util.Objects;

public class Driver {
    private Long id;
    private String title;
    private String licenseNumber;

    public Driver() {
    }

    public Driver(String title, String licenseNumber) {
        this.title = title;
        this.licenseNumber = licenseNumber;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
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
                && Objects.equals(title, driver.title)
                && Objects.equals(licenseNumber, driver.licenseNumber);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, title, licenseNumber);
    }

    @Override
    public String toString() {
        return "Driver{"
                + "id=" + id
                + ", name='" + title + '\''
                + ", licenseNumber='" + licenseNumber + '\''
                + '}';
    }
}
