package mate.jdbc.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Car {
    private Long id;
    private Long manufacturerId;
    private String model;
    private Manufacturer manufacturer;
    private List<Driver> drivers = new ArrayList<>();

    public Car(Long id, Long manufacturerId, String model, Manufacturer manufacturer) {
        this.id = id;
        this.manufacturerId = manufacturerId;
        this.model = model;
        this.manufacturer = manufacturer;
    }

    public Car(Long id, Long manufacturerId, String model) {
        this.id = id;
        this.manufacturerId = manufacturerId;
        this.model = model;
    }

    public Car(Long manufacturerId, String model) {
        this.manufacturerId = manufacturerId;
        this.model = model;
    }

    public Car() {
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

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getManufacturerId() {
        return manufacturerId;
    }

    public void setManufacturerId(Long manufacturerId) {
        this.manufacturerId = manufacturerId;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Car car = (Car) o;
        return Objects.equals(id, car.id)
                && Objects.equals(manufacturerId, car.manufacturerId)
                && Objects.equals(model, car.model)
                && Objects.equals(manufacturer, car.manufacturer);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, manufacturerId, model, manufacturer);
    }

    @Override
    public String toString() {
        return "Car{"
                + "id=" + id
                + ", manufacturerId=" + manufacturerId
                + ", model='" + model + '\''
                + ", manufacturer=" + manufacturer
                + ", drivers=" + drivers
                + '}';
    }
}
