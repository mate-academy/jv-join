package mate.jdbc;

import java.util.ArrayList;
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

        Manufacturer maserati = new Manufacturer();
        maserati.setName("Maserati");
        maserati.setCountry("Italy");
        manufacturerService.create(maserati);

        Car maseratiLevante = new Car();
        maseratiLevante.setDrivers(new ArrayList<>());
        maseratiLevante.setManufacturer(maserati);
        maseratiLevante.setModel("Maserati Levante");

        Car maseratiGhibli = new Car();
        maseratiGhibli.setDrivers(new ArrayList<>());
        maseratiGhibli.setModel("Maserati Ghibli");
        maseratiGhibli.setManufacturer(maserati);

        Driver antonio = new Driver();
        antonio.setName("Antonio");
        antonio.setLicenseNumber("124124124");

        Driver maria = new Driver();
        maria.setName("Maria");
        maria.setLicenseNumber("98978568");

        DriverService driverService = (DriverService) injector.getInstance(DriverService.class);
        driverService.create(maria);
        driverService.create(antonio);

        CarService carService = (CarService) injector.getInstance(CarService.class);
        carService.create(maseratiLevante);
        carService.create(maseratiGhibli);

        carService.addDriverToCar(maria, maseratiGhibli);
        carService.addDriverToCar(antonio, maseratiGhibli);
        carService.addDriverToCar(antonio, maseratiLevante);

        carService.getAllByDriver(antonio.getId()).forEach(System.out::println);
        carService.getAllByDriver(maria.getId()).forEach(System.out::println);

        carService.removeDriverFromCar(antonio, maseratiLevante);
        carService.getAllByDriver(antonio.getId()).forEach(System.out::println);
    }
}
