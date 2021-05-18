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
        Manufacturer bmw = new Manufacturer("BMV", "Ukraine");
        Manufacturer tesla = new Manufacturer("Tesla", "USA");

        bmw = manufacturerService.create(bmw);
        tesla = manufacturerService.create(tesla);

        DriverService driverService = (DriverService) injector.getInstance(DriverService.class);
        Driver spencer = new Driver("Spencer", "#925872");
        Driver chris = new Driver("Chris", "#978362");
        Driver tallyn = new Driver("Tallyn", "#741012");

        spencer = driverService.create(spencer);
        tallyn = driverService.create(tallyn);

        List<Driver> drivers1 = new ArrayList<>();
        drivers1.add(spencer);
        drivers1.add(tallyn);
        Car modelS = new Car("Model S", tesla, drivers1);
        List<Driver> drivers2 = new ArrayList<>();
        drivers2.add(spencer);
        Car x6 = new Car("X6", bmw, drivers2);

        CarService carService = (CarService) injector.getInstance(CarService.class);
        modelS = carService.create(modelS);
        x6 = carService.create(x6);

        System.out.println(carService.getAll());
        System.out.println(carService.get(modelS.getId()));

        System.out.println("__________________");
        modelS.setModel("Model S+");
        carService.update(modelS);
        System.out.println(carService.get(modelS.getId()));
        System.out.println("__________________");

        System.out.println(carService.delete(bmw.getId()));
        System.out.println(carService.getAll());
        System.out.println("__________________");

        System.out.println(carService.getAllByDriver(spencer.getId()));

        carService.removeDriverFromCar(spencer, x6);
        System.out.println(carService.get(x6.getId()));

        carService.addDriverToCar(tallyn, x6);
        System.out.println(carService.get(x6.getId()));
    }
}
