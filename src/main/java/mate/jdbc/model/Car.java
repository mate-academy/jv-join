package mate.jdbc.model;

import java.util.List;
import java.util.Objects;

public class Car {
    private String model;
    private Manufacturer manufacturer;
    private List<Driver> drivers;
    private Long id;

    public Car(){
    }

    public Car(String model, Manufacturer manufacturer, List<Driver> drivers) {
        this.model = model;
        this.manufacturer = manufacturer;
        this.drivers = drivers;
    }

    public Car(String model, Manufacturer manufacturer, List<Driver> drivers, Long id) {
        this(model, manufacturer, drivers);
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

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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
        return Objects.equals(model, car.model)
                && Objects.equals(manufacturer, car.manufacturer)
                && Objects.equals(drivers, car.drivers) && Objects.equals(id, car.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(model, manufacturer, drivers, id);
    }

    @Override
    public String toString() {
        return "Car{"
                + "model='" + model + '\''
                + ", manufacturer=" + manufacturer
                + ", drivers=" + drivers + ", id=" + id + '}';
    }
}
