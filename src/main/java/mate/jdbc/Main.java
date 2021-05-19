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
        Driver maks = new Driver();
        maks.setName("Maksym");
        maks.setLicenseNumber("8800535");

        Driver serj = new Driver();
        serj.setName("Serj");
        serj.setLicenseNumber("8800536");

        Driver artem = new Driver();
        artem.setName("Artem");
        artem.setLicenseNumber("8700537");

        DriverService driverService = (DriverService)
                injector.getInstance(DriverService.class);

        driverService.create(maks);
        driverService.create(serj);
        driverService.create(artem);

        artem.setLicenseNumber("8800537");
        driverService.update(artem);

        Manufacturer sanos = new Manufacturer();
        sanos.setName("Sanos");
        sanos.setCountry("North Macedonia");

        Manufacturer skoda = new Manufacturer();
        skoda.setName("Skoda");
        skoda.setCountry("Czech Republic");

        Manufacturer acura = new Manufacturer();
        acura.setName("Acura");
        acura.setCountry("Japan");

        ManufacturerService manufacturerService = (ManufacturerService)
                injector.getInstance(ManufacturerService.class);

        manufacturerService.create(sanos);
        manufacturerService.create(skoda);
        manufacturerService.create(acura);

        manufacturerService.get(acura.getId());

        skoda.setName("Škoda");
        manufacturerService.update(skoda);

        Car acuraCar = new Car();
        acuraCar.setModel("MDX");
        acuraCar.setManufacturer(acura);
        List<Driver> acuraDrivers = new ArrayList<>();
        acuraDrivers.add(maks);
        acuraCar.setDrivers(acuraDrivers);

        Car skodaCar = new Car();
        skodaCar.setModel("Kamiq");
        skodaCar.setManufacturer(skoda);
        List<Driver> skodaDrivers = new ArrayList<>();
        skodaDrivers.add(serj);
        skodaDrivers.add(maks);
        skodaCar.setDrivers(skodaDrivers);

        Car sanosCar = new Car();
        sanosCar.setModel("S-404");
        sanosCar.setManufacturer(sanos);
        List<Driver> sanosDrivers = new ArrayList<>();
        sanosDrivers.add(serj);
        sanosDrivers.add(artem);
        sanosCar.setDrivers(sanosDrivers);

        CarService carService = (CarService) injector.getInstance(CarService.class);

        System.out.println("Test create:");
        carService.create(skodaCar);
        carService.create(sanosCar);
        carService.create(acuraCar);

        System.out.println("Test get all by driverID:");
        System.out.println(carService.getAllByDriver(serj.getId()));

        System.out.println("Test get all:");
        carService.getAll().forEach(System.out::println);

        System.out.println("Test get by carId:");
        System.out.println(carService.get(skodaCar.getId()));

        System.out.println("Test update:");
        skodaCar.setModel("Kushaq");
        List<Driver> newSkodaDrivers = new ArrayList<>();
        newSkodaDrivers.add(artem);
        skodaCar.setDrivers(newSkodaDrivers);

        Car updatedSkodaCar = carService.update(skodaCar);
        System.out.println(updatedSkodaCar);

        System.out.println("Test add driver to a car and remove driver:");
        Driver tolyaDriver = new Driver();
        tolyaDriver.setName("Tolya");
        tolyaDriver.setLicenseNumber("8800576");
        driverService.create(tolyaDriver);

        carService.addDriverToCar(tolyaDriver, sanosCar);
        carService.removeDriverFromCar(artem, sanosCar);
        System.out.println(carService.get(sanosCar.getId()));

        System.out.println("Test get all cars by driverId:");
        List<Car> allCarsByDriverId = carService.getAllByDriver(tolyaDriver.getId());
        allCarsByDriverId.forEach(System.out::println);

        System.out.println("Test delete method:");
        carService.delete(skodaCar.getId());
        carService.getAll().forEach(System.out::println);

    }
}
