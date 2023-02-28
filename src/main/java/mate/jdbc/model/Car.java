package mate.jdbc.model;

import java.util.List;
import java.util.Objects;

public class Car {
    private Long id;
    private Manufacturer manufacturer;
    private String model;
    private List<Driver> driver;

    public Car(Long id, Manufacturer manufacture, String model, List<Driver> driver) {
        this.id = id;
        this.manufacturer = manufacture;
        this.model = model;
        this.driver = driver;
    }

    public Car(Manufacturer manufacture, String model, List<Driver> driver) {
        this.manufacturer = manufacture;
        this.model = model;
        this.driver = driver;
    }

    public Car(Long id, Manufacturer manufacture, String model) {
        this.id = id;
        this.manufacturer = manufacture;
        this.model = model;
    }

    public List<Driver> getDriver() {
        return driver;
    }

    public void setDriver(List<Driver> driver) {
        this.driver = driver;
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
                && Objects.equals(manufacturer, car.manufacturer)
                && Objects.equals(model, car.model);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, manufacturer, model);
    }

    @Override
    public String toString() {
        return "Car{"
                + "id=" + id
                + ", manufactureID='" + manufacturer + '\''
                + ", model='" + model + '\''
                + ", drivers='" + driver + '\''
                + '}';
    }
}
