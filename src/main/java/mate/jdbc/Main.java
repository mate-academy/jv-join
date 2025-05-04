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
        Manufacturer manufacturerByd = new Manufacturer(null, "BYD", "China");
        manufacturerService.create(manufacturerByd);
        Driver firstDriver = new Driver(null, "FirstDriverName", "11111111111");
        Driver seconfDriver = new Driver(null, "SecondDriverName", "22222222222");
        Driver thirdDriver = new Driver(null,"ThirdDriverName","33333333333");
        driverService.create(firstDriver);
        driverService.create(seconfDriver);
        driverService.create(thirdDriver);
        System.out.println(driverService.getAll());
        Car car = new Car();
        car.setModel("Tang");
        car.setManufacturer(manufacturerByd);
        car.setDrivers(List.of(firstDriver, seconfDriver));
        CarService carService = (CarService) injector.getInstance(CarService.class);
        carService.create(car);
        car = carService.get(1L);
        car.setModel("Han EV");
        carService.addDriverToCar(thirdDriver,car);
        carService.removeDriverFromCar(seconfDriver, car);
        carService.getAll().forEach(System.out::println);
        carService.getAllByDriver(1L).forEach(System.out::println);
        carService.delete(1L);
        carService.getAll().forEach(System.out::println);
    }
}
