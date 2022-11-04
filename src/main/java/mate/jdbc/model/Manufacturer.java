package mate.jdbc.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Manufacturer {
    private Long id;
    private String name;
    private String country;
}
