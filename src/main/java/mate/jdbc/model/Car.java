package mate.jdbc.model;

import java.util.Objects;

public class Car {
    private Long id;
    private String model;
    private Long manufacturerId;

    public Car(Long id, String model, Long manufacturerId) {
        this.id = id;
        this.model = model;
        this.manufacturerId = manufacturerId;
    }

    public Car(String model, Long manufacturerId) {
        this.model = model;
        this.manufacturerId = manufacturerId;
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

    public Long getManufacturerId() {
        return manufacturerId;
    }

    public void setManufacturerId(Long manufacturerId) {
        this.manufacturerId = manufacturerId;
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
        return Objects.equals(id, car.id) && Objects.equals(model, car.model)
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
                + ", manufacturerId=" + manufacturerId
                + '}';
    }
}
