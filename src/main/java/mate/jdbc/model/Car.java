package mate.jdbc.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Car {
    private Long id;
    private Manufacturer manufacturer;
    private String model;
    private List<Driver> drivers = new ArrayList<Driver>();

    public Car() {
    }

    public Car(Manufacturer manufacturer,String model,List<Driver> drivers) {
        this.model = model;
        this.manufacturer = manufacturer;
        this.drivers = drivers;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Car car = (Car) o;
        return Objects.equals(id,car.id)
                && Objects.equals(model,car.model)
                && Objects.equals(manufacturer,car.manufacturer)
                && Objects.equals(drivers,car.drivers);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id,model,manufacturer,drivers);
    }

    @Override
    public String toString() {
        return "Car{"
                + "id: " + id
                + ", model: '" + model + '\''
                + ", manufacturer: '" + manufacturer.toString() + '\''
                + ",drivers: " + drivers.toString()
                + '}';
    }
}
