package mate.jdbc.model;

import java.util.List;

public class Car {
    private Long id;
    private String name;
    private Manufacturer manufacturer;
    private List<Driver> driverList;

    public Car() {
    }

    public Car(String name, Manufacturer manufacturer, List<Driver> driverList) {
        this.name = name;
        this.manufacturer = manufacturer;
        this.driverList = driverList;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Manufacturer getManufacturer() {
        return manufacturer;
    }

    public void setManufacturer(Manufacturer manufacturer) {
        this.manufacturer = manufacturer;
    }

    public List<Driver> getDriverList() {
        return driverList;
    }

    public void setDriverList(List<Driver> driverList) {
        this.driverList = driverList;
    }

    public void removeDriverFromList(Driver driver) {
        driverList.remove(driver);
    }

    public void addDriverFromList(Driver driver) {
        driverList.add(driver);
    }

    @Override
    public String toString() {
        return "Car {"
                + " \t\tid=" + id
                + ", \n\t\tname='" + name + '\''
                + ", \n\t\t" + manufacturer
                + ", \n\t\tdriverList=" + driverList
                + "\n}";
    }
}
