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
    private static final Injector INJECTOR = Injector.getInstance("mate.jdbc");

    public static void main(String[] args) {
        CarService carService = (CarService) INJECTOR.getInstance(CarService.class);
        DriverService driverService = (DriverService) INJECTOR.getInstance(DriverService.class);
        ManufacturerService manufacturerService =
                (ManufacturerService) INJECTOR.getInstance(ManufacturerService.class);

        Manufacturer toyota = getManufacturer("Toyota", "Japan", manufacturerService);
        Driver anton = getDriver("Anton", "1234", driverService);
        Car car = getCar("Avensis", toyota, List.of(anton));
        System.out.println("Create Avensis with Anton: \n"
                           + carService.create(car));
        car.setModel("Camry");
        System.out.println("Update Avensis to Camry with Anton: \n"
                           + carService.update(car));
        Manufacturer bmw = getManufacturer("BMW", "Germany", manufacturerService);
        Driver tanya = getDriver("Tanya", "1423", driverService);
        Car secondCar = getCar("E34", bmw, List.of(anton, tanya));
        System.out.println("Create E34 with Anton and Tanya: \n"
                           + carService.create(secondCar));
        System.out.println("Get Avensis with Anton: \n"
                           + carService.get(car.getId()));
        System.out.println("Get all cars: \n" + carService.getAll());
        System.out.println("Get all cars with driver Anton: \n"
                           + carService.getAllByDriver(anton.getId()));
        carService.addDriverToCar(tanya, car);
        System.out.println("Add Tanya to Avensis " + car);
        carService.removeDriverFromCar(tanya, car);
        System.out.println("Remove Tanya from Avensis " + car);
        System.out.println("Delete car E34 : \n" + carService.delete(secondCar.getId()));
    }

    private static Car getCar(String model, Manufacturer manufacturer, List<Driver> drivers) {
        Car car = new Car();
        car.setModel(model);
        car.setManufacturer(manufacturer);
        car.setDrivers(drivers);
        return car;
    }

    private static Driver getDriver(
            String name,
            String licenseNumber,
            DriverService driverService) {
        Driver anton = new Driver();
        anton.setName(name);
        anton.setLicenseNumber(licenseNumber);
        driverService.create(anton);
        return anton;
    }

    private static Manufacturer getManufacturer(
            String name,
            String country,
            ManufacturerService manufacturerService) {
        Manufacturer bmw = new Manufacturer();
        bmw.setName(name);
        bmw.setCountry(country);
        manufacturerService.create(bmw);
        return bmw;
    }
}
