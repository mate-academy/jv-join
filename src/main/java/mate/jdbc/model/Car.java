package mate.jdbc.model;

import java.util.List;

public class Car {
    private Long id;
    private String model;
    private List<Driver> drivers;
    private Manufacturer manufacturer;
    
    public Car() {
    }
    
    public Car(String model, List<Driver> driver, Manufacturer manufacturer) {
        this.model = model;
        this.drivers = driver;
        this.manufacturer = manufacturer;
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
        return "Car{" + "id=" + id + ", model='" + model + '\'' + ", drivers=" + drivers
                + ", manufacturer=" + manufacturer + '}';
    }
}
