package mate.jdbc.model;

import java.util.List;
import java.util.Objects;

public class Car {
    private Long id;
    private String model;
    private Manufacturer manufacturer;
    private List<Driver> driver;

    public Car() {

    }

    public Car(String model, Manufacturer manufacturer, List<Driver> driver) {
        this.model = model;
        this.manufacturer = manufacturer;
        this.driver = driver;
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

    public List<Driver> getDriver() {
        return driver;
    }

    public void setDriver(List<Driver> driver) {
        this.driver = driver;
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
        return Objects.equals(id, car.id) && Objects.equals(model, car.model)
                && Objects.equals(manufacturer, car.manufacturer)
                && Objects.equals(driver, car.driver);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, model, manufacturer, driver);
    }

    @Override
    public String toString() {
        return "Car { "
                + "id = " + id
                + ", model = " + model + '\''
                + ", manufacturer = " + manufacturer
                + ", driver = " + driver + '}';
    }
}
