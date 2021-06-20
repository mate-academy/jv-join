package mate.jdbc.model;

import java.math.BigDecimal;
import java.util.Objects;

public class Car {
    private long id;
    private String model;
    private String color;
    private BigDecimal price;
    private Driver driver;

    public Car(String model, String color) {
        this.model = model;
        this.color = color;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
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

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public Driver getDriver() {
        return driver;
    }

    public void setDriver(Driver driver) {
        this.driver = driver;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {return true;}
        if (o == null || getClass() != o.getClass()) {return false;}
        Car car = (Car) o;
        return id == car.id && Objects.equals(model, car.model)
                && Objects.equals(color, car.color) && Objects.equals(driver, car.driver);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, model, color, driver);
    }

    @Override
    public String toString() {
        return "Car{"
                + "id=" + id
                + ", model='" + model + '\''
                + ", color='" + color + '\''
                + ", driver=" + driver
                + '}';
    }
}
