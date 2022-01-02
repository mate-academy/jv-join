package mate.jdbc.model;

import java.util.List;

public class Car {
    private Long id;
    private String model;
    private Long manufacturerId;
    private List<Driver> drivers;

    public Car() {

    }

    public Car(String model) {
        this.model = model;
    }

    public Car(Long id, String model) {
        this.id = id;
        this.model = model;
    }

    public Car(Long id, String model, Long manufacturerId) {
        this.id = id;
        this.model = model;
        this.manufacturerId = manufacturerId;
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

    public List<Driver> getDrivers() {
        return drivers;
    }

    public void setDrivers(List<Driver> drivers) {
        this.drivers = drivers;
    }

    public Long getManufacturer_id() {
        return manufacturerId;
    }

    public void setManufacturer_id(Long manufacturerId) {
        this.manufacturerId = manufacturerId;
    }

    @Override
    public String toString() {
        return "Car{"
                + "id=" + id
                + ", model='" + model + '\''
                + ", manufacturerId=" + manufacturerId
                + ", drivers=" + drivers
                + '}';
    }
}
