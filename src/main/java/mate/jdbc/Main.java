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
        DriverService driverService =
                (DriverService) injector.getInstance(DriverService.class);

        Driver driverOleh = new Driver();
        driverOleh.setName("Oleh");
        driverOleh.setLicenseNumber("11111");
        driverOleh = driverService.create(driverOleh);

        Driver driverAlice = new Driver();
        driverAlice.setName("ALice");
        driverAlice.setLicenseNumber("123422");
        driverAlice = driverService.create(driverAlice);

        Driver driverAlex = new Driver();
        driverAlex.setName("Alex");
        driverAlex.setLicenseNumber("222232");
        driverAlex = driverService.create(driverAlex);

        ManufacturerService manufacturerService =
                (ManufacturerService) injector.getInstance(ManufacturerService.class);

        Manufacturer vw = new Manufacturer();
        vw.setName("VW");
        vw.setCountry("Germany");
        vw = manufacturerService.create(vw);

        Manufacturer subaru = new Manufacturer();
        subaru.setName("Subaru");
        subaru.setCountry("Japan");
        subaru = manufacturerService.create(subaru);

        Car carVW = new Car();
        carVW.setModel("Transporter");
        carVW.setManufacturer(vw);
        List<Driver> driversVW = new ArrayList<>();
        driversVW.add(driverOleh);
        carVW.setDrivers(driversVW);

        CarService carService = (CarService) injector.getInstance(CarService.class);

        carVW = carService.create(carVW);
        System.out.println(carVW);

        carVW.setModel("Golf");
        carVW = carService.update(carVW);
        System.out.println(carVW);

        Car carForester = new Car();
        carForester.setModel("Forester");
        carForester.setManufacturer(subaru);
        List<Driver> driversForester = new ArrayList<>();
        driversForester.add(driverOleh);
        driversForester.add(driverAlice);
        carForester.setDrivers(driversForester);

        carForester = carService.create(carForester);
        boolean delete = carService.delete(carVW.getId());
        System.out.println("Is deleted " + carVW.getModel() + "? " + delete);

        System.out.println("Get all");
        carService.getAll().forEach(System.out::println);
        System.out.println();
        System.out.println("Get by Driver " + driverOleh);
        carService.getAllByDriver(driverOleh.getId()).forEach(System.out::println);

        System.out.println();
        carService.addDriverToCar(driverAlex, carForester);
        System.out.println(carService.get(carForester.getId()));

        carService.removeDriverFromCar(driverAlice, carForester);
        System.out.println(carService.get(carForester.getId()));
    }
}
