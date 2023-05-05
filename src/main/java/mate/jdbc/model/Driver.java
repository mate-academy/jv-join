package mate.jdbc.model;

import java.util.Objects;

public class Driver {
    private Long id;
    private String name;
    private String licenseNumber;
    private Car car;

    public Driver(Long id, String name, String licenseNumber, Car car) {
        this.id = id;
        this.name = name;
        this.licenseNumber = licenseNumber;
        this.car = car;
    }

    public Driver(Long id, String name, String licenseNumber) {
        this.id = id;
        this.name = name;
        this.licenseNumber = licenseNumber;
    }

    public Driver(String name, String licenseNumber, Car car) {
        this.name = name;
        this.licenseNumber = licenseNumber;
        this.car = car;
    }

    public Driver(String name, String licenseNumber) {
        this.name = name;
        this.licenseNumber = licenseNumber;
    }

    public Car getCar() {
        return car;
    }

    public Driver setCar(Car car) {
        this.car = car;
        return this;
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
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Driver driver = (Driver) o;
        return Objects.equals(id, driver.id) && Objects.equals(name, driver.name)
                && Objects.equals(licenseNumber, driver.licenseNumber)
                && Objects.equals(car, driver.car);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, licenseNumber, car);
    }

    @Override
    public String toString() {
        return "Driver{"
                + "id=" + id
                + ", name='"
                + name + '\''
                + ", licenseNumber='" + licenseNumber + '\''
                + ", car=" + car
                + '}';
    }
}
