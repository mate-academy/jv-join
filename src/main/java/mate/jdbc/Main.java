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
        Driver driverIvan = new Driver("Ivan", "1234");
        driverService.create(driverIvan);
        Driver driverPetro = new Driver("Petro", "5678");
        driverService.create(driverPetro);

        CarService carService = (CarService) injector.getInstance(CarService.class);
        Car carMazda = new Car("Mazda", manufacturerMazdaCorp);
        carMazda.setDrivers(new ArrayList<>(Arrays.asList(driverIvan, driverPetro)));
        carService.create(carMazda);
        Car carPorsche = new Car("Porsche", manufacturerPorscheCorp);
        carPorsche.setDrivers(new ArrayList<>(Arrays.asList(driverIvan)));
        carService.create(carPorsche);

        System.out.println("Petro drives: "
                + carService.getAllByDriver(driverPetro.getId()));

        System.out.println("Car: " + carService.get(carMazda.getId()));
        System.out.println("All cars: " + carService.getAll());

        carService.removeDriverFromCar(driverPetro, carMazda);
        carService.update(carMazda);
        System.out.println("Car Mazda after removing driver Petro: "
                + carService.get(carMazda.getId()));

        carService.delete(carPorsche.getId());
        System.out.println("All cars after deleting car Porsche: "
                + carService.getAll());
    }
}
