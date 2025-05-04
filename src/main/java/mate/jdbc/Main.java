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

    public static void main(String[] args) {
        ManufacturerService manufacturerService =
                (ManufacturerService) injector.getInstance(ManufacturerService.class);
        Manufacturer manufacturerFord = new Manufacturer("Ford", "USA");
        Manufacturer manufacturerBogdan = new Manufacturer("Bogdan", "Ukraine");
        manufacturerService.create(manufacturerFord);
        manufacturerService.create(manufacturerBogdan);

        DriverService driverService = (DriverService) injector.getInstance(DriverService.class);
        Driver driverOlga = new Driver("Olga", "OL20");
        Driver driverClaus = new Driver("Claus", "C89");
        Driver driverLinda = new Driver("Linda", "L007");
        driverService.create(driverOlga);
        driverService.create(driverLinda);
        driverService.create(driverClaus);

        CarService carService = (CarService) injector.getInstance(CarService.class);
        Car lincoln = new Car("Lincoln", manufacturerFord, List.of(driverLinda, driverClaus));
        Car ford = new Car("Ford", manufacturerFord, List.of(driverClaus, driverOlga));
        Car zaz = new Car("ZAZ", manufacturerBogdan, List.of(driverOlga));
        carService.create(lincoln);
        carService.create(ford);
        carService.create(zaz);
        System.out.println("Get all cars from DB: " + System.lineSeparator()
                + carService.getAll());

        System.out.println("Get car by id = " + lincoln.getId() + ": "
                + carService.get(lincoln.getId()));

        System.out.println("Get all cars by driver - " + driverClaus.getName()
                + ": " + carService.getAllByDriver(driverClaus.getId()));

        zaz.setModel("Devyatka");
        System.out.println("Get update model in car with id = " + zaz.getId()
                + ": " + carService.update(zaz));

        carService.addDriverToCar(driverLinda, zaz);
        System.out.println("Add new driver " + driverLinda.getName()
                + " to car " + zaz.getModel() + ": " + carService.get(zaz.getId()));

        carService.removeDriverFromCar(driverLinda, lincoln);
        System.out.println("Remove driver " + driverLinda.getName()
                + " from car " + lincoln.getModel() + ": " + carService.get(lincoln.getId()));

        carService.delete(ford.getId());
        System.out.println("Get all cars from DB after removed car by id = " + ford.getId()
                + System.lineSeparator() + carService.getAll());
    }
}
