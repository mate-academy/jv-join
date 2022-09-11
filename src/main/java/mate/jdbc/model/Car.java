package mate.jdbc.model;

import java.util.List;
import java.util.Objects;

public class Car {
    private long id;
    private String model;
    private Manufacturer manufacturer;
    private List<Driver> drivers;

    public Car() {
    }

    public Car(long id, String model, Manufacturer manufacturer, List<Driver> drivers) {
        this.id = id;
        this.model = model;
        this.manufacturer = manufacturer;
        this.drivers = drivers;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
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
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        Car car = (Car) obj;
        return Objects.equals(id, car.id)
                && Objects.equals(model, car.model)
                && Objects.equals(manufacturer, car.manufacturer)
                && driversEquals(car.getDrivers());
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
                + ", manufacturer=" + manufacturer.toString()
                + ", drivers='" + driversToString(drivers) + '\''
                + '}';
    }

    private String driversToString(List<Driver> drivers) {
        StringBuilder stringBuilder = new StringBuilder();
        for (Driver driver : drivers) {
            stringBuilder.append(driver.toString());
        }
        return stringBuilder.toString();
    }

    private boolean driversEquals(List<Driver> drivers) {
        if (this.drivers == drivers) {
            return true;
        }
        if (drivers == null || this.drivers.size() != drivers.size()) {
            return false;
        }
        if (this.drivers.isEmpty() && drivers.isEmpty()) {
            return true;
        }
        boolean result;
        for (Driver thisDriver : this.drivers) {
            result = false;
            for (Driver driver : drivers) {
                if (Objects.equals(thisDriver, driver)) {
                    result = true;
                    break;
                }
            }
            if (!result) {
                return false;
            }
        }
        return true;
    }
}
