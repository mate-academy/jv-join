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
        ManufacturerService manufacturerService = (ManufacturerService)
                injector.getInstance(ManufacturerService.class);
        Manufacturer manufacturer = new Manufacturer("Toyota", "Japan");
        manufacturerService.create(manufacturer);

        DriverService driverService = (DriverService) injector.getInstance(DriverService.class);
        Driver firstDriver = new Driver("Peter", "B12456777");
        Driver secondDriver = new Driver("Bob", "B8769849");
        Driver thirdDriver = new Driver("Jack", "874550907");
        Driver fourthDriver = new Driver("Snow", "90859874");
        driverService.create(firstDriver);
        driverService.create(secondDriver);
        driverService.create(thirdDriver);
        driverService.create(fourthDriver);

        List<Driver> toyotaDrivers = new ArrayList<>();
        toyotaDrivers.add(firstDriver);
        toyotaDrivers.add(secondDriver);
        List<Driver> corollaDrivers = new ArrayList<>();
        corollaDrivers.add(thirdDriver);
        corollaDrivers.add(fourthDriver);

        Car firstCar = new Car();
        firstCar.setModel("Passat");
        firstCar.setManufacturer(manufacturer);
        firstCar.setDrivers(toyotaDrivers);

        CarService carService = (CarService) injector.getInstance(CarService.class);
        carService.create(firstCar);
        carService.update(firstCar);
        System.out.println(carService.get(1L));
        carService.delete(firstCar.getId());
        carService.addDriverToCar(firstDriver, firstCar);
        carService.removeDriverFromCar(firstDriver, firstCar);
        carService.getAll().forEach(System.out::println);
        System.out.println(carService.getAllByDriver(1L));

    }
}
