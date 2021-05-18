package mate.jdbc.model;

import java.util.List;

public class Car {
    private Long id;
    private String model;
    private List<Driver> driver;
    private Manufacturer manufacturer;

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

    public List<Driver> getDriver() {
        return driver;
    }

    public void setDriver(List<Driver> driver) {
        this.driver = driver;
    }

    public Manufacturer getManufacturer() {
        return manufacturer;
    }

    public void setManufacturer(Manufacturer manufacturer) {
        this.manufacturer = manufacturer;
    }

    @Override
    public String toString() {
        return "Car: " + "id = " + id + ", model = '" + model + '\''
                + ", driver = " + driver + ", manufacturer = " + manufacturer + '.';
    }
}
