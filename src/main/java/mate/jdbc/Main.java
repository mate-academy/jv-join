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
        ManufacturerService manufacturerService =
                (ManufacturerService) injector.getInstance(ManufacturerService.class);

        Manufacturer manufacturerAlfa = new Manufacturer("Alfa-romeo", "Italy");
        manufacturerService.create(manufacturerAlfa);

        Manufacturer manufacturerRen = new Manufacturer("Renault", "France");
        manufacturerService.create(manufacturerRen);

        DriverService driverService =
                (DriverService) injector.getInstance(DriverService.class);

        Driver driverOstap = new Driver("Ostap", "324324");
        Driver driverOleh = new Driver("Oleh", "435354");
        Driver driverAnton = new Driver("Anton", "54645");
        List<Driver> driverList = new ArrayList<>();
        driverList.add(driverOstap);
        driverList.add(driverOleh);
        driverList.add(driverAnton);
        driverList.forEach(driverService::create);

        CarService carService = (CarService) injector.getInstance(CarService.class);

        Car mercedesCar = new Car("mercedes", manufacturerAlfa, driverList);
        System.out.println(carService.create(mercedesCar));

        Car bmwCar = new Car("BMW", manufacturerRen, driverList);
        System.out.println(carService.create(bmwCar));

        carService.getAll().forEach(System.out::println);
        System.out.println(carService.get(20L));
        System.out.println(carService.delete(22L));

        System.out.println(carService.getAllByDriver(15L));

        Driver driverIvan = new Driver("Ivan", "325235");

        driverService.create(driverIvan);
        carService.removeDriverFromCar(driverOstap, mercedesCar);
        carService.addDriverToCar(driverIvan, mercedesCar);
        System.out.println(carService.update(mercedesCar));
    }
}
