package mate.jdbc;

import java.util.ArrayList;
import mate.jdbc.lib.Injector;
import mate.jdbc.model.Car;
import mate.jdbc.model.Driver;
import mate.jdbc.model.Manufacturer;
import mate.jdbc.service.CarService;
import mate.jdbc.service.DriverService;
import mate.jdbc.service.ManufacturerService;

public class Main {
    private static Injector injector = Injector.getInstance("mate.jdbc");
    static final DriverService driverService
            = (DriverService) injector.getInstance(DriverService.class);
    static final ManufacturerService manufacturerService
            = (ManufacturerService) injector.getInstance(ManufacturerService.class);
    static final CarService carService = (CarService) injector.getInstance(CarService.class);

    public static void main(String[] args) {
        Driver driverVasyl = new Driver("Vasyl", "B129814");
        Driver driverBohdan = new Driver("Bohdan", "X129423");
        Driver driverKhrystyna = new Driver("Khrystyna", "K028472");

        driverService.create(driverVasyl);
        driverService.create(driverBohdan);
        driverService.create(driverKhrystyna);
        System.out.println("Insert drivers to DB");

        Manufacturer manufacturerZaz = new Manufacturer("ZAZ", "Ukraine");
        Manufacturer manufacturerJaguar = new Manufacturer("Jaguar", "UK");
        Manufacturer manufacturerFord = new Manufacturer("Ford", "USA");

        manufacturerService.create(manufacturerFord);
        manufacturerService.create(manufacturerJaguar);
        manufacturerService.create(manufacturerZaz);
        System.out.println("Insert manufacturers to DB");

        Car zaz = new Car("986", manufacturerZaz, new ArrayList<>());
        zaz.getDrivers().add(driverBohdan);
        zaz.getDrivers().add(driverVasyl);

        Car jaguar = new Car("S-Type", manufacturerJaguar, new ArrayList<>());
        jaguar.getDrivers().add(driverVasyl);

        Car ford = new Car("Fiesta", manufacturerFord, new ArrayList<>());
        ford.getDrivers().add(driverKhrystyna);

        carService.create(zaz);
        carService.create(jaguar);
        carService.create(ford);
        System.out.println("Create cars and add drivers to them");

        System.out.println("All cars: " + carService.getAll());

        carService.addDriverToCar(driverBohdan, ford);
        carService.removeDriverFromCar(driverVasyl, zaz);
        System.out.println("Remove drivers from cars");

        System.out.println("All car after  remove:" + carService.getAll());

        ford.setModel("Focus");
        carService.update(ford);
        System.out.println("Update car model");

        System.out.println(carService.getAllByDriver(driverVasyl.getId()));
        System.out.println("Get all cars by driver id");

        carService.delete(ford.getId());
        System.out.println("Delete car ford by id");

        System.out.println(carService.get(zaz.getId()));
        System.out.println("Get car zaz by id");
    }
}
