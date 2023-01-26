package mate.jdbc;

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
        ManufacturerService manufacturerService = (ManufacturerService)
                        injector.getInstance(ManufacturerService.class);
        DriverService driverService = (DriverService)
                injector.getInstance(DriverService.class);
        Manufacturer manufacturerVolkswagen = new Manufacturer(null, "Volkswagen", "Germany");
        manufacturerService.create(manufacturerVolkswagen);
        Driver driverAndrii = new Driver(null, "Andrii", "AB2304CA");
        Driver driverOleksandr = new Driver(null, "Oleksandr", "CB4230KI");
        driverService.create(driverAndrii);
        driverService.create(driverOleksandr);
        System.out.println(driverService.getAll());

        CarService carService = (CarService)
                injector.getInstance(CarService.class);
        Car passat = new Car();
        passat.setModel("passat B5");
        passat.setManufacturer(manufacturerVolkswagen);
        carService.addDriverToCar(driverAndrii, passat);
        carService.create(passat);
        carService.get(passat.getId());

        Car cc = new Car();
        cc.setModel("CC");
        cc.setManufacturer(manufacturerVolkswagen);
        carService.addDriverToCar(driverAndrii, cc);
        carService.create(cc);
        System.out.println(carService.getAll());

        carService.addDriverToCar(driverOleksandr, cc);
        carService.update(cc);
        carService.getAllByDriver(driverAndrii.getId());
        carService.delete(passat.getId());
        System.out.println(carService.getAll());

    }
}
