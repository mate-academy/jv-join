package mate.jdbc.model;

import java.util.List;

public class Car {
    private Long id;
    private String name;
    private Manufacturer manufacturer;
    private List<Driver> drivers;

    public Car() {
    }

    public Car(String name, Manufacturer manufacturer) {
        this.name = name;
        this.manufacturer = manufacturer;
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

    public Manufacturer getManufacturer() {
        return manufacturer;
    }

    public void setManufacturer(Manufacturer manufacturer) {
        this.manufacturer = manufacturer;
    }

    public List<Driver> getDrivers() {
        return drivers;
    }

    public List<Driver> addDriverToCar(Driver driver) {
        drivers.add(driver);
        return drivers;
    }

    public List<Driver> removeDriverFromCar(Driver driver) {
        drivers.remove(driver);
        return drivers;
    }

    public void setDrivers(List<Driver> drivers) {
        this.drivers = drivers;
    }

    @Override
    public String toString() {
        return "Car{"
                + "id=" + id
                + ", name='" + name + '\''
                + ", manufacturer=" + manufacturer
                + ", drivers=" + drivers
                + '}';
    }
}
