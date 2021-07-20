package mate.jdbc.model;

import java.util.ArrayList;
import java.util.List;

public class Car {
    private Long id;
    private String title;
    private Manufacturer manufacturer;
    private List<Driver> drivers = new ArrayList<>();

    public Car(String title, Manufacturer manufacturer) {
        this.title = title;
        this.manufacturer = manufacturer;
    }

    public Car() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
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
    public String toString() {
        return "Car{"
                + "id="
                + id
                + ", title='"
                + title
                + '\''
                + ", manufacturer="
                + manufacturer
                + ", drivers="
                + drivers
                + '}';
    }
}
