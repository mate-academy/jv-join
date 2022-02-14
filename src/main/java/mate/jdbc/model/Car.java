package mate.jdbc.model;

import java.util.List;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Car {
    private Long id;
    private String model;
    private Manufacturer manufacturer;
    private List<Driver> drivers;
}
