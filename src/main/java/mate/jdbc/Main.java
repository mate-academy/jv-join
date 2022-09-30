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
        final DriverService driverService = (DriverService) injector
                .getInstance(DriverService.class);
        final ManufacturerService manufacturerService = (ManufacturerService) injector
                .getInstance(ManufacturerService.class);
        final CarService carService = (CarService) injector.getInstance(CarService.class);

        Driver driverJohnny = new Driver();
        driverJohnny.setName("Johnny Depp");
        driverJohnny.setLicenseNumber("154686");
        driverService.create(driverJohnny);

        Driver driverTom = new Driver();
        driverTom.setName("Tom Cruise");
        driverTom.setLicenseNumber("898745");
        driverService.create(driverTom);

        Driver driverAngelina = new Driver();
        driverAngelina.setName("Angelina Jolie");
        driverAngelina.setLicenseNumber("423391");
        driverService.create(driverAngelina);

        Manufacturer manufacturerPagani = new Manufacturer();
        manufacturerPagani.setCountry("Italy");
        manufacturerPagani.setName("Pagani");
        manufacturerService.create(manufacturerPagani);

        Manufacturer manufacturerRolls = new Manufacturer();
        manufacturerRolls.setCountry("United Kingdom");
        manufacturerRolls.setName("Rolls-Royce");
        manufacturerService.create(manufacturerRolls);

        Car carHuayra = new Car();
        carHuayra.setManufacturer(manufacturerPagani);
        carHuayra.setModel("Huayra BC Roadster");
        List<Driver> huayraRoadsterDrivers = new ArrayList<>();
        huayraRoadsterDrivers.add(driverAngelina);
        carHuayra.setDrivers(huayraRoadsterDrivers);
        carService.create(carHuayra);
        carService.addDriverToCar(driverJohnny, carHuayra);

        Car carGhost = new Car();
        carGhost.setManufacturer(manufacturerRolls);
        carGhost.setModel("Rolls-Royce Ghost");
        List<Driver> ghostDrivers = new ArrayList<>();
        ghostDrivers.add(driverTom);
        ghostDrivers.add(driverJohnny);
        ghostDrivers.add(driverAngelina);
        carGhost.setDrivers(ghostDrivers);
        carService.create(carGhost);
        carService.removeDriverFromCar(driverAngelina, carGhost);

        System.out.println(carService.getAll());
        System.out.println(carService.getAllByDriver(driverJohnny.getId()));
        System.out.println(carService.getAllByDriver(driverAngelina.getId()));

        carHuayra.setModel("Huayra R");
        carService.update(carHuayra);
        System.out.println(carService.get(carHuayra.getId()));
    }
}
