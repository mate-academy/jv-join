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
        ManufacturerService manufacturerService
                = (ManufacturerService) injector.getInstance(ManufacturerService.class);
        Manufacturer manufacturerChallenger = new Manufacturer("Challenger", "USA");
        Manufacturer manufacturerReatta = new Manufacturer("Reatta", "Germany");
        manufacturerService.create(manufacturerChallenger);
        manufacturerService.create(manufacturerReatta);

        Driver driverAzamat = new Driver("Azamat", "PAR156834");
        Driver driverVazgen = new Driver("Vazgen", "GLO145687");
        Driver driverBarak = new Driver("Barak", "KOD258645");
        Driver driverAhmed = new Driver("Ahmed", "DON357951");

        DriverService driverService
                = (DriverService) injector.getInstance(DriverService.class);
        driverService.create(driverAzamat);
        driverService.create(driverVazgen);
        driverService.create(driverBarak);
        driverService.create(driverAhmed);

        List<Driver> driverListDodge = new ArrayList<>();
        List<Driver> driversListBuick = new ArrayList<>();

        driverListDodge.add(driverAzamat);
        driverListDodge.add(driverBarak);
        driversListBuick.add(driverVazgen);
        driversListBuick.add(driverAhmed);

        CarService carService = (CarService) injector.getInstance(CarService.class);
        Car carModelDodge = new Car("Challenger", manufacturerChallenger, driverListDodge);
        Car carModelBuick = new Car("Reatta", manufacturerReatta, driversListBuick);
        carService.create(carModelDodge);
        carService.create(carModelBuick);

        carService.getAllByDriver(carModelDodge.getId());
        carModelDodge.setManufacturer(manufacturerService.get(manufacturerChallenger.getId()));
        carService.update(carModelDodge);
        carService.getAll().forEach(System.out::println);
        carService.removeDriverFromCar(driverBarak, carModelDodge);
        carService.getAllByDriver(driverAzamat.getId()).forEach(System.out::println);

        carService.delete(carModelBuick.getId());

    }
}
