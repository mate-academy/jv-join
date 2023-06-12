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
        Manufacturer manufacturerKia = new Manufacturer();
        manufacturerKia.setName("Kia");
        manufacturerKia.setCountry("South Korea");

        Manufacturer manufacturerMercedes = new Manufacturer();
        manufacturerMercedes.setName("Mercedes-Benz");
        manufacturerMercedes.setCountry("Germany");

        ManufacturerService manufacturerService =
                (ManufacturerService) injector.getInstance(ManufacturerService.class);
        manufacturerService.create(manufacturerKia);
        manufacturerService.create(manufacturerMercedes);

        DriverService driverService = (DriverService) injector.getInstance(DriverService.class);

        List<Driver> driversForKia = new ArrayList<>();
        driversForKia.add(driverService.get(1L));
        driversForKia.add(driverService.get(2L));

        List<Driver> driversForMercedes = new ArrayList<>();
        driversForMercedes.add(driverService.get(3L));
        driversForMercedes.add(driverService.get(4L));
        driversForMercedes.add(driverService.get(5L));

        Car car1 = new Car();
        car1.setModel("Kia");
        car1.setManufacturer(manufacturerService.get(10L));
        car1.setDrivers(driversForKia);

        Car car2 = new Car();
        car2.setModel("Mercedes-Benz");
        car2.setManufacturer(manufacturerService.get(11L));
        car2.setDrivers(driversForMercedes);

        CarService carService = (CarService) injector.getInstance(CarService.class);

        carService.create(car1);
        carService.create(car2);
        carService.addDriverToCar(driverService.get(5L), carService.get(4L));
        carService.removeDriverFromCar(driverService.get(3L), carService.get(5L));

        Car carFromDb = carService.get(6L);
        carFromDb.setModel("S400");
        System.out.println(carFromDb);
        carService.getAll().forEach(System.out::println);
        carService.delete(3L);
        carService.update(carFromDb);
    }
}
