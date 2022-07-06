package mate.jdbc;

import java.util.NoSuchElementException;
import mate.jdbc.lib.Injector;
import mate.jdbc.model.Car;
import mate.jdbc.service.CarService;
import mate.jdbc.service.DriverService;
import mate.jdbc.service.ManufacturerService;

public class TestServices {
    private static final Injector injector = Injector.getInstance("mate.jdbc");

    public static void testCarServices() {
        CarService carService = (CarService) injector.getInstance(CarService.class);
        //getAll() check
        carService.getAll().forEach(System.out::println);

        //getAllByDriver() check
        carService.getAllByDriver(1L).forEach(System.out::println);

        //getById() check
        carService.get(7L);

        //create() check
        Car car = new Car();
        car.setModel("Civic");
        ManufacturerService manufacturerService = (ManufacturerService)
                injector.getInstance(ManufacturerService.class);
        car.setManufacturer(manufacturerService.get(7L)); //Honda
        carService.create(car);

        //update() check
        car = carService.get(3L);
        car.setModel("Passat");
        car.setManufacturer(manufacturerService.get(1L)); //Volkswagen
        carService.update(car);
        System.out.println(carService.get(3L));

        //delete() check
        carService.delete(3L);
        try {
            System.out.println(carService.get(3L));
        } catch (NoSuchElementException e) {
            System.out.println("As expected -> NoSuchElementException after car 3 was deleted");
        }

        //addDriverToCar() check
        System.out.println(carService.get(5L));
        DriverService driverService = (DriverService) injector.getInstance(DriverService.class);
        carService.addDriverToCar(driverService.get(11L), carService.get(5L));
        System.out.println(carService.get(5L));

        //removeDriverFromCar() check
        carService.removeDriverFromCar(driverService.get(11L), carService.get(5L));
        System.out.println(carService.get(5L));
    }
}
