package mate.jdbc.model;

import java.util.List;

public class Car {
    private Long id;
    private Manufacturer manufacturer;
    private String model;
    private List<Driver> drivers;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Manufacturer getManufacturer() {
        return manufacturer;
    }

    public void setManufacturer(Manufacturer manufacturer) {
        this.manufacturer = manufacturer;
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

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Car{");
        sb.append("id=").append(id);
        sb.append(", manufacturer=").append(manufacturer);
        sb.append(", model='").append(model).append('\'');
        sb.append(", drivers=").append(drivers);
        sb.append('}');
        return sb.toString();
    }
}
