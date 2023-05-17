package mate.jdbc.model;

import java.util.List;

public class Car {
    private Long id;
    private String model;
    private Manufacturer manufacturer;
    private List<Driver> drivers;

    public Car(Long id, String model, Manufacturer manufacturer, List<Driver> drivers) {
        this.id = id;
        this.model = model;
        this.manufacturer = manufacturer;
        this.drivers = drivers;
    }

    public Car(String model, Manufacturer manufacturer, List<Driver> drivers) {
        this.model = model;
        this.manufacturer = manufacturer;
        this.drivers = drivers;
    }

    public Car(Long id, String model, Manufacturer manufacturer) {
        this.id = id;
        this.model = model;
        this.manufacturer = manufacturer;
    }

    public Long getId() {
        return id;
    }

    public Car setId(Long id) {
        this.id = id;
        return this;
    }

    public String getModel() {
        return model;
    }

    public Car setModel(String model) {
        this.model = model;
        return this;
    }

    public Manufacturer getManufacturer() {
        return manufacturer;
    }

    public Car setManufacturer(Manufacturer manufacturer) {
        this.manufacturer = manufacturer;
        return this;
    }

    public List<Driver> getDrivers() {
        return drivers;
    }

    public Car setDrivers(List<Driver> drivers) {
        this.drivers = drivers;
        return this;
    }

    @Override
    public String toString() {
        return "Car{"
                + "id=" + id
                + ", model='" + model + '\''
                + ", manufacturer=" + manufacturer
                + ", drivers=" + drivers
                + '}';
    }
}
