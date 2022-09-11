package mate.jdbc.model;

public class CarDriver {
    private long carId;
    private long driverId;

    public CarDriver(long carId, long driverId) {
        this.carId = carId;
        this.driverId = driverId;
    }

    public long getCarId() {
        return carId;
    }

    public void setCarId(long carId) {
        this.carId = carId;
    }

    public long getDriverId() {
        return driverId;
    }

    public void setDriverId(long driverId) {
        this.driverId = driverId;
    }

    @Override
    public String toString() {
        return "CarDriver{"
                + "carId=" + carId
                + ", driverId=" + driverId + '}';
    }
}
