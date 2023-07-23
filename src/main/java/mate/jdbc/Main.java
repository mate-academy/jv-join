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
    private static final ManufacturerService manufacturerService =
            (ManufacturerService) injector.getInstance(ManufacturerService.class);
    private static final DriverService driverService =
            (DriverService) injector.getInstance(DriverService.class);
    private static final CarService carService =
            (CarService) injector.getInstance(CarService.class);

    public static void main(String[] args) {
        Manufacturer manufacturer = new Manufacturer();
        manufacturer.setName("Peugeot");
        manufacturer.setCountry("France");
        manufacturerService.create(manufacturer);
        Driver driverOne = new Driver();
        driverOne.setName("Andrii Zhuk");
        driverOne.setLicenseNumber("DD5555");
        Driver driverTwo = new Driver();
        driverTwo.setName("Igor Melnyk");
        driverTwo.setLicenseNumber("HH7777");
        Driver driverThird = new Driver();
        driverThird.setName("Taras Kulish");
        driverThird.setLicenseNumber("JJ8888");
        driverService.create(driverOne);
        driverService.create(driverTwo);
        driverService.create(driverThird);
        List<Driver> driversList = new ArrayList<>();
        driversList.add(driverOne);
        driversList.add(driverTwo);
        Car car = new Car();
        car.setDrivers(driversList);
        car.setManufacturer(manufacturer);
        car.setModel("307");
        carService.create(car);
        carService.get(car.getId());
        carService.update(car);
        carService.getAll().forEach(System.out::println);
        carService.addDriverToCar(driverThird, car);
        carService.removeDriverFromCar(driverOne, car);
        System.out.println(carService.getAllByDriver(driverTwo.getId()));
        carService.delete(car.getId());

    }
}
