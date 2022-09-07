package mate.jdbc.model;

import java.util.List;

public class Car {
    private long id;
    private String model;
    private List<Driver> driversList;
    private Manufacturer manufacturer;

    public Car() {
    }

    public Car(String model, Manufacturer manufacturer, List<Driver> driversList) {
        this.model = model;
        this.manufacturer = manufacturer;
        this.driversList = driversList;
    }

    public Car(long id, String model, Manufacturer manufacturer, List<Driver> driversList) {
        this.id = id;
        this.model = model;
        this.manufacturer = manufacturer;
        this.driversList = driversList;
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

    public List<Driver> getDriversList() {
        return driversList;
    }

    public void setDriversList(List<Driver> driversList) {
        this.driversList = driversList;
    }

    @Override
    public String toString() {
        return "Car{"
                + "id=" + id
                + ", model='" + model + '\''
                + ", manufacturer=" + manufacturer
                + ", driversList=" + driversList
                + '}';
    }
}
