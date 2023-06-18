package mate.jdbc.model;

import java.util.Objects;

public class Car {
    private Long id;
    private Long manufacturerId;
    private String model;

    public Car(Long id, String model, Long manufacturerId) {
        this.id = id;
        this.manufacturerId = manufacturerId;
        this.model = model;
    }

    public Long getId() {
        return id;
    }

    public Long getManufacturerId() {
        return manufacturerId;
    }

    public String getModel() {
        return model;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setManufacturerId(Long manufacturerId) {
        this.manufacturerId = manufacturerId;
    }

    public void setModel(String model) {
        this.model = model;
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
        return getId().equals(car.getId())
                && getManufacturerId().equals(car.getManufacturerId())
                && getModel().equals(car.getModel());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getManufacturerId(), getModel());
    }

    @Override
    public String toString() {
        return "Car{" + "id=" + id
                + ", manufacturerId=" + manufacturerId
                + ", model='" + model + '\''
                + '}';
    }
}
