package mate.jdbc;

import java.util.Arrays;
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
        List<Manufacturer> manufacturers = Arrays.asList(
                new Manufacturer("Hyundai", "South Korea"),
                new Manufacturer("Tesla", "USA"),
                new Manufacturer("BMW", "Germany"),
                new Manufacturer("Renault", "France")
        );
        manufacturers.forEach(manufacturerService::create);
        List<Driver> drivers = Arrays.asList(
                new Driver("Ivan Petrovych", "BB352335"),
                new Driver("Volodymyr Andriyovych", "TT353535"),
                new Driver("Petro Ostapovych", "KK346346"),
                new Driver("Andriy Andriyovych", "PP353634")
        );
        drivers.forEach(driverService::create);
        List<Car> cars = Arrays.asList(
                new Car("Santa Fe", manufacturerService.get(1L)),
                new Car("Model X", manufacturerService.get(2L)),
                new Car("X5", manufacturerService.get(3L)),
                new Car("Kangoo", manufacturerService.get(4L))
        );
        cars.forEach(carService::create);
        carService.addDriverToCar(driverService.get(1L), carService.get(1L));
        System.out.println(carService.getAllByDriver(1L));
        carService.removeDriverFromCar(driverService.get(1L), carService.get(1L));
    }
}
