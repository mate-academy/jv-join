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
    private static final CarService CAR_SERVICE =
            (CarService) INJECTOR.getInstance(CarService.class);
    private static final DriverService DRIVER_SERVICE =
            (DriverService) INJECTOR.getInstance(DriverService.class);
    private static final ManufacturerService MANUFACTURER_SERVICE =
            (ManufacturerService) INJECTOR.getInstance(ManufacturerService.class);

    public static void main(String[] args) {
        Manufacturer toyota = getManufacturer("Toyota", "Japan");
        Driver anton = getDriver("Anton", "1234");
        Car car = getCar("Avensis", toyota, List.of(anton));
        System.out.println("Create Avensis with Anton: \n"
                           + CAR_SERVICE.create(car));
        car.setModel("Camry");
        System.out.println("Update Avensis to Camry with Anton: \n"
                           + CAR_SERVICE.update(car));
        Manufacturer bmw = getManufacturer("BMW", "Germany");
        Driver tanya = getDriver("Tanya", "1423");
        Car secondCar = getCar("E34", bmw, List.of(anton, tanya));
        System.out.println("Create E34 with Anton and Tanya: \n"
                           + CAR_SERVICE.create(secondCar));
        System.out.println("Get Avensis with Anton: \n"
                           + CAR_SERVICE.get(car.getId()));
        System.out.println("Get all cars: \n" + CAR_SERVICE.getAll());
        System.out.println("Get all cars with driver Anton: \n"
                           + CAR_SERVICE.getAllByDriver(anton.getId()));
        CAR_SERVICE.addDriverToCar(tanya, car);
        System.out.println("Add Tanya to Avensis " + car);
        CAR_SERVICE.removeDriverFromCar(tanya, car);
        System.out.println("Remove Tanya from Avensis " + car);
        System.out.println("Delete car E34 : \n" + CAR_SERVICE.delete(secondCar.getId()));
    }

    private static Car getCar(String model, Manufacturer manufacturer, List<Driver> drivers) {
        Car car = new Car();
        car.setModel(model);
        car.setManufacturer(manufacturer);
        car.setDrivers(drivers);
        return car;
    }

    private static Driver getDriver(String name, String licenseNumber) {
        Driver driver = new Driver();
        driver.setName(name);
        driver.setLicenseNumber(licenseNumber);
        Main.DRIVER_SERVICE.create(driver);
        return driver;
    }

    private static Manufacturer getManufacturer(String name, String country) {
        Manufacturer manufacturer = new Manufacturer();
        manufacturer.setName(name);
        manufacturer.setCountry(country);
        Main.MANUFACTURER_SERVICE.create(manufacturer);
        return manufacturer;
    }
}
