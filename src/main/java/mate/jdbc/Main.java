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
        Manufacturer volkswagen = new Manufacturer("Volkswagen", "Germany");
        Manufacturer volvo = new Manufacturer("Volvo", "Sweden");
        Manufacturer mazda = new Manufacturer("Mazda", "Japan");
        manufacturerService.create(volkswagen);
        manufacturerService.create(volvo);
        manufacturerService.create(mazda);

        DriverService driverService =
                (DriverService) injector.getInstance(DriverService.class);
        Driver alina = new Driver("Alina", "UA123");
        Driver igor = new Driver("Igor", "UA456");
        Driver anna = new Driver("Anna", "UA789");
        driverService.create(alina);
        driverService.create(igor);
        driverService.create(anna);

        List<Driver> volkswagenDrivers = new ArrayList<>();
        volkswagenDrivers.add(alina);
        volkswagenDrivers.add(anna);

        List<Driver> volvoDrivers = new ArrayList<>();
        volvoDrivers.add(igor);
        volvoDrivers.add(anna);

        List<Driver> mazdaDrivers = new ArrayList<>();
        mazdaDrivers.add(alina);
        mazdaDrivers.add(igor);

        CarService carService =
                (CarService) injector.getInstance(CarService.class);
        Car passat = new Car("Passat", volkswagen, volkswagenDrivers);
        Car xc90Volvo = new Car("XC90", volvo, volkswagenDrivers);
        Car cxMazda = new Car("CX-5", mazda, mazdaDrivers);
        carService.create(passat);
        carService.create(xc90Volvo);
        carService.create(cxMazda);
        System.out.println(carService.get(passat.getId()));

        carService.getAll().forEach(System.out::println);

        carService.delete(passat.getId());
        xc90Volvo.setModel("XC90 2022");
        carService.update(xc90Volvo);
        carService.getAllByDriver(alina.getId()).forEach(System.out::println);

        carService.addDriverToCar(anna, cxMazda);
        carService.removeDriverFromCar(igor, cxMazda);
    }
}
