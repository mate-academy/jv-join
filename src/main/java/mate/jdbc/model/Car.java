package mate.jdbc.model;

import java.util.List;
import java.util.Objects;

public class Car {
    private Long id;
    private Manufacturer manufacturer;
    private List<Driver> driverList;
    private String model;

    public Car() {
    }

    public Car(Manufacturer manufacturer, List<Driver> driverList) {
        this.manufacturer = manufacturer;
        this.driverList = driverList;
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

    public List<Driver> getDriverList() {
        return driverList;
    }

    public void setDriverList(List<Driver> driverList) {
        this.driverList = driverList;
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
        return Objects.equals(id, car.id) && Objects.equals(manufacturer, car.manufacturer)
                && Objects.equals(driverList, car.driverList) && Objects.equals(model, car.model);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, manufacturer, driverList, model);
    }

    @Override
    public String toString() {
        return "Car{"
                + "id=" + id
                + ", manufacturer=" + manufacturer
                + ", driverList=" + driverList
                + ", model='" + model + '\'' + '}';
    }
}
