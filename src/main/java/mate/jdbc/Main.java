package mate.jdbc;

import java.util.ArrayList;
import java.util.List;
import mate.jdbc.lib.Injector;
import mate.jdbc.model.Car;
import mate.jdbc.model.Driver;
import mate.jdbc.service.CarService;
import mate.jdbc.service.DriverService;
import mate.jdbc.service.ManufacturerService;

public class Main {
    private static final Injector injector = Injector.getInstance("mate.jdbc");
    private static final String MANUFACTURER_CHINA_ID = "4";
    private static final String DRIVER_ABREK_ID = "7";
    private static final String DRIVER_BORIS_ID = "4";
    private static final String DRIVER_ANDRII_ID = "6";

    public static void main(String[] args) {
        System.out.println("Cars table before changes:");
        CarService carService = (CarService) injector.getInstance(CarService.class);
        System.out.println(carService.getAll());
        System.out.println("Adding new Geely car into DB");
        Car geely = new Car();
        geely.setModel("Geely Emgrand X7");
        ManufacturerService manufacturerService = (ManufacturerService) injector
                .getInstance(ManufacturerService.class);
        geely.setManufacturer(manufacturerService.get(Long.parseLong(MANUFACTURER_CHINA_ID)));
        DriverService driverService = (DriverService) injector.getInstance(DriverService.class);
        List<Driver> drivers = new ArrayList<>();
        drivers.add(driverService.get(Long.parseLong(DRIVER_ABREK_ID)));
        drivers.add(driverService.get(Long.parseLong(DRIVER_BORIS_ID)));
        geely.setDrivers(drivers);
        carService.create(geely);
        System.out.println("Getting Geely from DB:");
        System.out.println(carService.get(geely.getId()));
        System.out.println("Getting Geely from DB by driver Boris:");
        carService.getAllByDriver(Long.parseLong(DRIVER_BORIS_ID));
        System.out.println("Updating Geely model to Geely LG King Kong");
        geely.setModel("Geely LG King Kong");
        carService.update(geely);
        System.out.println("Deleting driver Abrek from Geely and adding Andrii");
        carService.removeDriverFromCar(driverService.get(Long.parseLong(DRIVER_ABREK_ID)),
                geely);
        carService.addDriverToCar(driverService.get(Long.parseLong(DRIVER_ANDRII_ID)),
                geely);
        System.out.println("Getting Geely from DB after changes:");
        System.out.println(carService.get(geely.getId()));
        System.out.println("Removing Geely from DB:");
        carService.delete(geely.getId());
        System.out.println("Cars table after changes:");
        System.out.println(carService.getAll());
    }
}
