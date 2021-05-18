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
        Manufacturer renaultManufacturer = new Manufacturer();
        renaultManufacturer.setName("RenaultHolding");
        renaultManufacturer.setCountry("France");

        Car renault = new Car();
        renault.setModel("Renault");
        renault.setManufacturer(renaultManufacturer);

        Driver firstDriver = new Driver();
        firstDriver.setName("Nitro");
        firstDriver.setLicenseNumber("Qu99");
        Driver secondDriver = new Driver();
        secondDriver.setName("Havok");
        secondDriver.setLicenseNumber("Pl57");

        Driver firstSavedDriver = driverService.create(firstDriver);
        Driver secondSavedDriver = driverService.create(secondDriver);
        Car savedCar = carService.create(renault);

        carService.getAll().forEach(System.out::println);

        carService.addDriverToCar(firstSavedDriver, savedCar);
        carService.addDriverToCar(secondSavedDriver, savedCar);

        Car benz = new Car();
        benz.setModel("Mercedes-Benz");
        benz.setManufacturer(manufacturerService.get(renaultManufacturer.getId()));

        Car currentCar = carService.get(savedCar.getId());
        benz.setId(currentCar.getId());
        carService.update(benz);

        List<Car> allByFirstDriver = carService.getAllByDriver(firstSavedDriver.getId());
        List<Car> allBySecondDriver = carService.getAllByDriver(secondSavedDriver.getId());

        System.out.println(allByFirstDriver);
        System.out.println(allBySecondDriver);

        Driver driverForDeleteId = driverService.get(firstSavedDriver.getId());
        Car carForDelete = carService.get(benz.getId());
        carService.removeDriverFromCar(driverForDeleteId, carForDelete);

        carService.getAll().forEach(System.out::println);
    }
}
