package mate.jdbc.model;

import java.util.List;

public class Car {
    private Long id;
    private String model;
    private Manufacturer manufacturerId;
    private List<Driver> drivers;

    public Car() {
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
        return manufacturerId;
    }

    public void setManufacturer(Manufacturer manufacturerId) {
        this.manufacturerId = manufacturerId;
    }

    public List<Driver> getDrivers() {
        return drivers;
    }

    public void setDrivers(List<Driver> drivers) {
        this.drivers = drivers;
    }

    @Override
    public String toString() {
        return "Car{"
                + "id=" + id
                + ", model='" + model + '\''
                + ", manufacturerId=" + manufacturerId
                + ", drivers=" + drivers + '}';
    }
}
