package mate.jdbc.model;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Car {
    private Long id;
    private String model;
    private Manufacturer manufacturer;
    private List<Driver> drivers;

    public Car(String model, Manufacturer manufacturer, List<Driver> drivers) {
        this.model = model;
        this.manufacturer = manufacturer;
        this.drivers = drivers;
    }
}
