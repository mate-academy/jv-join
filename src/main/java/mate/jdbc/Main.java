package mate.jdbc;

import java.util.ArrayList;
import java.util.List;
import mate.jdbc.lib.Injector;
import mate.jdbc.model.Car;
import mate.jdbc.model.Driver;
import mate.jdbc.model.Manufacturer;
import mate.jdbc.service.CarService;

public class Main {
    private static final Injector injector = Injector.getInstance("mate.jdbc");

    public static void main(String[] args) {
        List<Driver> driverList = new ArrayList<>();
        driverList.add(new Driver(3L, "updatedDriver4141", "340040"));
        driverList.add(new Driver(1L, "updatedDriver2", "747123"));
        Manufacturer manufacturer = new Manufacturer(1L,
                "updatedNameManufacturer", "updatedCountry");
        Car car = new Car();
        car.setModel("updatedCar");
        car.setManufacturer(manufacturer);
        car.setDrivers(driverList);
        CarService carService = (CarService) injector.getInstance(CarService.class);
        carService.create(car);
        carService.update(car);
        carService.get(2L);
        carService.delete(10L);
        Driver driver = new Driver(4L, "DriverToCar", "3813048");
        carService.addDriverToCar(driver, car);
        carService.getAllByDriver(3L);
        car.setId(2L);
        carService.removeDriverFromCar(driverList.get(0), car);
        carService.get(1L);
        carService.getAll();
    }
}
