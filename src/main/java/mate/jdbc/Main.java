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
    private static final Injector injector = Injector.getInstance("mate.jdbc");

    public static void main(String[] args) {
        CarService carService = (CarService) injector.getInstance(CarService.class);

        System.out.println("The result of the method getAll() "
                + "from CarServiceImpl Class: ");
        carService.getAll().stream().forEach(System.out::println);

        Car receivedCar = carService.get(3L);
        System.out.println("The result of the method get() from CarServiceImpl Class: "
                + receivedCar);

        DriverService driverService = (DriverService) injector.getInstance(DriverService.class);
        Driver anduin = new Driver();
        anduin.setName("Anduin");
        anduin.setLicenseNumber("12346895");
        driverService.create(anduin);
        List<Driver> drivers = new ArrayList<>();
        drivers.add(anduin);

        Manufacturer manufacturer = new Manufacturer();
        manufacturer.setCountry("Germany");
        manufacturer.setName("Volkswagen AG");
        ManufacturerService manufacturerService =
                (ManufacturerService) injector.getInstance(ManufacturerService.class);
        Car car = new Car();
        car.setModel("Golf");
        car.setDrivers(drivers);
        car.setManufacturer(manufacturerService.get(4L));
        Car golf = carService.create(car);
        System.out.println("The result of the method create() from CarServiceImpl Class: "
                + golf);

        Driver sylvanas = new Driver();
        sylvanas.setName("Sylvanas");
        sylvanas.setLicenseNumber("12342295");
        driverService.create(sylvanas);
        carService.addDriverToCar(sylvanas, golf);
        System.out.println("The method addDriverToCar() from CarServiceImpl class was called. "
                + "The result of the method: ");
        carService.getAll().stream().forEach(System.out::println);

        carService.removeDriverFromCar(sylvanas, golf);
        System.out.println("The method removeDriverFromCar from CarServiceImpl class was called. "
                + "The result of the method: ");
        carService.getAll().stream().forEach(System.out::println);

        golf.setModel("Golf-531");
        Car updatedGolf = carService.update(car);
        System.out.println("The result of the method update() from CarServiceImpl Class: "
                + updatedGolf);

        boolean deletedCar = carService.delete(car.getId());
        System.out.println("The result of the method delete() from CarServiceImpl Class: "
                + deletedCar);

        List<Car> allCarsByDriver = carService.getAllByDriver(1L);
        System.out.println("The result of the method getAllByDriver() from CarServiceImpl Class: "
                + allCarsByDriver);
    }
}
