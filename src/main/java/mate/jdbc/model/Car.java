package mate.jdbc.model;

import java.util.List;

public class Car {
    private String model;
    private Manufacturer manufacturer;
    private long id;
    private List<Driver> drivers;

    public Car() {

    }

    public Car(String model, Manufacturer manufacturer, long id, List<Driver> drivers) {
        this.model = model;
        this.manufacturer = manufacturer;
        this.id = id;
        this.drivers = drivers;
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

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public List<Driver> getDrivers() {
        return drivers;
    }

    public void setDrivers(List<Driver> drivers) {
        this.drivers = drivers;
    }

    @Override
    public String toString() {
        return "Car{" + "model='"
                + model + '\'' + ", manufacturer="
                + manufacturer + ", id=" + id
                + ", drivers=" + drivers + '}';
    }
}
