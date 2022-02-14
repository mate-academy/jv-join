package mate.jdbc.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Driver {
    private Long id;
    private String name;
    private String licenseNumber;
}
