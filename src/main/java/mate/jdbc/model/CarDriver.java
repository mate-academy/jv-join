package mate.jdbc.model;

import java.util.Objects;

public class CarDriver {
    private Long carId;
    private Long driverId;

    public CarDriver(Long carId, Long driverId) {
        this.carId = carId;
        this.driverId = driverId;
    }

    public Long getCarId() {
        return carId;
    }

    public void setCarId(Long carId) {
        this.carId = carId;
    }

    public Long getDriverId() {
        return driverId;
    }

    public void setDriverId(Long driverId) {
        this.driverId = driverId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        CarDriver carDriver = (CarDriver) o;
        return Objects.equals(carId, carDriver.carId) && Objects
                .equals(driverId, carDriver.driverId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(carId, driverId);
    }

    @Override
    public String toString() {
        return "CarDriver{"
                + "carId=" + carId
                + ", driverId=" + driverId
                + '}';
    }
}
