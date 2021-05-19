package mate.jdbc.model;

import java.util.List;
import java.util.Objects;

public class Car {

    private Long id;
    private Manufacturer manufacturer;
    private String model;
    private List<Driver> drivers;

    public Car() {
    }

    public Car(Manufacturer manufacturer, String model) {
        this.manufacturer = manufacturer;
        this.model = model;
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
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }

        Car car = (Car) obj;
        return Objects.equals(car.id, id)
                && Objects.equals(car.manufacturer, manufacturer)
                && Objects.equals(car.model, model);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, manufacturer, model);
    }

    @Override
    public String toString() {
        return "Car{"
                + "id=" + id
                + ", producer='" + manufacturer + '\''
                + ", model='" + model + '\''
                + ", drivers=" + drivers
                + '}';
    }
}
