package mate.jdbc.model;

import java.util.List;
import java.util.Objects;

public class Car {
    private Long id;
    private String model;
    private Manufacturer manufacturer;
    private List<Driver> drivers;

    public Car(String name, Manufacturer manufacturer) {
        this.model = name;
        this.manufacturer = manufacturer;
    }

    public Car() {

    }

    public Manufacturer getManufacturer() {
        return manufacturer;
    }

    public void setManufacturer(Manufacturer manufacturer) {
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

    public void setDrivers(List<Driver> drivers) {
        this.drivers = drivers;
    }

    public List<Driver> getDrivers() {
        return drivers;
    }

    @Override
    public int hashCode() {
        return Objects.hash(model, manufacturer, id, drivers);
    }

    @Override
    public String toString() {
        return "Car{"
                + " id= " + id
                + ", name= '" + model + '\''
                + ", manufacturer= '" + manufacturer + '\''
                + ", drivers= '" + drivers + '\''
                + "}";
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        Car car = (Car) obj;
        return Objects.equals(id, car.id)
                && Objects.equals(model, car.model)
                && Objects.equals(manufacturer, car.manufacturer)
                && Objects.equals(drivers, car.drivers);
    }
}
