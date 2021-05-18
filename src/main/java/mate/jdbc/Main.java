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
        DriverService driverService
                = (DriverService) injector.getInstance(DriverService.class);

        Driver bob = new Driver("Bob", "12345678");
        Driver alice = new Driver("Alice", "11111111");
        Driver omen = new Driver("Omen", "666");
        driverService.create(bob);
        driverService.create(alice);
        driverService.create(omen);
        List<Driver> teslaDrivers = new ArrayList<>();
        teslaDrivers.add(bob);
        teslaDrivers.add(alice);
        teslaDrivers.add(omen);

        ManufacturerService manufacturerService
                = (ManufacturerService) injector.getInstance(ManufacturerService.class);

        Manufacturer teslaManufacturer = new Manufacturer("Tesla", "USA");
        manufacturerService.create(teslaManufacturer);

        Manufacturer toyotaManufacturer = new Manufacturer("Toyota", "Japan");
        manufacturerService.create(toyotaManufacturer);

        Manufacturer opelManufacturer = new Manufacturer("Opel", "Germany");
        manufacturerService.create(opelManufacturer);

        Car tesla = new Car(teslaManufacturer, teslaDrivers);

        CarService carService = (CarService) injector.getInstance(CarService.class);
        carService.create(tesla);

        List<Driver> toyotaDrivers = new ArrayList<>();
        toyotaDrivers.add(bob);
        toyotaDrivers.add(omen);
        Car toyota = new Car(toyotaManufacturer, toyotaDrivers);
        carService.create(toyota);

        List<Driver> lexusDrivers = new ArrayList<>();
        lexusDrivers.add(bob);
        lexusDrivers.add(omen);
        Car lexus = new Car(toyotaManufacturer, lexusDrivers);
        carService.create(lexus);

        System.out.println(carService.get(tesla.getId()));
        carService.delete(toyota.getId());

        List<Driver> newTeslaDrivers = new ArrayList<>();
        Driver house = new Driver("Greg", "22b");
        driverService.create(house);
        newTeslaDrivers.add(house);
        tesla.setDrivers(newTeslaDrivers);
        System.out.println(carService.update(tesla));

        System.out.println();
        System.out.println(carService.get(tesla.getId()));
        Driver habib = new Driver("Habib", "00000");
        driverService.create(habib);
        carService.addDriverToCar(habib, tesla);
        carService.removeDriverFromCar(omen, tesla);
        System.out.println(carService.get(tesla.getId()));

        System.out.println();
        System.out.println("My car info: " + carService.getAllByDriver(1L));
        System.out.println("Bobs car info: " + carService.getAllByDriver(bob.getId()));
        System.out.println("Omen car info: " + carService.getAllByDriver(omen.getId()));
        System.out.println("Greg car info: " + carService.getAllByDriver(house.getId()));
        System.out.println("Habib car info: " + carService.getAllByDriver(habib.getId()));
        System.out.println("All: " + carService.getAll());
    }
}
