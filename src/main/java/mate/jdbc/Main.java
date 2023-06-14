package mate.jdbc;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
        ManufacturerService manufacturerService
                = (ManufacturerService) injector.getInstance(ManufacturerService.class);
        System.out.println("Manufacturer Service testing:");
        Map<String, Manufacturer> manufacturersMap
                = runExampleOfUsageManufacturerService(manufacturerService);

        DriverService driverService = (DriverService) injector.getInstance(DriverService.class);
        System.out.println("\nDriver Service testing:");
        Map<String, Driver> driversMap = runExampleOfUsageDriverService(driverService);

        CarService carService = (CarService) injector.getInstance(CarService.class);
        System.out.println("\nCar Service testing:");
        runExampleOfUsageCarService(carService, manufacturersMap, driversMap);
    }

    private static Map<String, Manufacturer> runExampleOfUsageManufacturerService(
            ManufacturerService manufacturerService) {
        System.out.println("Create:");
        Manufacturer opelManufacturer = new Manufacturer(null, "Opel", "Germany");
        Manufacturer fiatManufacturer = new Manufacturer(null, "Fiat", "France");
        Manufacturer renaultManufacturer = new Manufacturer(null, "Renault", "France");
        System.out.println(manufacturerService.create(opelManufacturer));
        System.out.println(manufacturerService.create(fiatManufacturer));
        System.out.println(manufacturerService.create(renaultManufacturer));
        Map<String, Manufacturer> manufacturersMap = new HashMap<>();
        manufacturersMap.put(opelManufacturer.getName(), opelManufacturer);
        manufacturersMap.put(fiatManufacturer.getName(), fiatManufacturer);
        manufacturersMap.put(renaultManufacturer.getName(), renaultManufacturer);

        System.out.println("Delete:");
        System.out.println(manufacturerService.delete(fiatManufacturer.getId()));
        manufacturersMap.remove(fiatManufacturer.getName());

        System.out.println("Get all:");
        List<Manufacturer> manufacturers = manufacturerService.getAll();
        manufacturers.forEach(System.out::println);
        return manufacturersMap;
    }

    private static Map<String, Driver> runExampleOfUsageDriverService(DriverService driverService) {
        System.out.println("Create:");
        Driver bobDriver = new Driver(null, "Bob", "12345");
        Driver aliceDriver = new Driver(null, "Alice", "56789");
        Driver johnDriver = new Driver(null, "John", "98765");
        System.out.println(driverService.create(bobDriver));
        System.out.println(driverService.create(aliceDriver));
        System.out.println(driverService.create(johnDriver));
        Map<String, Driver> driversMap = new HashMap<>();
        driversMap.put(bobDriver.getName(), bobDriver);
        driversMap.put(aliceDriver.getName(), aliceDriver);
        driversMap.put(johnDriver.getName(), johnDriver);

        System.out.println("Delete:");
        System.out.println(driverService.delete(aliceDriver.getId()));
        driversMap.remove(aliceDriver.getName());

        System.out.println("Get all:");
        List<Driver> drivers = driverService.getAll();
        drivers.forEach(System.out::println);
        return driversMap;
    }

    private static void runExampleOfUsageCarService(CarService carService,
                                                    Map<String, Manufacturer> manufacturersMap,
                                                    Map<String, Driver> driversMap) {
        System.out.println("Create:");
        Car opelCombo = new Car("Combo", manufacturersMap.get("Opel"));
        List<Driver> opelDriversList = new ArrayList<>();
        opelDriversList.add(driversMap.get("Bob"));
        opelCombo.setDrivers(opelDriversList);

        Car renaultMegane = new Car("Megane", manufacturersMap.get("Renault"));
        List<Driver> renaultDriversList = new ArrayList<>();
        renaultDriversList.add(driversMap.get("John"));
        renaultDriversList.add(driversMap.get("Bob"));
        renaultMegane.setDrivers(renaultDriversList);

        System.out.println(carService.create(opelCombo));
        System.out.println(carService.create(renaultMegane));

        System.out.println("Read:");
        System.out.println(carService.get(opelCombo.getId()));

        System.out.println("Update:");
        opelCombo.setModel("Astra");
        System.out.println(carService.update(opelCombo));

        System.out.println("Delete:");
        System.out.println(carService.delete(renaultMegane.getId()));

        System.out.println("Get all:");
        List<Car> cars = carService.getAll();
        cars.forEach(System.out::println);

        System.out.println("addDriverToCar:");
        carService.addDriverToCar(driversMap.get("John"), opelCombo);
        System.out.println(carService.get(opelCombo.getId()));

        System.out.println("removeDriverFromCar");
        carService.removeDriverFromCar(driversMap.get("Bob"), opelCombo);
        System.out.println(carService.get(opelCombo.getId()));

        System.out.println("getAllByDriver");
        System.out.println(carService.getAllByDriver(driversMap.get("John").getId()));
    }
}
