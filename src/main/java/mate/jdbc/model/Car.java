package mate.jdbc.model;

import java.util.ArrayList;
import java.util.List;

public class Car {
    private Long id;
    private String model;
    private Manufacturer manufacturer;
    private List<Driver> drivers = new ArrayList<>();

    public void setId(Long id) {
        this.id = id;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public void setManufacturer(Manufacturer manufacturer) {
        this.manufacturer = manufacturer;
    }

    public void setDrivers(List<Driver> drivers) {
        this.drivers = drivers;
    }

    public Long getId() {
        return id;
    }

    public String getModel() {
        return model;
    }

    public Manufacturer getManufacturer() {
        return manufacturer;
    }

    public List<Driver> getDrivers() {
        return drivers;
    }

    @Override
    public String toString() {
        return String.format("Car{id=%d, model='%s', manufacturer=%s, drivers=%s}",
                id, model, manufacturer, drivers);
    }
}
