package mate.jdbc;

import java.util.Arrays;
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

        ManufacturerService manufacturerService =
                (ManufacturerService) injector.getInstance(ManufacturerService.class);

        List<Manufacturer> manufacturerList = Arrays.asList(
                new Manufacturer("Renault", "France"),
                new Manufacturer("Shevrolet", "USA"),
                new Manufacturer("Audi", "USA"));

        manufacturerList.forEach(manufacturer -> manufacturerService.create(manufacturer));
        manufacturerService.getAll().forEach(System.out::println);
        System.out.println("List of created manufacturers\n");

        Manufacturer manufacturerWhichWasGot = manufacturerService.get(Long.valueOf(76));
        System.out.println(manufacturerWhichWasGot);
        System.out.println("Manufacturer which was got by ID\n");

        manufacturerWhichWasGot.setCountry("Germany");
        System.out.println(manufacturerService.update(manufacturerWhichWasGot));
        System.out.println("Manufacturer which was updated\n");

        manufacturerService.delete(manufacturerWhichWasGot.getId());
        manufacturerService.getAll().forEach(System.out::println);
        System.out.println("List of manufacturers without deleted one\n");

        List<Driver> drivers = Arrays.asList(
                new Driver("Mika Hakkinen", "Fin_1998,1999"),
                new Driver("Kimi Raikkonen", "Fin_2007"),
                new Driver("Jenson Button", "Ger_2009"));

        DriverService driverService =
                (DriverService) injector.getInstance(DriverService.class);

        drivers.forEach(driver -> driverService.create(driver));
        driverService.getAll().forEach(System.out::println);
        System.out.println("List of created drivers\n");

        Driver driverWhichWasGot = driverService.get(8L);
        System.out.println(driverWhichWasGot);
        System.out.println("Driver which was got by ID\n");

        driverWhichWasGot.setLicenseNumber("GB_2009");
        System.out.println(driverService.update(driverWhichWasGot));
        System.out.println("Driver which was updated\n");

        driverService.delete(9L);

        CarService carService =
                (CarService) injector.getInstance(CarService.class);

        List<Car> carsList = Arrays.asList(new Car("Logan", manufacturerService.get(74L)),
                new Car("Aveo", manufacturerService.get(75L)));
        carsList.forEach(car -> carService.create(car));

        Car firstCar = new Car("Logan",
                manufacturerService.get(74L),
                List.of(driverService.get(6L), driverService.get(7L), driverService.get(8L)));
        Car secondCar = new Car("Aveo",
                manufacturerService.get(75L),
                List.of(driverService.get(6L), driverService.get(7L), driverService.get(8L)));
        List<Car> carList = Arrays.asList(firstCar, secondCar);
        carList.forEach(car -> carService.create(car));

        System.out.println(carService.get(17L));
        System.out.println("Car which was got by ID\n");

        carService.delete(5L);

        Car carToUpdate = carService.get(8L);
        carToUpdate.setModel("Aveo");
        carToUpdate.setManufacturer(manufacturerService.get(75L));
        carToUpdate.setDrivers(List.of(driverService.get(6L), driverService.get(8L)));
        System.out.println(carService.update(carToUpdate));
        System.out.println("Updated car");

        carService.getAll().forEach(System.out::println);
        System.out.println("List of all cars");

        carService.getAllByDriver(8L).forEach(System.out::println);
        System.out.println("List of cars which was got by driver ID");

        Driver driverToAdd = driverService.get(7L);
        Car carToAddDriver = carService.get(8L);
        carService.addDriverToCar(driverToAdd, carToAddDriver);
        System.out.println(carService.get(8L));
        System.out.println("Car with added driver");

        Driver driverToRemove = driverService.get(7L);
        Car carToRemoveDriver = carService.get(8L);
        carService.removeDriverFromCar(driverToRemove, carToRemoveDriver);
        System.out.println(carService.get(8L));
        System.out.println("Car with removed driver");
    }
}
