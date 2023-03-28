package mate.jdbc;

import java.util.ArrayList;
import java.util.List;
import mate.jdbc.lib.Injector;
import mate.jdbc.model.Car;
import mate.jdbc.model.Driver;
import mate.jdbc.service.CarService;
import mate.jdbc.service.DriverService;
import mate.jdbc.service.ManufacturerService;

public class Main {
    private static final Injector injector = Injector.getInstance("mate.jdbc");
    private static final CarService carService
            = (CarService) injector.getInstance(CarService.class);
    private static final ManufacturerService manufacturerService
            = (ManufacturerService) injector.getInstance(ManufacturerService.class);
    private static final DriverService driverService
            = (DriverService) injector.getInstance(DriverService.class);

    private static final Long MANUFACTURER_ID = 1L;
    private static final Long DRIVER_IVAN_ID = 1L;
    private static final Long DRIVER_ALICE_ID = 2L;

    public static void main(String[] args) {
        Car car1 = new Car();
        car1.setModel("model-1-2");
        car1.setManufacturer(manufacturerService.get(MANUFACTURER_ID));
        Driver firstDriver = driverService.create(new Driver("name", "0000"));
        Driver secondDriver = driverService.create(new Driver("name-2", "1111"));
        List<Driver> drivers = new ArrayList<>();
        drivers.add(firstDriver);
        drivers.add(secondDriver);
        car1.setDrivers(drivers);
        carService.create(car1);

        Car car2 = new Car();
        car2.setModel("model_2-2");
        car2.setManufacturer(manufacturerService.get(MANUFACTURER_ID));
        Driver thirdDriver = driverService.create(new Driver("name-3", "2222"));
        Driver fourthDriver = driverService.create(new Driver("name-4", "3333"));
        drivers.clear();
        drivers.add(thirdDriver);
        drivers.add(fourthDriver);
        car2.setDrivers(drivers);
        carService.create(car2);
        System.out.println("get car1 by id");
        System.out.println(carService.get(car1.getId()));
        System.out.println("get all cars");
        carService.getAll().forEach(System.out::println);

        Driver driverIvan = driverService.get(DRIVER_IVAN_ID);
        Driver driverAlice = driverService.get(DRIVER_ALICE_ID);
        carService.addDriverToCar(driverIvan, car1);
        carService.addDriverToCar(driverAlice, car1);
        System.out.println("get the car after add driver");
        System.out.println(car1);

        carService.removeDriverFromCar(driverAlice, car1);
        System.out.println("get the car after remove driver");
        System.out.println(car1);

        carService.delete(car2.getId());
        System.out.println("get all cars after remove car");
        carService.getAll().forEach(System.out::println);
    }
}
