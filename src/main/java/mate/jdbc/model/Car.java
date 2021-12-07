package mate.jdbc.model;

import java.util.List;

public class Car {
    private List<Driver> drivers;
    private Long id;
    private Manufacturer manufacturer;
    private String model;

    public List<Driver> getDrivers() {
        return drivers;
    }

    public void setDrivers(List<Driver> drivers) {
        this.drivers = drivers;
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

    @Override
    public String toString() {
        return "Car{"
                + "drivers=" + drivers
                + ", id=" + id
                + ", manufacturer=" + manufacturer
                + ", model='" + model + '\''
                + '}';
    }
}
