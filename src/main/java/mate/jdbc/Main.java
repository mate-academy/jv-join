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
    private static final ManufacturerService manufacturerService = (ManufacturerService) injector
            .getInstance(ManufacturerService.class);
    private static final DriverService driverService = (DriverService) injector
            .getInstance(DriverService.class);
    private static final CarService carService = (CarService) injector
            .getInstance(CarService.class);

    public static void main(String[] args) {
        Car car = new Car();
        car.setModel("Renault");
        car.setManufacturer(manufacturerService.get(1L));

        Manufacturer manufacturer = new Manufacturer();
        manufacturer.setName("RenaultHolding");
        manufacturer.setCountry("France");

        Driver driver = new Driver();
        driver.setName("Nitro");
        driver.setLicenseNumber("Qu99");
        Driver savedDriver = driverService.create(driver);

        Car savedCar = carService.create(car);
        carService.get(savedCar.getId());
        carService.getAll().forEach(System.out::println);

        carService.addDriverToCar(savedDriver, savedCar);

        Car newCar = new Car();
        newCar.setModel("Mercedes-Benz");
        newCar.setManufacturer(manufacturerService.get(manufacturer.getId()));
        newCar.setId(carService.get(car.getId()).getId());
        carService.update(newCar);

        List<Car> allByDriver = carService.getAllByDriver(savedDriver.getId());
        System.out.println(carService.get(newCar.getId()));
        System.out.println(allByDriver);

        carService.removeDriverFromCar(driverService.get(driver.getId()),
                carService.get(car.getId()));
        System.out.println(carService.get(car.getId()));
    }
}
