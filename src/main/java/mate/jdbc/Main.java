package mate.jdbc;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
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
        DriverService driverService = (DriverService) injector.getInstance(DriverService.class);
        List<Driver> drivers = new ArrayList<>();
        drivers.add(new Driver("Kim", "TYGW34GH12Q"));
        drivers.add(new Driver("Choli", "HK3H662SDF2"));
        drivers.add(new Driver("Chonsuk", "QEF124GS8H7"));
        drivers.add(new Driver("Chongkuk", "FYI45GW359GD"));
        for (Driver driver: drivers) {
            driverService.create(driver);
        }
        /* <-----------------------------------------> */
        ManufacturerService manufacturerService = (ManufacturerService) injector
                .getInstance(ManufacturerService.class);
        List<Manufacturer> manufacturers = new ArrayList<>();
        manufacturers.add(new Manufacturer("Bob", "France"));
        manufacturers.add(new Manufacturer("Bobby", "British"));
        manufacturers.add(new Manufacturer("Bober", "Italy"));
        manufacturers.add(new Manufacturer("Bobrik", "Poland"));
        for (Manufacturer manufacturer: manufacturers) {
            manufacturerService.create(manufacturer);
        }
        /* <-----------------------------------------> */
        CarService carService = (CarService) injector.getInstance(CarService.class);
        List<Car> cars = new ArrayList<>();
        List<Driver> driverListFirstCar = drivers.stream()
                .filter(d -> d.getId() % 2 == 0).collect(Collectors.toList());
        List<Driver> driverListSecondCar = drivers.stream()
                .filter(d -> d.getId() % 2 == 1).collect(Collectors.toList());
        cars.add(new Car("Jeep 1997", manufacturers.get(1), driverListFirstCar));
        cars.add(new Car("Tesla Model X", manufacturers.get(3), driverListSecondCar));
        for (Car car: cars) {
            carService.create(car);
        }
        carService.getAll().forEach(System.out::println);
        Car car = carService.get(cars.stream().findFirst().get().getId());
        System.out.println(car);
        carService.addDriverToCar(driverService.create(new Driver("SomeGuy", "QEWRAFD234")), car);
        System.out.println(carService.get(car.getId()));
        carService.removeDriverFromCar(drivers.get(1), cars.get(1));
        System.out.println(carService.get(cars.get(1).getId()));
        carService.delete(cars.stream().findFirst().get().getId());
        carService.getAll().forEach(System.out::println);
        List<Car> allByDriver = carService.getAllByDriver(cars.get(1).getId());
        allByDriver.forEach(System.out::println);
    }
}
