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
        ManufacturerService manufacturerService
                = (ManufacturerService) injector.getInstance(ManufacturerService.class);
        Manufacturer manufacturerPeugeot = new Manufacturer("Peugeot", "France");
        Manufacturer manufacturerAlfaRomeo = new Manufacturer("ALfa Romeo", "Italy");
        Manufacturer manufacturerZaz = new Manufacturer("ZAZ", "Ukraine");
        manufacturerService.create(manufacturerPeugeot);
        manufacturerService.create(manufacturerZaz);
        manufacturerService.create(manufacturerAlfaRomeo);

        DriverService driverService
                = (DriverService) injector.getInstance(DriverService.class);
        Driver driverSebastian = new Driver("Sebastian Loeb", "1123");
        driverService.create(driverSebastian);
        Driver driverMichael = new Driver("Michael Shumaher", "2332");
        driverService.create(driverMichael);
        Driver driverColin = new Driver("Colin McRay", "4466");
        driverService.create(driverColin);
        Driver driverKen = new Driver("Ken Block", "0911");
        driverService.create(driverKen);

        List<Driver> driverListPeugeot = new ArrayList<>();
        driverListPeugeot.add(driverSebastian);
        driverListPeugeot.add(driverColin);
        List<Driver> driverListZaz = new ArrayList<>();
        driverListZaz.add(driverMichael);
        List<Driver> driverListAlfaRomeo = new ArrayList<>();
        driverListAlfaRomeo.add(driverKen);

        CarService carService = (CarService) injector.getInstance(CarService.class);
        Car carModelPeugeot = new Car("508", manufacturerPeugeot, driverListPeugeot);
        Car carModelZaz = new Car("Tavria", manufacturerZaz, driverListZaz);
        Car carModelAlfaRomeo = new Car("Julia", manufacturerAlfaRomeo,driverListAlfaRomeo);
        carService.create(carModelPeugeot);
        carService.create(carModelZaz);
        carService.create(carModelAlfaRomeo);

        carModelPeugeot.setModel("3008");
        carService.update(carModelPeugeot);
        carService.getAll().forEach(System.out::println);
        carService.removeDriverFromCar(driverColin,carModelPeugeot);
        carService.getAllByDriver(driverSebastian.getId()).forEach(System.out::println);
    }
}
