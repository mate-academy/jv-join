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
    private static Injector injector = Injector.getInstance("mate.jdbc");

    public static void main(String[] args) {
        Manufacturer ferrari = new Manufacturer(4L, "ferrari", "Italy");
        Manufacturer volvo = new Manufacturer(5L, "volvo", "USA");
        ManufacturerService manufacturerService =
                (ManufacturerService) injector.getInstance(ManufacturerService.class);
        manufacturerService.create(ferrari);
        manufacturerService.create(volvo);

        DriverService driverService = (
                DriverService) injector.getInstance(DriverService.class);
        Driver jane = new Driver(4L, "jane","4444");
        Driver rick = new Driver(6L, "rick", "666666");
        Driver lina = new Driver(5L, "lina","55555");
        driverService.create(jane);
        driverService.create(lina);
        driverService.create(rick);

        Car first = new Car(4L, "universal",
                manufacturerService.get(ferrari.getId()),
                List.of(driverService.get(jane.getId()), driverService.get(lina.getId())));
        Car second = new Car(5L, "business",
                manufacturerService.get(volvo.getId()),
                List.of(driverService.get(rick.getId())));
        CarService carService =
                (CarService) injector.getInstance(CarService.class);
        carService.create(first);
        carService.create(second);
        carService.getAll().stream().forEach(System.out::println);
        carService.getAllByDriver(driverService.get(rick.getId()).getId())
                .stream().forEach(System.out::println);
        first.setManufacturer(manufacturerService.get(volvo.getId()));
        carService.update(first);
        System.out.println(carService.get(first.getId()));
        carService.delete(first.getId());
        System.out.println(carService.getAll());
    }
}
