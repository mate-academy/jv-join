package mate.jdbc;

import mate.jdbc.lib.Injector;
import mate.jdbc.model.Car;
import mate.jdbc.model.Driver;
import mate.jdbc.model.Manufacturer;
import mate.jdbc.service.CarService;
import mate.jdbc.service.DriverService;

public class Main {
    private static final Injector injector = Injector.getInstance("mate.jdbc");
    public static void main(String[] args) {
        DriverService driverService = (DriverService) injector.getInstance(DriverService.class);
        Driver driver = new Driver("Viktoriia", "55555");
        driverService.create(driver);

        Manufacturer manufacturer = new Manufacturer(null, "Pegeout", "France");

        CarService carService = (CarService) injector.getInstance(CarService.class);
        Car car = new Car();
        car.setManufacturer(manufacturer);
        car.setModel("306");
        car.set
                //add more data to DB
    }
}
