package mate.jdbc.model;

import java.util.Objects;

public class Car {
    private Long id;
    private Long manufacturerId;
    private String model;

    public Car() {
    }

    public Car(Long manufacturerId, String model) {
        this.model = model;
        this.manufacturerId = manufacturerId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getManufacturerId() {
        return manufacturerId;
    }

    public void setManufacturerId(Long manufacturerId) {
        this.manufacturerId = manufacturerId;
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
                && Objects.equals(model, car.model)
                && Objects.equals(manufacturerId, car.manufacturerId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, model, manufacturerId);
    }

    @Override
    public String toString() {
        return "Car{"
                + "id=" + id
                + ", model='" + model + '\''
                + ", manufacturer id='" + manufacturerId + '\''
                + '}';
    }
}
