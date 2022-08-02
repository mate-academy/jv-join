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
        Manufacturer mercedes = new Manufacturer("Mercedes", "Germany");
        Manufacturer bmw = new Manufacturer("Bmw", "Germany");
        Manufacturer audi = new Manufacturer("Audi", "Germany");
        manufacturerService.create(mercedes);
        manufacturerService.create(bmw);
        manufacturerService.create(audi);

        DriverService driverService =
                (DriverService) injector.getInstance(DriverService.class);
        Driver illya = new Driver("Illya", "UA2045");
        Driver igor = new Driver("Igor", "UA3512");
        Driver andrey = new Driver("Andrey", "UA1257");
        driverService.create(illya);
        driverService.create(igor);
        driverService.create(andrey);

        List<Driver> mercedesDrivers = new ArrayList<>();
        mercedesDrivers.add(illya);
        mercedesDrivers.add(andrey);

        List<Driver> bmwDrivers = new ArrayList<>();
        bmwDrivers.add(igor);
        bmwDrivers.add(andrey);

        List<Driver> audiDrivers = new ArrayList<>();
        audiDrivers.add(illya);
        audiDrivers.add(igor);

        CarService carService =
                (CarService) injector.getInstance(CarService.class);
        Car c300 = new Car("C300", mercedes, mercedesDrivers);
        Car i340 = new Car("340i", bmw, bmwDrivers);
        Car a6 = new Car("A6", audi, audiDrivers);
        carService.create(c300);
        carService.create(i340);
        carService.create(a6);
        System.out.println(carService.get(c300.getId()));

        carService.getAll().forEach(System.out::println);

        carService.delete(c300.getId());
        i340.setModel("340i 2020");
        carService.update(i340);
        carService.getAllByDriver(illya.getId()).forEach(System.out::println);

        carService.addDriverToCar(andrey, a6);
        carService.removeDriverFromCar(igor, a6);
    }
}
