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
        DriverService driverService
                = (DriverService) injector.getInstance(DriverService.class);
        CarService carService =
                (CarService) injector.getInstance(CarService.class);

        Manufacturer bmw = manufacturerService
                .create(new Manufacturer("Mercedes", "Germany"));

        List<Driver> drivers = new ArrayList<>();
        Driver oleg = driverService.create(new Driver("Oleg", "RV001"));
        Driver illya = driverService.create(new Driver("Illya", "RV002"));
        drivers.add(oleg);

        Car bmwCar = new Car("M5", bmw, drivers);
        carService.create(bmwCar);
        carService.addDriverToCar(illya, bmwCar);
        carService.removeDriverFromCar(oleg, bmwCar);
        bmwCar.setModel("M3");
        carService.update(bmwCar);
        System.out.println(carService.getAll());
        carService.delete(bmwCar.getId());
        System.out.println(carService.getAll());
    }
}
