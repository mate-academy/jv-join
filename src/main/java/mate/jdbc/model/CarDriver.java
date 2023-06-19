package mate.jdbc.model;

public class CarDriver {
    private Long carId;
    private Long driverId;

    public CarDriver() {
    }

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
}
