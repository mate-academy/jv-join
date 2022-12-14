package mate.jdbc;

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
    private static final ManufacturerService manufacturerService = (ManufacturerService) injector
            .getInstance(ManufacturerService.class);
    private static final DriverService driverService = (DriverService) injector
            .getInstance(DriverService.class);
    private static final CarService carService = (CarService) injector
            .getInstance(CarService.class);

    public static void main(String[] args) {
        Driver maksym = new Driver("Maksym", "1234");
        Driver olha = new Driver("Olha", "1235");
        Driver denys = new Driver("Denys", "1236");
        Driver anna = new Driver("Anna", "1237");
        driverService.create(maksym);
        driverService.create(olha);
        driverService.create(denys);
        driverService.create(anna);

        Manufacturer audiManufacturer = new Manufacturer("Audi", "Germany");
        Manufacturer fordManufacturer = new Manufacturer("Ford", "USA");
        manufacturerService.create(audiManufacturer);
        manufacturerService.create(fordManufacturer);

        Car audi = new Car("A6", audiManufacturer, List.of(maksym, olha));
        Car ford = new Car("Focus", fordManufacturer, List.of(denys, anna));
        carService.create(audi);
        carService.create(ford);
        carService.removeDriverFromCar(olha, audi);
        carService.addDriverToCar(anna, audi);
        System.out.println(carService.get(audi.getId()));
        carService.getAllByDriver(denys.getId()).forEach(System.out::println);
        carService.getAll().forEach(System.out::println);
    }
}
