package mate.jdbc.model;

import java.util.List;
import java.util.Objects;

public class Car {
    private Long id;
    private String model;
    private Manufacturer manufacturer;
    private List<Driver> driversList;

    public Car() {
    }

    public Car(Long id, String model, Manufacturer manufacturer, List<Driver> driversList) {
        this.id = id;
        this.model = model;
        this.manufacturer = manufacturer;
        this.driversList = driversList;
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

    public List<Driver> getDriversList() {
        return driversList;
    }

    public void setDriversList(List<Driver> driversList) {
        this.driversList = driversList;
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
                && Objects.equals(driversList, car.driversList);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, model, manufacturer, driversList);
    }

    @Override
    public String toString() {
        return "Car{id=" + id
                + ", mode='" + model + '\''
                + ", manufacturer=" + manufacturer
                + ", driversList=" + driversList + '}';
    }
}
