package mate.jdbc.model;

import java.util.List;
import java.util.Objects;

public class Car {
    private Long id;
    private Manufacturer manufacturer;
    private String model;
    private List<Driver> drivers;

    public Car(Manufacturer manufacturer, String model, List<Driver> drivers) {
        this.manufacturer = manufacturer;
        this.model = model;
        this.drivers = drivers;
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
        return Objects.equals(id, car.id) && Objects.equals(model, car.model)
                && Objects.equals(manufacturer, car.manufacturer)
                && Objects.equals(drivers, car.drivers);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, model, manufacturer, drivers);
    }

    @Override
    public String toString() {
        return "Car{" + '\n'
                + "id=" + id + '\n'
                + ", model='" + model + '\n'
                + ", manufacturer=" + manufacturer + '\n'
                + ", drivers=" + drivers + '\n'
                + '}';
    }
}
