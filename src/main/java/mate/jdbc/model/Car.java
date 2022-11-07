package mate.jdbc.model;

import java.util.Set;

public class Car {
    private Long id;
    private String model;
    private Manufacturer manufacturer;
    private Set<Driver> drivers;

    public Car() {
    }

    public Car(Long id, String model) {
        this.id = id;
        this.model = model;
    }

    public Car(String model, Manufacturer manufacturer, Set<Driver> drivers) {
        this.id = id;
        this.model = model;
        this.manufacturer = manufacturer;
        this.drivers = drivers;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public Manufacturer getManufacturer() {
        return manufacturer;
    }

    public void setManufacturer(Manufacturer manufacturer) {
        this.manufacturer = manufacturer;
    }

    public Set<Driver> getDrivers() {
        return drivers;
    }

    public void setDrivers(Set<Driver> drivers) {
        this.drivers = drivers;
    }

    public void addDriver(Driver driver) {
        drivers.add(driver);
    }

    public void removeDriver(Driver driver) {
        drivers.remove(driver);
    }

    @Override
    public String toString() {
        return "Car{"
                + "id=" + id
                + ", model='" + model + '\''
                + ", manufacturer=" + manufacturer
                + '}';
    }
}
