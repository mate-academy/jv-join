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

    public static void main(String[] args) {
        ManufacturerService manufacturerService = (ManufacturerService)
                injector.getInstance(ManufacturerService.class);
        DriverService driverService = (DriverService)
                injector.getInstance(DriverService.class);
        Manufacturer manufacturerHyundai = new Manufacturer(null, "hyundai", "Korea");
        manufacturerService.create(manufacturerHyundai);
        Driver driverIvanov = new Driver(null, "Ivanov", "ABC123456");
        Driver driverPetrov = new Driver(null, "Oleksandr", "CDE678901");
        Driver driverSurkov = new Driver(null,"Surkov","EFG123456");
        driverService.create(driverIvanov);
        driverService.create(driverPetrov);
        driverService.create(driverSurkov);
        System.out.println(driverService.getAll());
        Car car = new Car();
        car.setModel("honda");
        car.setManufacturer(manufacturerHyundai);
        car.setDrivers(List.of(driverIvanov, driverPetrov));
        CarService carService = (CarService) injector.getInstance(CarService.class);
        carService.create(car);
        car = carService.get(1L);
        car.setModel("suzuki");
        carService.addDriverToCar(driverSurkov,car);
        carService.removeDriverFromCar(driverPetrov, car);
        carService.getAll().forEach(System.out::println);
        carService.getAllByDriver(1L).forEach(System.out::println);
        carService.delete(1L);
        carService.getAll().forEach(System.out::println);
    }
}
