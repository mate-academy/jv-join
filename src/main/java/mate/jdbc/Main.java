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
        Manufacturer manufacturerFrance = new Manufacturer(null,"Renault", "France");
        Manufacturer manufacturerGermany = new Manufacturer(null,"Mercedes", "Germany");
        Manufacturer manufacturerItaly = new Manufacturer(null,"Ferrari", "Italy");
        manufacturerService.create(manufacturerFrance);
        manufacturerService.create(manufacturerGermany);
        manufacturerService.create(manufacturerItaly);

        DriverService driverService =
                (DriverService) injector.getInstance(DriverService.class);
        Driver slavik = new Driver(null,"Slavik","1715");
        Driver roman = new Driver(null,"Roman","1234");
        Driver john = new Driver(null,"John","5566");
        Driver ron = new Driver(null,"Ron","6677");
        driverService.create(slavik);
        driverService.create(roman);
        driverService.create(john);
        driverService.create(ron);
        List<Driver> driversEast = new ArrayList<>();
        driversEast.add(slavik);
        driversEast.add(roman);
        List<Driver> driversWest = new ArrayList<>();
        driversWest.add(john);
        driversWest.add(ron);
        CarService carService = (CarService) injector.getInstance(CarService.class);
        Car renaultKwid = new Car(null,"Kwid",manufacturerFrance,driversEast);
        Car mersedesAmg = new Car(null,"AMG VISION EQXX",manufacturerGermany,driversWest);
        carService.create(renaultKwid);
        carService.create(mersedesAmg);
        carService.addDriverToCar(slavik,renaultKwid);
        carService.addDriverToCar(slavik,mersedesAmg);
        carService.addDriverToCar(roman,renaultKwid);
        carService.addDriverToCar(john,mersedesAmg);
        carService.addDriverToCar(ron,mersedesAmg);
        System.out.println("Car service original: ");
        System.out.println(carService.getAll());
        Car renaultKwidUpdated = carService.get(1L);
        renaultKwidUpdated.setModel("Kwid RLX");
        renaultKwidUpdated.setDrivers(driversWest);
        carService.update(renaultKwidUpdated);
        System.out.println("Car service updated: ");
        System.out.println(carService.getAll());
        carService.delete(1L);
        System.out.println("Car service delete: ");
        System.out.println(carService.getAll());
    }
}
