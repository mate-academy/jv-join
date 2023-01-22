package mate.jdbc;

import java.util.ArrayList;
import java.util.List;
import mate.jdbc.lib.Injector;
import mate.jdbc.model.Car;
import mate.jdbc.model.Driver;
import mate.jdbc.model.Manufacturer;
import mate.jdbc.service.CarService;
import mate.jdbc.service.DriverService;
import mate.jdbc.service.ManufacturerService;

public class Main {
    private static Injector injector = Injector.getInstance("mate.jdbc");

    public static void main(String[] args) {
        CarService carService = (CarService) injector.getInstance(CarService.class);

        System.out.println(carService.get(30L));
        carService.delete(2L);

        List<Car> all = carService.getAll();
        for (Car car : all) {
            System.out.println(car);
        }

        Driver driverEmily = new Driver();
        driverEmily.setId(6L);
        driverEmily.setName("Emily");
        driverEmily.setLicenseNumber("1225521");

        DriverService driverService = (DriverService) injector.getInstance(DriverService.class);
        System.out.println(driverService.create(driverEmily));

        Driver driverLili = new Driver();
        driverLili.setId(7L);
        driverLili.setName("Lili");
        driverLili.setLicenseNumber("8452367");
        System.out.println(driverService.create(driverLili));

        List<Driver> drivers = new ArrayList<>();
        drivers.add(driverService.get(6L));
        drivers.add(driverService.get(7L));

        ManufacturerService manufacturerService =
                (ManufacturerService) injector.getInstance(ManufacturerService.class);

        Manufacturer manufacturer = manufacturerService.get(14L);
        System.out.println(manufacturer);

        Car car = new Car();
        car.setId(2L);
        car.setModel("A3");
        car.setManufacturer(manufacturer);
        car.setDrivers(drivers);
        Car update = carService.update(car);
        System.out.println(update);

        List<Car> allByDriver = carService.getAllByDriver(5L);
        for (Car carAllByDriver : allByDriver) {
            System.out.println(carAllByDriver);
        }

        Car carAddDriver = carService.get(2L);
        Driver driver = driverService.get(1L);
        carService.removeDriverFromCar(driver, carAddDriver);

        Car carRemoveDriver = carService.get(2L);
        Driver driverRemoveDriver = driverService.get(1L);
        carService.removeDriverFromCar(driverRemoveDriver, carRemoveDriver);
        carService.removeDriverFromCar(driver, carAddDriver);
    }
}
