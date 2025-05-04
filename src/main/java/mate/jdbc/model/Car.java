package mate.jdbc.model;

import java.util.List;
import java.util.Objects;

public class Car {
    private Long id;
    private String model;
    private Manufacturer manufacturer;
    private List<Driver> driverList;

    public Car() {
    }

    public Car(Long id, String model, Manufacturer manufacturer, List<Driver> driverList) {
        this.id = id;
        this.model = model;
        this.manufacturer = manufacturer;
        this.driverList = driverList;
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

    public List<Driver> getDriverList() {
        return driverList;
    }

    public void setDriverList(List<Driver> driverList) {
        this.driverList = driverList;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Car)) {
            return false;
        }

        Car car = (Car) o;
        return Objects.equals(id, car.id)
            && Objects.equals(model, car.model)
            && Objects.equals(manufacturer, car.manufacturer)
            && Objects.equals(driverList, car.driverList);
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (model != null ? model.hashCode() : 0);
        result = 31 * result + (manufacturer != null ? manufacturer.hashCode() : 0);
        result = 31 * result + (driverList != null ? driverList.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Car{"
                + "id=" + id + ", model='" + model + '\''
                + ", manufacturer=" + manufacturer
                + ", driverList=" + driverList + '}';
    }
}
