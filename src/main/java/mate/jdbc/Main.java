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
    private static final Injector injector = Injector.getInstance("mate.jdbc");

    public static void main(String[] args) {
        ManufacturerService manufacturerService =
                (ManufacturerService) injector.getInstance(ManufacturerService.class);
        Manufacturer manufacturerMazdaCorp = new Manufacturer("MazdaCorp", "Japan");
        manufacturerService.create(manufacturerMazdaCorp);
        Manufacturer manufacturerPorscheCorp = new Manufacturer("PorscheCorp", "Germany");
        manufacturerService.create(manufacturerPorscheCorp);

        DriverService driverService = (DriverService) injector.getInstance(DriverService.class);
        Driver driverLisa = new Driver("Lisa", "12345678");
        driverService.create(driverLisa);
        Driver driverOleg = new Driver("Oleg", "12345678");
        driverService.create(driverOleg);

        final CarService carService = (CarService) injector.getInstance(CarService.class);
        Car carMazda = new Car();
        carMazda.setModel("RX 6");
        carMazda.setManufacturer(manufacturerMazdaCorp);
        carMazda.setDrivers(new ArrayList<>(Arrays.asList(driverLisa, driverOleg)));
        carService.create(carMazda);
        Car carBmv = new Car();
        carBmv.setModel("Feraro");
        carBmv.setManufacturer(manufacturerPorscheCorp);
        carBmv.setDrivers(new ArrayList<>(Arrays.asList(driverLisa)));
        carService.create(carBmv);

        System.out.println("Peter drives: "
                + carService.getAllByDriver(driverOleg.getId()));

        System.out.println("Car: " + carService.get(carMazda.getId()));
        System.out.println("All cars: " + carService.getAll());

        carService.removeDriverFromCar(driverOleg, carMazda);
        carService.update(carMazda);
        System.out.println("Car Mazda after removing driver Oleg: "
                + carService.get(carMazda.getId()));

        carService.delete(carBmv.getId());
        System.out.println("All cars after deleting car Bmv: "
                + carService.getAll());
    }
}
