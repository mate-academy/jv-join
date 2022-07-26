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
    private static Injector injector = Injector.getInstance("mate.jdbc");

    public static void main(String[] args) {
        CarService carService = (CarService) injector.getInstance(CarService.class);
        DriverService driverService = (DriverService) injector.getInstance(DriverService.class);
        ManufacturerService manufacturerService
                = (ManufacturerService) injector.getInstance(ManufacturerService.class);
        List<Driver> drivers = new ArrayList<>();
        drivers.add(driverService.create(new Driver("John", "1257")));
        Manufacturer mersedes = manufacturerService.get(6L);
        Car maybach = new Car("Maybach", mersedes , drivers);
        maybach.setId(26L);
        carService.create(maybach);
        System.out.println(carService.get(maybach.getId()));

        drivers.add(driverService.create(new Driver("Poll", "3698")));

        maybach.setDrivers(drivers);
        carService.update(maybach);
        System.out.println(carService.get(maybach.getId()));

        carService.getAll().forEach(System.out::println);

        Driver driverBill = new Driver();
        driverBill.setName("Bill");
        driverBill.setLicenseNumber("2589");
        driverService.create(driverBill);

        carService.addDriverToCar(driverBill, maybach);

        carService.getAllByDriver(driverBill.getId()).forEach(System.out::println);

        carService.removeDriverFromCar(driverBill, maybach);
        System.out.println(carService.get(maybach.getId()));

        carService.delete(maybach.getId());
        carService.getAll().forEach(System.out::println);
    }
}
