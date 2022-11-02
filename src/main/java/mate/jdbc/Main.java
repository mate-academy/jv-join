package mate.jdbc;

import java.util.List;
import java.util.Optional;
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
        DriverService driverService = (DriverService) injector.getInstance(DriverService.class);
        CarService carService = (CarService) injector.getInstance(CarService.class);
        Manufacturer bogdan = manufacturerService.get(5L);
        final Driver driver = driverService.get(3L);
        Car tractor = new Car("Madagaskar",bogdan,List.of(driver));
        carService.create(tractor);
        System.out.println(carService.get(1L));
        Driver fedya = new Driver("fedya","de987456");
        Driver misha = new Driver("misha","au111111");
        Driver vitya = driverService.get(4L);
        Manufacturer getbmw = manufacturerService.get(12L);
        System.out.println(vitya);
        Car car = new Car("Land Cruser",getbmw, List.of(vitya));
        System.out.println(carService.create(car));
        List<Car> cars = carService.getAll();
        System.out.println(cars);
        carService.delete(2L);
        List<Car> allCars = carService.getAll();
        allCars.forEach((x) -> System.out.println(x));
        List<Car> carsByDriver = carService.getAllByDriver(1L);
        carsByDriver.forEach((x) -> System.out.println(x));
        Driver driver3 = driverService.get(3L);
        Optional<Car> getCar = carService.get(33L);
        carService.addDriverToCar(driver,car);
        Driver driver1 = driverService.get(1L);
        Driver driver5 = driverService.get(5L);
        Manufacturer man = manufacturerService.get(13L);
        Car newCar = new Car(35L,"ARMAGEDON",man,List.of(driver1,driver5));
        System.out.println(carService.update(newCar));
    }
}
