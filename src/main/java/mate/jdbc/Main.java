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

        ManufacturerService manufacturerService =
                (ManufacturerService) injector.getInstance(ManufacturerService.class);
        Manufacturer teslaCar = new Manufacturer("Tesla", "USA");
        manufacturerService.create(teslaCar);

        DriverService driverService =
                (DriverService) injector.getInstance(DriverService.class);
        List<Driver> drivers = new ArrayList<>();
        Driver garryKasparov = new Driver("Garry Kasparov", "1234");
        Driver bobbyFischer = new Driver("Bobby Fischer", "5678");
        driverService.create(garryKasparov);
        driverService.create(bobbyFischer);

        CarService carService =
                (CarService) injector.getInstance(CarService.class);
        Car topTeslaCar = new Car("Top", teslaCar, drivers);
        carService.create(topTeslaCar);
        Car averageTeslaCar = new Car("average", teslaCar, drivers);
        carService.create(averageTeslaCar);

        carService.addDriverToCar(garryKasparov, topTeslaCar);
        carService.addDriverToCar(garryKasparov, averageTeslaCar);
        carService.addDriverToCar(bobbyFischer, averageTeslaCar);
        carService.addDriverToCar(bobbyFischer, topTeslaCar);
        drivers.add(garryKasparov);
        drivers.add(bobbyFischer);
        System.out.println("Get all before operations: "
                + carService.getAll());

        System.out.println("Get car: " + carService.get(topTeslaCar.getId()));

        carService.removeDriverFromCar(garryKasparov, topTeslaCar);
        System.out.println("Get all after remove operation: "
                + carService.getAll());

        carService.addDriverToCar(garryKasparov, topTeslaCar);
        System.out.println("Get cars via driver`s id: "
                + carService.getAllByDriver(garryKasparov.getId()));

        topTeslaCar.setModel("Most expensive Tesla");
        carService.update(topTeslaCar);
        System.out.println("Get car after update: "
                + carService.get(topTeslaCar.getId()));

        System.out.println("Get all by drivers id: "
                + carService.getAllByDriver(bobbyFischer.getId()));

    }
}
