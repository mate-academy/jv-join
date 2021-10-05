package mate.jdbc.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Car {
    private long id;
    private String model;
    private Manufacturer manufacturer;
    private List<Driver> drivers;

    public Car() {
    }

    public Car(String model, Manufacturer manufacturer) {
        this.model = model;
        this.manufacturer = manufacturer;
        this.drivers = new ArrayList<>();
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
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

    public List<Driver> getDrivers() {
        return drivers;
    }

    public void setDrivers(List<Driver> drivers) {
        this.drivers = drivers;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Car)) {
            return false;
        }
        Car car = (Car) o;
        return getId() == car.getId()
                    && getModel().equals(car.getModel())
                    && getManufacturer().equals(car.getManufacturer())
                    && getDrivers().equals(car.getDrivers());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getModel(), getManufacturer());
    }

    @Override
    public String toString() {
        return "Car{" + "id=" + id + ", model='"
                    + model + '\'' + ", manufacturer="
                    + manufacturer.toString() + ", drivers="
                    + drivers + '}';
    }
}
