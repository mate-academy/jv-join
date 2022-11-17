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

    public static void main(String[] args) {
        ManufacturerService manufacturerService = (ManufacturerService)
                injector.getInstance(ManufacturerService.class);
        Manufacturer manufacturerVW = new Manufacturer("Volkswagen", "Germany");
        Manufacturer manufacturerPorsche = new Manufacturer("Porsche", "Germany");
        manufacturerService.create(manufacturerVW);
        manufacturerService.create(manufacturerPorsche);

        DriverService driverService = (DriverService)
                injector.getInstance(DriverService.class);
        Driver driver1 = new Driver("Bob", "11111");
        Driver driver2 = new Driver("Alice", "22222");
        Driver driver3 = new Driver("Barry", "33333");
        Driver driver4 = new Driver("Elton", "44444");
        Driver driver5 = new Driver("Ringo", "55555");
        driverService.create(driver1);
        driverService.create(driver2);
        driverService.create(driver3);
        driverService.create(driver4);
        driverService.create(driver5);
        List<Driver> drivingVwList = List.of(driver1, driver2, driver3);
        List<Driver> drivingPorscheList = List.of(driver4, driver5);

        CarService carService = (CarService)
                injector.getInstance(CarService.class);
        Car porsche911 = new Car("911", manufacturerPorsche, drivingPorscheList);
        Car vwJetta = new Car("Jetta", manufacturerVW, drivingVwList);
        carService.create(porsche911);
        carService.create(vwJetta);

        System.out.println("Car dao and service testing:");
        System.out.println("Currently in db");
        carService.getAll().forEach(System.out::println);

        vwJetta.setModel("Passat");
        carService.update(vwJetta);
        Car updated = carService.get(vwJetta.getId());
        System.out.println("Updated car with Passat model " + updated);

        carService.addDriverToCar(driver1, porsche911);
        System.out.println("Car with added driver " + carService.get(porsche911.getId()));

        System.out.println("All info about porsche car by id "
                + carService.getAllByDriver(porsche911.getId()));

        carService.removeDriverFromCar(driver2, vwJetta);
        System.out.println("Car without driver2 " + carService.get(vwJetta.getId()));

        carService.delete(vwJetta.getId());
        System.out.println("DB after all changes");
        carService.getAll().forEach(System.out::println);
    }
}
