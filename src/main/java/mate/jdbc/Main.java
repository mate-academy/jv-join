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
        ManufacturerService manufacturerDao = (ManufacturerService)
                injector.getInstance(ManufacturerService.class);

        Manufacturer ferrari = new Manufacturer();
        ferrari.setCountry("Italy");
        ferrari.setName("Ferrari");
        manufacturerDao.create(ferrari);

        Manufacturer porsche = new Manufacturer();
        porsche.setCountry("Germany");
        porsche.setName("Porsche");
        manufacturerDao.create(porsche);

        Manufacturer koenigsegg = new Manufacturer();
        koenigsegg.setCountry("Sweden");
        koenigsegg.setName("Koenigsegg");
        manufacturerDao.create(koenigsegg);

        manufacturerDao.getAll().forEach(System.out::println);

        porsche.setCountry("Ukraine");
        manufacturerDao.update(porsche);

        manufacturerDao.getAll().forEach(System.out::println);

        DriverService driverService = (DriverService) injector.getInstance(DriverService.class);

        Driver eugene = new Driver();
        eugene.setName("Eugene");
        eugene.setLicenseNumber("888");
        driverService.create(eugene);

        Driver andrew = new Driver();
        andrew.setName("Andrew");
        andrew.setLicenseNumber("777");
        driverService.create(andrew);

        Driver daniel = new Driver();
        daniel.setName("Daniel");
        daniel.setLicenseNumber("666");
        driverService.create(daniel);

        System.out.println(driverService.get(2L));
        driverService.getAll().forEach(System.out::println);

        daniel.setLicenseNumber("88");
        driverService.update(daniel);

        driverService.getAll().forEach(System.out::println);

        List<Driver> ferrariDrivers = new ArrayList<>();
        ferrariDrivers.add(daniel);
        ferrariDrivers.add(andrew);

        List<Driver> koenigseggDrivers = new ArrayList<>();
        koenigseggDrivers.add(eugene);
        koenigseggDrivers.add(andrew);
        koenigseggDrivers.add(daniel);

        List<Driver> porscheDrivers = new ArrayList<>();
        porscheDrivers.add(eugene);

        Car ferrariCar = new Car();
        ferrariCar.setManufacturer(ferrari);
        ferrariCar.setModel("Roma");
        ferrariCar.setDrivers(ferrariDrivers);

        Car koenigseggCar = new Car();
        koenigseggCar.setManufacturer(koenigsegg);
        koenigseggCar.setModel("Agera R");
        koenigseggCar.setDrivers(koenigseggDrivers);

        Car porscheCar = new Car();
        porscheCar.setManufacturer(porsche);
        porscheCar.setModel("911 turbo s");
        porscheCar.setDrivers(porscheDrivers);

        CarService carService = (CarService) injector.getInstance(CarService.class);
        carService.create(ferrariCar);
        carService.create(koenigseggCar);
        carService.create(porscheCar);

        carService.getAll().forEach(System.out::println);

        ferrariCar.setModel("488 Pista");
        carService.update(ferrariCar);

        carService.delete(ferrariCar.getId());
        carService.removeDriverFromCar(daniel, ferrariCar);
        carService.addDriverToCar(eugene, ferrariCar);
        carService.update(ferrariCar);

        carService.getAllByDriver(andrew.getId());

        carService.getAll().forEach(System.out::println);
    }
}
