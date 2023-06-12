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
import mate.jdbc.service.impl.ClearAll;

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
        ClearAll clearAll = new ClearAll();
        clearAll.clearAllFromTable();

        Manufacturer ford = new Manufacturer("Ford", "USA");
        Manufacturer nissan = new Manufacturer("Nissan", "Japan");
        Manufacturer bmw = new Manufacturer("BMW", "USA");

        Driver anatoliy = new Driver("Anatoliy", "12345");
        Driver valeriy = new Driver("Valeriy", "12378");
        Driver stepan = new Driver("Stepan", "12378");
        Driver illia = new Driver("Illia", "12345");

        Car fordModel = new Car("Mustang", ford, new ArrayList<>());
        fordModel.getDrivers().add(anatoliy);
        Car nissanModel = new Car("Altima", nissan, new ArrayList<>());
        nissanModel.getDrivers().add(valeriy);
        nissanModel.getDrivers().add(stepan);
        Car bmwModel = new Car("X5", bmw, new ArrayList<>());
        bmwModel.getDrivers().add(illia);

        manufacturerService.create(ford);
        manufacturerService.create(nissan);
        manufacturerService.create(bmw);

        driverService.create(anatoliy);
        driverService.create(valeriy);
        driverService.create(stepan);
        driverService.create(illia);

        carService.create(fordModel);
        carService.create(nissanModel);
        carService.create(bmwModel);

        System.out.println("The Car table has been supplemented "
                + "with the following values: \n"
                + fordModel + "\n"
                + nissanModel + "\n"
                + bmwModel + "\n");

        System.out.println("Getting car by id:");
        System.out.println(carService.get(fordModel.getId()));

        System.out.println("\nUpdate drivers:");
        fordModel.setDrivers(List.of(valeriy));
        carService.update(fordModel);
        System.out.println(carService.get(fordModel.getId()));

        System.out.println("\nUpdate model: ");
        bmwModel.setModel("x10");
        carService.update(bmwModel);
        System.out.println(carService.get(bmwModel.getId()));

        carService.removeDriverFromCar(valeriy, bmwModel);
        System.out.println("\nThe Driver was removed from the car: \n"
                + carService.getAllByDriver(illia.getId()) + "\n");

        System.out.println("The Car was deleted: \n"
                + carService.delete(bmwModel.getId()));

        System.out.println("\nThe Car table: \n"
                + carService.getAll());

    }
}
