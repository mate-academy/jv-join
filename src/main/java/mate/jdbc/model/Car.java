package mate.jdbc.model;

public class Car {
    Long id;
    Manufacturer manufacturer;
    String model;

    public Car() {}

    public Car(Manufacturer manufacturer, String model) {
        this.manufacturer = manufacturer;
        this.model = model;
    }

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

    @Override
    public String toString() {
        return "Car{"
                + "id=" + id
                + ", manufacturer=" + manufacturer
                + ", model='" + model + '\''
                + '}';
    }
}
