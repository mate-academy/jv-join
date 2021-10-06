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
    public static final Injector injector = Injector.getInstance("mate.jdbc");
    public static final CarService carService = (CarService) injector.getInstance(CarService.class);
    public static final DriverService driverService =
            (DriverService) injector.getInstance(DriverService.class);
    public static final ManufacturerService manufacturerService =
            (ManufacturerService) injector.getInstance(ManufacturerService.class);

    public static void main(String[] args) {
        Manufacturer audi = new Manufacturer("Audi", "Germany");
        audi = manufacturerService.create(audi);
        System.out.println("Manufacturer audi: " + audi);

        List<Driver> drivers = new ArrayList<>();
        Driver vlada = driverService.create(new Driver("Vlada", "01234"));
        drivers.add(vlada);
        Driver joe = driverService.create(new Driver("Joe", "56789"));
        drivers.add(joe);
        Driver bob = driverService.create(new Driver("Bob", "10111"));
        drivers.add(bob);
        System.out.println("List of drivers: ");
        drivers.forEach(System.out::println);

        System.out.println("CREATE: ");
        Car q7 = carService.create(new Car("q7", audi, drivers));
        System.out.println("Car audi q7: " + q7);

        System.out.println("GET: ");
        System.out.println(carService.get(q7.getId()));

        System.out.println("GET ALL: ");
        carService.getAll().forEach(System.out::println);

        System.out.println("Update: ");
        Manufacturer manufacturer = manufacturerService.create(new Manufacturer("BMV", "America"));
        Car updatedCar = new Car("q8", manufacturer, drivers);
        updatedCar.setId(7L);
        System.out.println("Before:" + carService.get(7L));
        carService.update(updatedCar);
        System.out.println("After:" + carService.get(7L));

        System.out.println("Delete: ");
        System.out.println(carService.delete(6L));

        System.out.println("GetAllByDriver: ");
        carService.getAllByDriver(30L).forEach(System.out::println);

        System.out.println("Remove driver from car:");
        System.out.println("Before:" + carService.get(42L));
        carService.removeDriverFromCar(vlada, q7);
        System.out.println("After:" + carService.get(42L));
    }
}
