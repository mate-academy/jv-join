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
        CarService carService = (CarService) injector.getInstance(CarService.class);
        ManufacturerService manufacturerService =
                (ManufacturerService) injector.getInstance(ManufacturerService.class);
        DriverService driverService =
                (DriverService) injector.getInstance(DriverService.class);

        Driver driverAlice = driverService.get(3L);
        Driver driverOleh = driverService.get(4L);
        Driver driverAlex = driverService.get(5L);

        Manufacturer manufacturerVW = manufacturerService.get(2L);
        Manufacturer manufacturerSubaru = manufacturerService.get(3L);

        Car carVW = new Car();
        carVW.setModel("Transporter");
        carVW.setManufacturer(manufacturerVW);
        List<Driver> driversVW = new ArrayList<>();
        driversVW.add(driverOleh);
        carVW.setDrivers(driversVW);

        carService.create(carVW);
        carVW = carService.get(1L);
        System.out.println(carVW);

        carVW.setModel("Golf");
        carService.update(carVW);
        carVW = carService.get(1L);
        System.out.println(carVW);

        Car carForester = new Car();
        carForester.setModel("Forester");
        carForester.setManufacturer(manufacturerSubaru);
        List<Driver> driversForester = new ArrayList<>();
        driversForester.add(driverOleh);
        driversForester.add(driverAlice);
        carForester.setDrivers(driversForester);

        carForester = carService.create(carForester);
        carService.delete(carForester.getId());

        carService.getAll().forEach(System.out::println);
        System.out.println();
        carService.getAllByDriver(driverOleh.getId()).forEach(System.out::println);

        carService.addDriverToCar(driverAlex, carForester);
        carService.get(carForester.getId());

        carService.removeDriverFromCar(driverAlice, carForester);
        carService.get(carForester.getId());
    }
}
