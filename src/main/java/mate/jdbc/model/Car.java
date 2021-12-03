package mate.jdbc.model;

import java.util.List;

public class Car {
    private Long id;
    private List<Driver> drivers;
    private Manufacturer manufacturer;
    private String model;

    public Long getId() {
        return id;
    }

    public String getModel() {
        return model;
    }

    public void setId(Long id) {
        this.id = id;
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

    public Manufacturer getManufacturer() {
        return manufacturer;
    }

    public void setManufacturer(Manufacturer manufacturer) {
        this.manufacturer = manufacturer;
    }

    @Override
    public String toString() {
        return "Car{"
                + "id=" + id
                + ", drivers=" + drivers
                + ", manufacturer=" + manufacturer
                + ", model='" + model + '\''
                + '}';
    }
}
