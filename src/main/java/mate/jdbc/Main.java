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
    private static final ManufacturerService manufacturerService
                = (ManufacturerService) injector.getInstance(ManufacturerService.class);
    private static final DriverService driverService
                = (DriverService) injector.getInstance(DriverService.class);
    private static final CarService carService
                = (CarService) injector.getInstance(CarService.class);

    public static void main(String[] args) {
        Manufacturer manufacturer = new Manufacturer("toyota", "Japan");
        manufacturerService.create(manufacturer);

        Driver john = new Driver("John", "112211");
        Driver andrew = new Driver("Andrew", "223344");
        Driver nik = new Driver("Nik", "332233");
        Driver mihael = new Driver("Mihael", "887788");

        driverService.create(john);
        driverService.create(andrew);
        driverService.create(nik);
        driverService.create(mihael);
        System.out.println(driverService.getAll());

        Car car = new Car("nissan", manufacturer, List.of(john, andrew));
        car.setModel("mazda");

        carService.create(car);
        car = carService.get(car.getId());
        carService.addDriverToCar(nik,car);
        carService.removeDriverFromCar(john, car);
        carService.getAll().forEach(System.out::println);
        carService.getAllByDriver(john.getId()).forEach(System.out::println);
        carService.delete(car.getId());
        carService.getAll().forEach(System.out::println);
    }
}
