package mate.jdbc;

import mate.jdbc.lib.Injector;
import mate.jdbc.model.Car;
import mate.jdbc.model.Driver;
import mate.jdbc.model.Manufacturer;
import mate.jdbc.service.CarService;
import mate.jdbc.service.DriverService;
import mate.jdbc.service.ManufacturerService;

public class Main {
    public static void main(String[] args) {
        Car car = new Car();
        car.setModel("Sportage");
        Manufacturer manufacturer = new Manufacturer();
        manufacturer.setName("KIA");
        manufacturer.setCountry("South Korea");

        Injector injector = Injector.getInstance("mate.jdbc");

        ManufacturerService manufacturerService =
                (ManufacturerService) injector.getInstance(ManufacturerService.class);
        car.setManufacturer(manufacturerService.create(manufacturer));

        CarService carService =
                (CarService) injector.getInstance(CarService.class);
        Car newCar = carService.create(car);

        DriverService driverService =
                (DriverService) injector.getInstance(DriverService.class);
        Driver driverId3 = driverService.get(3L);
        Driver driverId7 = driverService.get(7L);
        carService.addDriverToCar(driverId3, newCar);
        carService.addDriverToCar(driverId7, newCar);

        Driver driver = driverService.get(7L);
        System.out.println("All cars where driver is with id: "
                + driver.getId() + System.lineSeparator());
        carService.getAllByDriver(driver.getId())
                .forEach(System.out::println);

        carService.removeDriverFromCar(driverId3, newCar);

        newCar.setModel("Sorento");
        carService.update(newCar);
        carService.delete(newCar.getId());

        carService.getAll();
    }
}
