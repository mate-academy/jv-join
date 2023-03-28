package mate.jdbc.model;

import java.util.HashSet;
import java.util.Set;

public class Car {
    private Long id;
    private Manufacturer manufacturer;
    private String model;
    private Set<Driver> drivers;

    public Car() {
        drivers = new HashSet<>();
    }

    public Car(Manufacturer manufacturer, String model, Set<Driver> drivers) {
        this.manufacturer = manufacturer;
        this.model = model;
        this.drivers = drivers;
    }

    public Car(Long id, Manufacturer manufacturer, String model) {
        this.id = id;
        this.manufacturer = manufacturer;
        this.model = model;
        this.drivers = new HashSet<>();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Manufacturer getManufacturer() {
        return manufacturer;
    }

    public void setManufacturer(Manufacturer manufacturer) {
        this.manufacturer = manufacturer;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public Set<Driver> getDrivers() {
        return drivers;
    }

    public void setDrivers(Set<Driver> drivers) {
        this.drivers = drivers;
    }
}
