package mate.jdbc.model;

import java.util.List;
import java.util.Objects;

public class Car {
    private Long id;
    private String model;
    private List<Driver> drivers;
    private Manufacturer manufacturer;

    public Car() {
    }

    public Car(String model, List<Driver> drivers, Manufacturer manufacturer) {
        this.model = model;
        this.drivers = drivers;
        this.manufacturer = manufacturer;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Car car = (Car) o;
        return Objects.equals(id, car.id)
                && Objects.equals(model, car.model)
                && Objects.equals(drivers, car.drivers)
                && Objects.equals(manufacturer, car.manufacturer);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, model, drivers, manufacturer);
    }

    @Override
    public String toString() {
        return "Car{"
                + "id=" + id
                + ", model='" + model
                + '\'' + ", drivers=" + drivers
                + ", manufacturer=" + manufacturer
                + '}';
    }
}
