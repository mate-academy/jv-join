package mate.jdbc.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Car {
    private Long id;
    private String model;
    private Manufacturer manufacturer;
    private List<Driver> drivers;

    public Car() {

    }

    public Car(Long id, String model, Manufacturer manufacturer, List<Driver> drivers) {
        this.id = id;
        this.manufacturer = manufacturer;
        this.model = model;
        this.drivers = drivers;
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
        return new Manufacturer(manufacturer.getId(),
                manufacturer.getName(), manufacturer.getCountry());
    }

    public void setManufacturer(Manufacturer manufacturer) {
        this.manufacturer = manufacturer;
    }

    public List<Driver> getDrivers() {
        List<Driver> copiedDrivers = new ArrayList<>();
        for (Driver driver : drivers) {
            Driver copiedDriver = new Driver(driver.getId(),
                    driver.getName(), driver.getLicenseNumber());
            copiedDrivers.add(copiedDriver);
        }
        return copiedDrivers;
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
                + "id=" + id
                + ", model='" + model + '\''
                + ", manufacturer='" + manufacturer + '\''
                + ", drivers='" + drivers + '\''
                + '}';
    }
}
