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
    private static final Injector injector = Injector.getInstance("mate");

    public static void main(String[] args) {
        ManufacturerService manufacturerService =
                (ManufacturerService) injector.getInstance(ManufacturerService.class);
        Manufacturer bmv = new Manufacturer("BMW", "Germany");
        Manufacturer toyota = new Manufacturer("Toyota", "France");
        Manufacturer hyundai = new Manufacturer("Hyundai", "USA");
        Manufacturer mercedes = new Manufacturer("Mercedes", "USA");

        Manufacturer savedBmv = manufacturerService.create(bmv);
        Manufacturer savedToyota = manufacturerService.create(toyota);
        Manufacturer savedHyundai = manufacturerService.create(hyundai);
        final Manufacturer savedMercedes = manufacturerService.create(mercedes);

        savedToyota.setCountry("USA");
        final Manufacturer updatedToyota = manufacturerService.update(savedToyota);
        Manufacturer bmvFromDB = manufacturerService.get(savedBmv.getId());
        manufacturerService.delete(savedHyundai.getId());
        System.out.println(manufacturerService.getAll());

        DriverService driverService =
                (DriverService) injector.getInstance(DriverService.class);
        Driver john = new Driver("John", "123456789");
        Driver maks = new Driver("Maks", "234567890");
        Driver bob = new Driver("Bob", "345678901");
        Driver mark = new Driver("Mark", "345678901");

        Driver savedJohn = driverService.create(john);
        Driver savedMaks = driverService.create(maks);
        Driver savedBob = driverService.create(bob);
        final Driver savedMark = driverService.create(mark);

        savedJohn.setLicenseNumber("112345678");
        Driver updatedJohn = driverService.update(savedJohn);
        Driver maksFromDB = driverService.get(savedMaks.getId());
        driverService.delete(savedBob.getId());
        System.out.println(driverService.getAll());

        CarService carService =
                (CarService) injector.getInstance(CarService.class);

        List<Driver> driversForBmv = new ArrayList<>(List.of(updatedJohn, savedMaks));
        List<Driver> driversForToyota = new ArrayList<>(List.of(savedMaks, savedMark));
        List<Driver> driversForMercedes = new ArrayList<>(List.of(savedMark));
        Car bmvX5 = new Car("X5", savedBmv, driversForBmv);
        Car toyotaCamry = new Car("Camry", updatedToyota, driversForToyota);
        Car mercedesBenz = new Car("Benz", savedMercedes, driversForMercedes);

        Car savedBmvX5 = carService.create(bmvX5);
        Car savedToyotaCamry = carService.create(toyotaCamry);
        Car savedMercedesBenz = carService.create(mercedesBenz);

        savedMercedesBenz.setModel("Benz-GLS");
        Car updatedMercedesBenz = carService.update(savedMercedesBenz);
        Car bmvX5FromDB = carService.get(savedBmvX5.getId());
        carService.delete(savedToyotaCamry.getId());
        carService.removeDriverFromCar(updatedJohn, bmvX5);
        carService.addDriverToCar(updatedJohn, mercedesBenz);

        System.out.println(carService.getAllByDriver(updatedJohn.getId()));
        System.out.println(carService.getAll());
    }
}
