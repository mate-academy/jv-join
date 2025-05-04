package mate.jdbc.model;

import java.util.ArrayList;
import java.util.List;

public class Car {
    private Long id;
    private Manufacturer manufacturer;
    private List<Driver> drivers;
    private String model;

    public Car(Long id, String model, Manufacturer manufacturer, List<Driver> drivers) {
        this.id = id;
        this.manufacturer = manufacturer;
        this.model = model;
    }

    public Car(Long id, String model, Manufacturer manufacturer) {
        this.id = id;
        this.model = model;
        this.manufacturer = manufacturer;
    }

    public Car() {
        this.drivers = new ArrayList<>();
    }

    public Long getId() {
        return id;
    }

    public Manufacturer getManufacturer() {
        return manufacturer;
    }

    public String getModel() {
        return model;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setManufacturer(Manufacturer manufacturer) {
        this.manufacturer = manufacturer;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public List<Driver> getDrivers() {
        return drivers;
    }

    public void setDrivers(List<Driver> drivers) {
        this.drivers = drivers;
    }

    @Override
    public String toString() {
        return "Car{" + "id=" + id
                + ", manufacturer=" + manufacturer
                + ", model='" + model + '\''
                + ", drivers=" + drivers
                + '}';
    }
}
