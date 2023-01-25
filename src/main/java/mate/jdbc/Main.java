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
    private static final Injector injector = Injector.getInstance("mate");

    public static void main(String[] args) {
        CarService carService = (CarService) injector.getInstance(CarService.class);
        System.out.println(carService.get(3L));
        carService.delete(4L);
        carService.getAll().forEach(System.out::println);

        Driver driverOlexiy = new Driver();
        driverOlexiy.setName("Olexiy");
        driverOlexiy.setId(4L);
        driverOlexiy.setLicenseNumber("canDriveBikeAndCar159753");
        DriverService driverService = (DriverService) injector.getInstance(DriverService.class);
        driverService.create(driverOlexiy);
        Driver driverPavlo = new Driver();
        driverPavlo.setName("Pavlo");
        driverPavlo.setId(5L);
        driverPavlo.setLicenseNumber("canDriveCar753159846");
        driverService.create(driverPavlo);
        List<Driver> drivers = new ArrayList<>();
        drivers.add(driverOlexiy);
        drivers.add(driverPavlo);

        Manufacturer manufacturer = new Manufacturer();
        manufacturer.setId(20L);
        manufacturer.setName("Jeep");
        manufacturer.setCountry("USA");
        ManufacturerService manufacturerService =
                (ManufacturerService) injector.getInstance(ManufacturerService.class);
        manufacturerService.create(manufacturer);

        Car car = new Car();
        car.setDrivers(drivers);
        car.setManufacturer(manufacturer);
        car.setModel("Compass");
        car.setId(5L);

        carService.crate(car);
        car.setModel("cherokee");
        carService.update(car);

        carService.removeDriverFromCar(car,driverPavlo);
        carService.addDriverToCar(car,driverPavlo);

    }
}
