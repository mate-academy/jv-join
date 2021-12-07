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
        final CarService carService = (CarService)
                injector.getInstance(CarService.class);
        final ManufacturerService manufacturerService = (ManufacturerService)
                injector.getInstance(ManufacturerService.class);

        final DriverService driverService = (DriverService)
                injector.getInstance(DriverService.class);

        Manufacturer manufacturerFirst = new Manufacturer("Lexus", "Ukraine");
        Manufacturer manufacturerSecond = new Manufacturer("FORD", "USA");
        manufacturerService.create(manufacturerFirst);
        manufacturerService.create(manufacturerSecond);

        Driver driverFirst = new Driver("Vitalii", "12325q5");
        Driver driverSecond = new Driver("Anton", "3445nbbb");
        driverService.create(driverFirst);
        driverService.create(driverSecond);

        List<Driver> drivers = new ArrayList<>();
        drivers.add(driverFirst);
        drivers.add(driverSecond);
        
        Car carFirst = new Car("LModel", manufacturerFirst, drivers);
        Car carSecond = new Car("FModel", manufacturerSecond, drivers);
        carService.create(carFirst);
        carService.create(carSecond);

        Car carFirstFromDB = carService.get(carFirst.getId());
        List<Car> allCarsFromDB = carService.getAll();
        System.out.println("All cars from DB");
        allCarsFromDB.stream()
                .forEach(System.out::println);

        System.out.println("\nAll cars by driver " + driverFirst);
        carService.getAllByDriver(driverFirst.getId())
                        .stream()
                                .forEach(System.out::println);
        carFirst.setModel("TOYOTA");
        carFirst.setManufacturer(manufacturerSecond);
        carService.update(carFirst);

        Driver newDriverToCar = new Driver("Tom", "12467879");
        driverService.create(newDriverToCar);
        carService.addDriverToCar(newDriverToCar, carSecond);

        carService.removeDriverFromCar(newDriverToCar, carSecond);
    }
}
