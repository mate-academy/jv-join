package mate.jdbc.model;

public class Car {
    private Long id;
    private String model;
    private Manufacturer manufacturer;

    public Car() {
    }

    public Car(String model, Manufacturer manufacturer) {
        this.model = model;
        this.manufacturer = manufacturer;
    }

    public Car(Long id, String model, Manufacturer manufacturer) {
        this(model, manufacturer);
        this.id = id;
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

    public Manufacturer getManufacturer() {
        return manufacturer;
    }

    public void setManufacturer(Manufacturer manufacturer) {
        this.manufacturer = manufacturer;
    }

    @Override
    public String toString() {
        return "Car{id=" + id
                + ", model='" + model + '\''
                + ", manufacturer=" + manufacturer
                + '}';
    }
}
