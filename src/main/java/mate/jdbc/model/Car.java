package mate.jdbc.model;

import java.util.List;

public class Car {
    private Long id;
    private int year;
    private String numberPlate;
    private List<Driver> drivers;
    private Manufacturer manufacturer;
    
    public Car() {
    }
    
    public Car(int year, String numberPlate, List<Driver> driver, Manufacturer manufacturer) {
        this.year = year;
        this.numberPlate = numberPlate;
        this.drivers = driver;
        this.manufacturer = manufacturer;
    }
    
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public int getYear() {
        return year;
    }
    
    public void setYear(int year) {
        this.year = year;
    }
    
    public String getNumberPlate() {
        return numberPlate;
    }
    
    public void setNumberPlate(String numberPlate) {
        this.numberPlate = numberPlate;
    }
    
    public List<Driver> getDrivers() {
        return drivers;
    }
    
    public void setDrivers(List<Driver> drivers) {
        this.drivers = drivers;
    }
    
    public Manufacturer getManufacturer() {
        return manufacturer;
    }
    
    public void setManufacturer(Manufacturer manufacturer) {
        this.manufacturer = manufacturer;
    }
    
    @Override
    public String toString() {
        return "Car{" + "id=" + id
                + ", year=" + year
                + ", numberPlate='" + numberPlate + '\''
                + ", driver=" + drivers
                + ", manufacturer=" + manufacturer + '}';
    }
}
