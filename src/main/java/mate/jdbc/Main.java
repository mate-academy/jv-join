package mate.jdbc;

import java.util.List;
import mate.jdbc.lib.Injector;
import mate.jdbc.model.Car;
import mate.jdbc.model.Driver;
import mate.jdbc.service.CarService;
import mate.jdbc.service.DriverService;
import mate.jdbc.service.ManufacturerService;

public class Main {
    private static final Injector injector = Injector.getInstance("mate.jdbc");

    public static void main(String[] args) {

        DriverService driverService =
                 (DriverService) injector.getInstance(DriverService.class);

        ManufacturerService manufacturerService =
                (ManufacturerService) injector.getInstance(ManufacturerService.class);

        List<Driver> car1Drivers = List.of(driverService.get(15L), driverService.get(17L));

        Car car1 = new Car();
        car1.setModel("ToyotaAcura");
        car1.setManufacturer(manufacturerService.get(17L));
        car1.setDrivers(car1Drivers);

        List<Driver> car2Drivers = List.of(driverService.get(15L),
                driverService.get(16L), driverService.get(17L));

        Car car2 = new Car();
        car2.setModel("DaewooLanos");
        car2.setManufacturer(manufacturerService.get(16L));
        car2.setDrivers(car2Drivers);

        List<Driver> car3Drivers = List.of(driverService.get(17L));

        Car car3 = new Car();
        car3.setModel("Zaporozec");
        car3.setManufacturer(manufacturerService.get(15L));
        car3.setDrivers(car3Drivers);

        CarService carService =
                (CarService) injector.getInstance(CarService.class);
        carService.create(car1);
        carService.create(car2);
        carService.create(car3);

        System.out.println(carService.get(1L));
        carService.getAll().forEach(System.out::println);
        carService.getAllByDriver(16L).forEach(System.out::println);
        carService.delete(4L);

        List<Driver> car3DriversNew = List.of(driverService.get(15L),
                driverService.get(16L), driverService.get(17L));

        Car car4 = new Car();
        car4.setModel("Zaporozec+++");
        car4.setManufacturer(manufacturerService.get(16L));
        car4.setDrivers(car3DriversNew);
        car4.setId(3L);

        carService.get(3L);
        System.out.println(carService.update(car4));

        System.out.println(carService.get(3L));
        carService.addDriverToCar(driverService.get(16L), carService.get(3L));
        System.out.println(carService.get(3L));
        carService.removeDriverFromCar(driverService.get(16L), carService.get(3L));
        System.out.println(carService.get(3L));
    }
}
