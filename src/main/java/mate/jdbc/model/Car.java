package mate.jdbc.model;

import java.util.List;

public class Car {
    private Long id;
    private String model;
    private Long manufacturerId;
    private List<Driver> drivers;

    public Car(String model, long manufacturerId) {
        this.model = model;
        this.manufacturerId = manufacturerId;
    }

    public Car(String model, long manufacturerId, List<Driver> drivers) {
        this(model, manufacturerId);
        this.drivers = drivers;
    }

    public Car(long id, String model, long manufacturerId) {
        this(model, manufacturerId);
        this.id = id;
    }

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
