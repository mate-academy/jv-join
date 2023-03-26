package mate.jdbc;

import java.util.List;
import mate.jdbc.lib.Injector;
import mate.jdbc.model.Car;
import mate.jdbc.model.Driver;
import mate.jdbc.model.Manufacturer;
import mate.jdbc.service.CarService;
import mate.jdbc.service.DriverService;
import mate.jdbc.service.ManufacturerService;

public class Main {
    private static final Injector injector = Injector.getInstance("mate.jdbc");
    private static final CarService carService = (CarService)
            injector.getInstance(CarService.class);
    private static final ManufacturerService manufacturerService = (ManufacturerService)
            injector.getInstance(ManufacturerService.class);
    private static final DriverService driverService = (DriverService)
            injector.getInstance(DriverService.class);

    public static void main(String[] args) {
        // creating manufactures
        List<Manufacturer> manufacturerList =
                List.of(new Manufacturer("ZHIGUNI", "USSR"),
                        new Manufacturer("TOYOTA", "Japane"),
                        new Manufacturer("MERCEDES", "Germany"));
        for (Manufacturer manufacturer : manufacturerList) {
            System.out.println(manufacturerService.create(manufacturer));
        }
        //creating drivers
        List<Driver> drivers =
                List.of(new Driver("Andrew", "1"),
                        new Driver("Sasha", "123"),
                        new Driver("Lyosha", "1234"));
        for (Driver driver : drivers) {
            System.out.println(driverService.create(driver));
        }
        System.out.println("creating car");
        Car car = new Car("Land Rover", manufacturerService.get(1L));
        car.setDrivers(List.of(driverService.get(1L)));
        System.out.println(carService.create(car));

        System.out.println("get car from database");
        System.out.println(carService.get(1L));

        System.out.println("get all cars from databasee");
        System.out.println(carService.getAll());

        System.out.println("update car");
        System.out.println(carService.update(new Car(1L,"2109",
                manufacturerService.get(2L))));
        System.out.println(carService.get(1L));

        System.out.println("delete car");
        carService.delete(1L);
        car.setId(2);
        carService.create(car);

        System.out.println("add driver to car");
        carService.addDriverToCar(driverService.get(2L), carService.get(1L));
        System.out.println(carService.get(1L));

        System.out.println("delete driver from car");
        carService.removeDriverFromCar(driverService.get(2L), carService.get(1L));
        System.out.println(carService.get(1L));
    }
}
