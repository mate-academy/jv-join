package mate.jdbc.model;

import java.util.List;
import java.util.Objects;

public class Car {
    private Long id;
    private String carModel;
    private List<Driver> drivers;
    private Manufacturer manufacturer;

    public Car() {

    }

    public Car(String carModel, List<Driver> drivers, Manufacturer manufacturer) {
        this.carModel = carModel;
        this.drivers = drivers;
        this.manufacturer = manufacturer;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCarModel() {
        return carModel;
    }

    public void setCarModel(String carModel) {
        this.carModel = carModel;
    }

    public List<Driver> getDrivers() {
        return drivers;
    }

    public void setDrivers(List<Driver> drivers) {
        this.drivers = drivers;
    }

    public Manufacturer getManufacturer() {
        return manufacturer;
    }

    public void setManufacturer(Manufacturer manufacturer) {
        this.manufacturer = manufacturer;
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
        return Objects.equals(id, car.id) && Objects.equals(carModel, car.carModel)
                && Objects.equals(drivers, car.drivers) && Objects.equals(
                manufacturer, car.manufacturer);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, carModel, drivers, manufacturer);
    }

    @Override
    public String toString() {
        return "Car{"
                + "id=" + id
                + ", carModel='" + carModel
                + '\'' + ", drivers=" + drivers
                + ", manufacturer=" + manufacturer + '}';
    }
}
