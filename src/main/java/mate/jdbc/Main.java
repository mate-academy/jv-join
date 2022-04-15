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
        Manufacturer manufacturer = new Manufacturer("Volkswagen", "Germany");
        manufacturerService.create(manufacturer);

        DriverService driverService = (DriverService) injector.getInstance(DriverService.class);
        Driver firstDriver = new Driver("Paul", "B12356777");
        Driver secondDriver = new Driver("Jack", "B90877676");
        Driver thirdDriver = new Driver("Bob", "B141515155");
        Driver fourthDriver = new Driver("CJ", "B1234556622");
        driverService.create(firstDriver);
        driverService.create(secondDriver);
        driverService.create(thirdDriver);
        driverService.create(fourthDriver);

        List<Driver> volkswagenDrivers = new ArrayList<>();
        volkswagenDrivers.add(firstDriver);
        volkswagenDrivers.add(secondDriver);
        List<Driver> poloDrivers = new ArrayList<>();
        poloDrivers.add(thirdDriver);
        poloDrivers.add(fourthDriver);

        Car firstCar = new Car();
        firstCar.setModel("Passat");
        firstCar.setManufacturer(manufacturer);
        firstCar.setDrivers(volkswagenDrivers);

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
