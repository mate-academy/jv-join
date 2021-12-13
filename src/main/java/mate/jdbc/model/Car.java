package mate.jdbc.model;

import java.util.List;
import java.util.Objects;

public class Car {
    private Long id;
    private Long manufacturedId;
    private String model;
    private List<Driver> drivers;
    private boolean isDeleted;

    public Car(String model, Long manufacturedId) {
        this.model = model;
        this.manufacturedId = manufacturedId;
    }

    public Car() {
    }

    public List<Driver> getDrivers() {
        return drivers;
    }

    public void setDrivers(List<Driver> drivers) {
        this.drivers = drivers;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getManufactured_id() {
        return manufacturedId;
    }

    public void setManufactured_id(Long manufacturedId) {
        this.manufacturedId = manufacturedId;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public boolean isIs_deleted() {
        return isDeleted;
    }

    public void setIs_deleted(boolean isDeleted) {
        this.isDeleted = isDeleted;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Car car = (Car) o; {
            return isDeleted == car.isDeleted && Objects.equals(id, car.id)
                    && Objects.equals(manufacturedId, car.manufacturedId)
                    && Objects.equals(model, car.model)
                    && Objects.equals(drivers, car.drivers);
        }

    }

    @Override
    public int hashCode() {
        return Objects.hash(id, manufacturedId, model, drivers, isDeleted);
    }

    @Override
    public String toString() {
        return "Car{"
                + "id=" + id
                + ", manufactured_id=" + manufacturedId
                + ", model='" + model + '\''
                + ", drivers=" + drivers
                + ", is_deleted=" + isDeleted
                + '}';
    }
}
