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
        ManufacturerService manufacturerService =
                (ManufacturerService) injector.getInstance(ManufacturerService.class);

        DriverService driverService = (DriverService) injector.getInstance(DriverService.class);

        Driver firstDriver = driverService.create(new Driver("Chris", "6743"));
        Car firstCar = new Car();
        firstCar.setModel("Kia");
        Manufacturer manufacturerKia = manufacturerService.create(
                new Manufacturer("Kia", "South Korea"));
        firstCar.setManufacturer(manufacturerService.get(manufacturerKia.getId()));
        firstCar.setDrivers(List.of(driverService.get(firstDriver.getId())));

        Driver secondDriver = driverService.create(new Driver("Philip", "8246"));
        Car secondCar = new Car();
        secondCar.setModel("Mercedes-Benz");
        Manufacturer manufacturerMercedes = manufacturerService.create(
                new Manufacturer("Mercedes-Benz", "Germany"));
        secondCar.setManufacturer(manufacturerService.get(manufacturerMercedes.getId()));
        secondCar.setDrivers(List.of(driverService.get(secondDriver.getId())));

        CarService carService = (CarService) injector.getInstance(CarService.class);

        carService.create(firstCar);
        carService.create(secondCar);
        carService.addDriverToCar(driverService.get(firstDriver.getId()),
                carService.get(firstCar.getId()));
        carService.removeDriverFromCar(driverService.get(secondDriver.getId()),
                carService.get(secondCar.getId()));

        Car carFromDb = carService.get(secondCar.getId());
        carFromDb.setModel("S400");
        System.out.println(carFromDb);
        carService.getAll().forEach(System.out::println);
        carService.delete(secondCar.getId());
        carService.update(carFromDb);
    }
}
