package mate.jdbc.model;

import java.util.List;
import java.util.Objects;

public class Car {
    private Long id;
    private String model;
    private Manufacturer manufacturer;
    private List<Driver> drivers;

    public Car() {
    }

    public Car(String model, Manufacturer manufacturer, List<Driver> drivers) {
        this.model = model;
        this.manufacturer = manufacturer;
        this.drivers = drivers;
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getModel() {
        return this.model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public Manufacturer getManufacturer() {
        return this.manufacturer;
    }

    public void setManufacturer(Manufacturer manufacturer) {
        this.manufacturer = manufacturer;
    }

    public List<Driver> getDrivers() {
        return this.drivers;
    }

    public void setDrivers(List<Driver> drivers) {
        this.drivers = drivers;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof Car)) {
            return false;
        }
        Car car = (Car) o;
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
        return "Car{" 
                + " id='" + getId() 
                + "', model='" + getModel() 
                + "', manufacturer='" + getManufacturer() 
                + "', drivers='" + getDrivers() 
                + "'}";
    }
}
