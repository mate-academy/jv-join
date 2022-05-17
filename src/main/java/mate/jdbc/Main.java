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

        Car createdCar = carService.create(getCar());

        carService.getAll();

        carService.getAllByDriver(getDriver().getId());

        Car carToUpdate = carService.get(createdCar.getId());
        carToUpdate.setModel("Updated");
        carService.update(carToUpdate);

        carService.delete(carToUpdate.getId());

        carService.addDriverToCar(getDriver(), carToUpdate);

        carService.removeDriverFromCar(getDriver(), carToUpdate);
    }

    private static Car getCar() {
        Car car = new Car();
        car.setModel("TestModel");
        car.setManufacturer(getManufacturer());
        car.setDrivers(getListOfDrivers());
        return car;
    }

    private static Driver getDriver() {
        DriverService driverService = (DriverService) injector.getInstance(DriverService.class);
        return driverService.create(new Driver("DriverName", "License"));
    }

    private static Manufacturer getManufacturer() {
        ManufacturerService manufacturerService = (ManufacturerService) injector
                .getInstance(ManufacturerService.class);
        return manufacturerService.create(new Manufacturer("TestName", "TestCountry"));
    }

    private static List<Driver> getListOfDrivers() {
        DriverService driverService = (DriverService) injector.getInstance(DriverService.class);
        List<Driver> driversList = new ArrayList<>();
        driversList.add(driverService.create(new Driver("DriverOne", "LicNum1")));
        driversList.add(driverService.create(new Driver("DriverTwo", "LicNum2")));
        return driversList;
    }
}
