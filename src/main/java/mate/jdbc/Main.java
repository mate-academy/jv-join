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
        Manufacturer manufacturerVolvo = new Manufacturer("Volvo", "Sweden");
        Manufacturer manufacturerVolkswagen = new Manufacturer("Volkswagen", "Germany");
        manufacturerService.create(manufacturerVolvo);
        manufacturerService.create(manufacturerVolkswagen);

        DriverService driverService = (DriverService) injector.getInstance(DriverService.class);
        Driver driverAlex = new Driver(null, "Alex", "112233");
        Driver driverVin = new Driver(null, "Vin", "223344");
        Driver driverKarl = new Driver(null, "Karl", "334455");
        driverService.create(driverAlex);
        driverService.create(driverVin);
        driverService.create(driverKarl);

        List<Driver> firstCarDrivers = new ArrayList<>();
        List<Driver> secondCarDrivers = new ArrayList<>();

        CarService carService = (CarService) injector.getInstance(CarService.class);
        Car firstCar = new Car("car12", manufacturerVolkswagen, firstCarDrivers);
        Car secondCar = new Car("car16", manufacturerVolvo, secondCarDrivers);
        carService.create(firstCar);
        carService.create(secondCar);
        firstCarDrivers.add(driverAlex);
        firstCarDrivers.add(driverVin);
        secondCarDrivers.add(driverAlex);
        secondCarDrivers.add(driverKarl);
        carService.getAllByDriver(firstCar.getId());
        firstCar.setManufacturer(manufacturerService.get(manufacturerVolkswagen.getId()));
        carService.update(firstCar);
        carService.getAll().forEach(System.out::println);
        carService.removeDriverFromCar(driverVin, firstCar);
        carService.getAllByDriver(driverKarl.getId()).forEach(System.out::println);
        carService.delete(secondCar.getId());
    }
}
