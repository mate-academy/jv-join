package mate.jdbc.model;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Car {
    private Long id;
    private String model;
    private String color;
    private Manufacturer manufacturer;
    private List<Driver> drivers;

    public Car(String model, String color, Manufacturer manufacturer) {
        this.model = model;
        this.color = color;
        this.manufacturer = manufacturer;
        this.drivers = new ArrayList<>();
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

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
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
        String allDrivers = drivers.stream()
                .map(driver -> "Name: " + driver.getName() + ", license_number: "
                        + driver.getLicenseNumber())
                .collect(Collectors.joining(", "));
        return "Car { " + "id = " + id
                + ", model ='" + model + '\''
                + ", color = '" + color + '\''
                + ", manufacturer = " + manufacturer.getName() + ", "
                + manufacturer.getCountry() + ", " + "drivers = "
                + (allDrivers.length() > 0 ? allDrivers : "No drivers assigned") + '}';
    }
}
