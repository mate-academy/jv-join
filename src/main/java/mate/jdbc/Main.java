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
        // test your code here
        Manufacturer bentley = new Manufacturer("Bentley", "United Kingdom");

        Manufacturer ferrari = new Manufacturer("Ferrari", "Italy");

        ManufacturerService manufacturerService = (ManufacturerService) injector
                .getInstance(ManufacturerService.class);
        manufacturerService.create(bentley);
        manufacturerService.create(ferrari);

        List<Manufacturer> manufacturers = manufacturerService.getAll();
        manufacturers.forEach(System.out::println);

        bentley = manufacturers.get(0);
        bentley.setCountry("United Kingdom");
        manufacturerService.update(bentley);
        System.out.println(manufacturerService.get(bentley.getId()));
        manufacturerService.delete(bentley.getId());
        manufacturerService.getAll().forEach(System.out::println);

        DriverService driverService =
                (DriverService) injector.getInstance(DriverService.class);

        Driver driverAang = new Driver("Aang", "1-111");
        driverService.create(driverAang);

        Driver driverZuko = new Driver("Zuko", "1-222");
        driverService.create(driverZuko);

        List<Driver> drivers = driverService.getAll();
        drivers.forEach(System.out::println);

        driverService.delete(driverAang.getId());
        System.out.println("Driver was deleted");
        drivers.forEach(System.out::println);

        driverZuko.setLicenseNumber("2-111");
        System.out.println("Driver license number was updated");
        System.out.println(driverService.update(driverZuko));

        System.out.println("Getting Driver");
        System.out.println(driverService.get(driverZuko.getId()));

        List<Driver> driverList = new ArrayList<>();
        driverList.add(driverAang);
        driverList.add(driverZuko);

        CarService carService = (CarService) injector.getInstance(CarService.class);

        System.out.println("Create 2 cars");
        Car ferrariEnzo = new Car("Enzo", ferrari, driverList);
        carService.create(ferrariEnzo);
        System.out.println(carService.get(ferrariEnzo.getId()));
        Car bentleyArnage = new Car("Arnage", bentley, driverList);
        carService.create(bentleyArnage);
        System.out.println(carService.get(bentleyArnage.getId()));

        System.out.println(carService.getAll());
        System.out.println("---");
        carService.removeDriverFromCar(driverAang, ferrariEnzo);
        System.out.println("Remove driverAang from ferrariEnzo!");
        System.out.println(carService.get(ferrariEnzo.getId()));
        System.out.println("---");
        carService.addDriverToCar(driverZuko, bentleyArnage);
        System.out.println("Add driverZuko to bentleyArnage!");
    }
}
