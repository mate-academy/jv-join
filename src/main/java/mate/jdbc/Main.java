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
        Manufacturer manufacturerBmw = new Manufacturer();
        manufacturerBmw.setName("BMW");
        manufacturerBmw.setCountry("Germany");
        Manufacturer manufacturerVolkswagen = new Manufacturer();
        manufacturerVolkswagen.setName("Volkswagen");
        manufacturerVolkswagen.setCountry("German");
        ManufacturerService manufacturerService = (ManufacturerService) injector.getInstance(
                ManufacturerService.class);
        manufacturerService.create(manufacturerBmw);
        manufacturerService.create(manufacturerVolkswagen);

        Driver driverAndre = new Driver();
        driverAndre.setName("Andre");
        driverAndre.setLicenseNumber("A12345");
        Driver driverSergey = new Driver();
        driverSergey.setName("Sergey");
        driverSergey.setLicenseNumber("S45678");
        Driver driverPetro = new Driver();
        driverPetro.setName("Petro");
        driverPetro.setLicenseNumber("P14785");
        DriverService driverService = (DriverService) injector.getInstance(DriverService.class);
        driverService.create(driverAndre);
        driverService.create(driverSergey);
        driverService.create(driverPetro);
        List<Driver> driversBmw = new ArrayList<>();
        driversBmw.add(driverAndre);
        driversBmw.add(driverSergey);
        List<Driver> driversVolkswagen = new ArrayList<>();
        driversVolkswagen.add(driverPetro);

        CarService carService = (CarService) injector.getInstance(CarService.class);
        Car carBmw = new Car("X7", manufacturerBmw, driversBmw);
        Car carVolkswagen = new Car("VW", manufacturerVolkswagen, driversVolkswagen);
        carService.create(carBmw);
        carService.create(carVolkswagen);
        System.out.println(carService.get(carVolkswagen.getId()));

        carService.getAll().forEach(System.out::println);

        carBmw.setModel("XM");
        carService.update(carBmw);

        driversVolkswagen.add(driverAndre);
        carVolkswagen.setDrivers(driversVolkswagen);
        carService.update(carVolkswagen);
        System.out.println(carService.get(carBmw.getId()));
        System.out.println(carService.get(carVolkswagen.getId()));

        carService.delete(carBmw.getId());

        carService.removeDriverFromCar(driverAndre, carBmw);

        carService.addDriverToCar(driverPetro, carBmw);

        carService.getAllByDriver(driverAndre.getId()).forEach(System.out::println);

    }
}
