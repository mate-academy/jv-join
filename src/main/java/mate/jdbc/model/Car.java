package mate.jdbc.model;

import java.util.Objects;

public class Car {
    private long id;
    private String model;
    private Manufacturer manufacturer;

    public Car(long id, String model, Manufacturer manufacturer) {
        this.id = id;
        this.model = model;
        this.manufacturer = manufacturer;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Car car = (Car) o;
        return id == car.id && Objects.equals(model, car.model) && Objects.equals(manufacturer, car.manufacturer);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, model, manufacturer);
    }


    @Override
    public String toString() {
        return "Car{"
                + "id=" + id + '\''
                + ", model='" + model + '\''
                + ", manufacturer=" + manufacturer + '\''
                + '}';
    }
}
