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
    private static final Injector injector
            = Injector.getInstance("mate.jdbc");
    private static final ManufacturerService manufacturerService =
            (ManufacturerService) injector.getInstance(ManufacturerService.class);
    private static final DriverService driverService =
            (DriverService) injector.getInstance(DriverService.class);
    private static final CarService carService =
            (CarService) injector.getInstance(CarService.class);

    public static void main(String[] args) {
        Manufacturer ford = new Manufacturer("Ford", "USA");
        Manufacturer nissan = new Manufacturer("Nissan", "Japan");
        Manufacturer toyota = new Manufacturer("Toyota", "Japan");

        Driver bogdan = new Driver("Bogdan", "12345");
        Driver oleksandr = new Driver("Oleksandr", "12378");
        Driver maksim = new Driver("Maksim", "12378");
        Driver oleg = new Driver("Oleg", "12345");

        Car fordModel = new Car("Mustang", ford, new ArrayList<>());
        fordModel.getDrivers().add(bogdan);
        Car nissanModel = new Car("Skyline R-34", nissan, new ArrayList<>());
        nissanModel.getDrivers().add(oleksandr);
        nissanModel.getDrivers().add(maksim);
        Car toyotaModel = new Car("Supra A70", toyota, new ArrayList<>());
        toyotaModel.getDrivers().add(oleg);

        manufacturerService.create(ford);
        manufacturerService.create(nissan);
        manufacturerService.create(toyota);

        driverService.create(bogdan);
        driverService.create(oleksandr);
        driverService.create(maksim);
        driverService.create(oleg);

        carService.create(fordModel);
        carService.create(nissanModel);
        carService.create(toyotaModel);

        System.out.println("The Car table has been supplemented "
                + "with the following values: \n"
                + fordModel + "\n"
                + nissanModel + "\n"
                + toyotaModel + "\n");

        System.out.println("Getting car by id:");
        System.out.println(carService.get(fordModel.getId()));

        System.out.println("\nUpdate drivers:");
        fordModel.setDrivers(List.of(oleksandr));
        carService.update(fordModel);
        System.out.println(carService.get(fordModel.getId()));

        System.out.println("\nUpdate model: ");
        toyotaModel.setModel("Corolla AE86");
        carService.update(toyotaModel);
        System.out.println(carService.get(toyotaModel.getId()));

        carService.removeDriverFromCar(oleksandr, toyotaModel);
        System.out.println("\nThe Driver was removed from the car: \n"
                + carService.getAllByDriver(oleg.getId()) + "\n");

        System.out.println("The Car was deleted: \n"
                + carService.delete(toyotaModel.getId()));

        System.out.println("\nThe Car table: \n"
                + carService.getAll());
    }
}
