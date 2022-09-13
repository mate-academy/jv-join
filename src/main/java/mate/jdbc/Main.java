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

    public static void main(String[] args) {
        ManufacturerService manufacturerService =
                (ManufacturerService) injector.getInstance(ManufacturerService.class);

        Manufacturer ferrari = new Manufacturer("Ferrari", "Italy");
        manufacturerService.create(ferrari);
        Manufacturer nissan = new Manufacturer(
                "Nissan", "Japan");
        manufacturerService.create(nissan);

        DriverService driverService =
                (DriverService) injector.getInstance(DriverService.class);

        List<Driver> ferrariDrivers = new ArrayList<>();
        Driver artem = new Driver("Artem", "260495");
        driverService.create(artem);
        ferrariDrivers.add(artem);
        Driver nazar = new Driver("Nazar", "270492");
        driverService.create(nazar);
        ferrariDrivers.add(nazar);

        List<Driver> nissanDrivers = new ArrayList<>();
        Driver george = new Driver("George", "211289");
        driverService.create(george);
        nissanDrivers.add(george);

        System.out.println("Run tests:");
        System.out.println("---------------------------------------------------------------------");
        CarService carService =
                (CarService) injector.getInstance(CarService.class);
        Car ferrariCar = new Car("250 Testa Rossa", ferrari, ferrariDrivers);
        System.out.println(carService.create(ferrariCar));
        System.out.println("Car " + ferrariCar.getManufacturer().getName()
                + " " + ferrariCar.getModel() + " was created successfully!");
        System.out.println();
        Car nissanCar = new Car("GT-R", nissan, nissanDrivers);
        System.out.println(carService.create(nissanCar));
        System.out.println("Car " + nissanCar.getManufacturer().getName()
                + " " + nissanCar.getModel() + " was created successfully!");
        System.out.println("_____________________________________________________________________");

        System.out.println("Get all data from the DB:");
        carService.getAll().forEach(System.out::println);
        System.out.println("---------------------------------------------------------------------");

        carService.addDriverToCar(george, ferrariCar);
        System.out.println(carService.get(ferrariCar.getId()));
        System.out.println(george.getName() + " was add to the car: "
                + ferrariCar.getManufacturer().getName() + " " + ferrariCar.getModel());
        System.out.println("_____________________________________________________________________");

        System.out.println("Get all by driver " + george.getName() + ":");
        System.out.println(carService.getAllByDriver(george.getId()));
        System.out.println("_____________________________________________________________________");

        System.out.println("Add driver " + george.getName() + " to the car "
                + ferrariCar.getManufacturer().getName() + " " + ferrariCar.getModel());
        System.out.println(ferrariCar.getManufacturer().getName() + " " + ferrariCar.getModel()
                + " has total driver amount before adding new driver: " + ferrariDrivers.size());
        carService.addDriverToCar(george, ferrariCar);
        System.out.println(ferrariCar.getManufacturer().getName() + " " + ferrariCar.getModel()
                + " has total driver amount after adding one new driver: " + ferrariDrivers.size());
        System.out.println("_____________________________________________________________________");

        System.out.println("Remove driver " + george.getName() + " from the car "
                + nissanCar.getManufacturer().getName() + " " + nissanCar.getModel());
        System.out.println(nissanCar.getManufacturer().getName() + " " + nissanCar.getModel()
                + " has total driver amount before deleting a driver: " + nissanDrivers.size());
        carService.removeDriverFromCar(george, nissanCar);
        System.out.println(nissanCar.getManufacturer().getName() + " "
                + nissanCar.getModel() + " has total driver amount after deleting one driver: "
                + nissanDrivers.size());
        System.out.println("_____________________________________________________________________");

        System.out.println("Delete car " + nissanCar.getManufacturer().getName()
                            + " " + nissanCar.getModel() + " from the DB");
        System.out.println(carService.delete(nissanCar.getId()));
        System.out.println("------------<The end of the tests------------");
    }
}
