package mate.jdbc;

import java.util.ArrayList;
import java.util.Arrays;
import mate.jdbc.lib.Injector;
import mate.jdbc.model.Car;
import mate.jdbc.model.Driver;
import mate.jdbc.model.Manufacturer;
import mate.jdbc.service.CarService;
import mate.jdbc.service.DriverService;
import mate.jdbc.service.ManufacturerService;

public class Main {
    public static final Injector injector = Injector.getInstance("mate.jdbc");

    public static void main(String[] args) {
        ManufacturerService manufacturerService =
                (ManufacturerService) injector.getInstance(ManufacturerService.class);
        Manufacturer toyota = new Manufacturer("toyota", "japan");
        Manufacturer ford = new Manufacturer("ford", "usa");
        manufacturerService.create(toyota);
        manufacturerService.create(ford);

        DriverService driverService = (DriverService) injector.getInstance(DriverService.class);
        Driver alex = new Driver("alex", "123456");
        Driver dmitry = new Driver("dmitry", "234567");
        Driver igor = new Driver("igor", "345678");
        driverService.create(alex);
        driverService.create(dmitry);
        driverService.create(igor);

        CarService carService = (CarService) injector.getInstance(CarService.class);
        Car toyotaCar = new Car("mark", toyota, new ArrayList<>(Arrays.asList(alex, dmitry)));
        Car fordCar = new Car("fiesta", ford, new ArrayList<>(Arrays.asList(dmitry, igor)));

        carService.create(toyotaCar);
        carService.create(fordCar);

        System.out.println("Get all:");
        System.out.println(carService.getAll());

        System.out.println("Get toyota");
        System.out.println(carService.get(toyotaCar.getId()));

        System.out.println("Update ford from fiesta to focus");
        fordCar.setModel("focus");
        System.out.println(carService.update(fordCar));

        System.out.println("delete toyota");
        System.out.println(carService.delete(toyotaCar.getId()));

        System.out.println("add and remove driver");
        Driver ai = new Driver("ai", "098765");
        driverService.create(ai);
        carService.addDriverToCar(ai, fordCar);
        carService.removeDriverFromCar(ai, fordCar);

        System.out.println("get all cars by dmitry id");
        System.out.println(carService.getAllCarsById(dmitry.getId()));

    }
}
