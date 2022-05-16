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
        Manufacturer manufacturerToyota = new Manufacturer("Virobnik", "Kraina");
        Manufacturer manufacturerBmw = new Manufacturer("InshiyVirobnik", "InshaKraina");
        manufacturerService.create(manufacturerToyota);
        manufacturerService.create(manufacturerBmw);

        DriverService driverService = (DriverService) injector.getInstance(DriverService.class);
        Driver driverVodiy = new Driver("Vodiy", "NOMER");
        Driver driverInshiyVodiy = new Driver("InshiyVodiy", "INSHIYNOMER");
        Driver driverSheInshiyVodiy = new Driver("SheInshiyVodiy", "SHEINSHI");
        driverService.create(driverVodiy);
        driverService.create(driverInshiyVodiy);
        driverService.create(driverSheInshiyVodiy);

        List<Driver> firstCarDrivers = new ArrayList<>();
        List<Driver> secondCarDrivers = new ArrayList<>();

        CarService carService = (CarService) injector.getInstance(CarService.class);
        Car firstCar = new Car("Model", manufacturerBmw, firstCarDrivers);
        Car secondCar = new Car("InshaModel", manufacturerToyota, secondCarDrivers);
        carService.create(firstCar);
        carService.create(secondCar);
        firstCarDrivers.add(driverInshiyVodiy);
        firstCarDrivers.add(driverSheInshiyVodiy);
        secondCarDrivers.add(driverInshiyVodiy);
        secondCarDrivers.add(driverVodiy);
        carService.getAllByDriver(firstCar.getId());
        firstCar.setManufacturer(manufacturerService.get(manufacturerBmw.getId()));
        carService.update(firstCar);
        carService.getAll().forEach(System.out::println);

        carService.removeDriverFromCar(driverSheInshiyVodiy, firstCar);
        carService.getAllByDriver(driverInshiyVodiy.getId()).forEach(System.out::println);

        carService.delete(secondCar.getId());
    }
}
