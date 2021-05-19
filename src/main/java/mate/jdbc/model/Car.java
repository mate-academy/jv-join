package mate.jdbc.model;

import java.util.List;

public class Car {
    private Long id;
    private String model;
    private String registrationNumber;
    private Manufacturer manufacturer;
    private List<Driver> drivers;

    public Car(String model, String registrationNumber, Manufacturer manufacturer) {
        this.model = model;
        this.registrationNumber = registrationNumber;
        this.manufacturer = manufacturer;
    }

    public Car(String model, String registrationNumber,
               Manufacturer manufacturer, List<Driver> drivers) {
        this.model = model;
        this.registrationNumber = registrationNumber;
        this.manufacturer = manufacturer;
        this.drivers = drivers;
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

    public String getRegistrationNumber() {
        return registrationNumber;
    }

    public void setRegistrationNumber(String registrationNumber) {
        this.registrationNumber = registrationNumber;
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
                + "id=" + id
                + ", model='" + model + '\''
                + ", registrationNumber='" + registrationNumber + '\''
                + ", manufacturer=" + manufacturer
                + ", drivers=" + drivers
                + '}';
    }
}
