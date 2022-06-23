package mate.jdbc.model;

import java.util.ArrayList;
import java.util.List;

public class Car {
    private Long id;
    private String model;
    private Manufacturer manufacturer;
    private List<Driver> drivers;

    public Car() {
    }

    public Car(String model, Manufacturer manufacturer) {
        this.model = model;
        this.manufacturer = manufacturer;
        this.drivers = new ArrayList<>();
    }

    public Car(Long id, String model, Manufacturer manufacturer, List<Driver> drivers) {
        this.id = id;
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
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Car car = (Car) o;

        if (getId() != null ? !getId().equals(car.getId()) : car.getId() != null) {
            return false;
        }
        if (getModel() != null ? !getModel().equals(car.getModel()) : car.getModel() != null) {
            return false;
        }
        if (getManufacturer() != null ? !getManufacturer()
                .equals(car.getManufacturer()) : car.getManufacturer() != null) {
            return false;
        }
        return getDrivers() != null
                ? getDrivers().equals(car.getDrivers()) : car.getDrivers() == null;
    }

    @Override
    public int hashCode() {
        int result = getId() != null ? getId().hashCode() : 0;
        result = 31 * result + (getModel() != null ? getModel().hashCode() : 0);
        result = 31 * result + (getManufacturer() != null ? getManufacturer().hashCode() : 0);
        result = 31 * result + (getDrivers() != null ? getDrivers().hashCode() : 0);
        return result;
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
