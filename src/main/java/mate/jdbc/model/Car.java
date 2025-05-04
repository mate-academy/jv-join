package mate.jdbc.model;

import java.util.Objects;
import java.util.Set;

public class Car {
    private Long id;
    private String model;
    private Manufacturer manufacturer;
    private Set<Driver> drivers;

    public Car(String model, Manufacturer manufacturer) {
        this.model = model;
        this.manufacturer = manufacturer;
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

    public Manufacturer getManufacturer() {
        return manufacturer;
    }

    public void setManufacturer(Manufacturer manufacturer) {
        this.manufacturer = manufacturer;
    }

    public Set<Driver> getDrivers() {
        return drivers;
    }

    public void setDrivers(Set<Driver> drivers) {
        this.drivers = drivers;
    }

    @Override
    public boolean equals(Object carObject) {
        if (this == carObject) {
            return true;
        }
        if (carObject == null || getClass() != carObject.getClass()) {
            return false;
        }
        Car car = (Car) carObject;
        return Objects.equals(id, car.id)
                && Objects.equals(model, car.model)
                && Objects.equals(manufacturer, car.manufacturer)
                && Objects.equals(drivers, car.drivers);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, model, manufacturer, drivers);
    }

    @Override
    public String toString() {
        return "Car{" + "id=" + id
                + ", model='" + model + '\''
                + ", manufacturer=" + manufacturer
                + ", drivers=" + drivers
                + '}';
    }
}
