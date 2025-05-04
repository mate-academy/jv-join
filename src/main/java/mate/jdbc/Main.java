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
    private static final ManufacturerService manufacturerService =
            (ManufacturerService) injector.getInstance(ManufacturerService.class);
    private static final CarService carService =
            (CarService) injector.getInstance(CarService.class);
    private static final DriverService driverService =
            (DriverService) injector.getInstance(DriverService.class);

    public static void main(String[] args) {
        System.out.println("Create 4 manufacturers: Audi, BMW, Volkswagen, Mercedes.");
        Manufacturer audi = new Manufacturer("Audi", "Germany");
        manufacturerService.create(audi);
        Manufacturer bmw = new Manufacturer("BMW", "Germany");
        manufacturerService.create(bmw);
        Manufacturer volkswagen = new Manufacturer("Volkswagen", "Germany");
        manufacturerService.create(volkswagen);
        Manufacturer mercedes = new Manufacturer("Mercedes", "Germany");
        manufacturerService.create(mercedes);

        System.out.println(System.lineSeparator() + "Create 4 cars: Audi Q5, BMW X5, VW Tiguan, "
                + "Mercedes GLS.");
        Car audiQ5 = new Car("Q5", audi);
        carService.create(audiQ5);
        Car bmwX5 = new Car("X5", bmw);
        carService.create(bmwX5);
        Car vwTiguan = new Car("Tiguan", volkswagen);
        carService.create(vwTiguan);
        Car mercedesGls = new Car("GLS", mercedes);
        carService.create(mercedesGls);

        System.out.println(System.lineSeparator() + "Create 4 drivers: Steve, Bill,Kate, "
                + "Monica.");
        Driver steve = new Driver("Steve", "AAA-001");
        driverService.create(steve);
        Driver bill = new Driver("Bill", "AAA-002");
        driverService.create(bill);
        Driver kate = new Driver("Kate", "AAA-003");
        driverService.create(kate);
        Driver monica = new Driver("Monica", "AAA-004");
        driverService.create(monica);

        System.out.println(System.lineSeparator() + "Add list of 4 drivers: Steve, Bill,Kate, "
                + "Monica to Mercedes GLS.");
        mercedesGls.setDrivers(new ArrayList<>(List.of(steve, bill, kate, monica)));
        carService.update(mercedesGls);
        System.out.println(carService.get(mercedesGls.getId()));
        System.out.println(mercedesGls.getDrivers());
        System.out.println(carService.getAll());

        System.out.println(System.lineSeparator() + "Remove BMW X5 from list of cars.");
        carService.delete(bmwX5.getId());
        System.out.println(carService.getAll());

        System.out.println(System.lineSeparator() + "Add list of 2 drivers: Steve, Monica to Audi "
                + "Q5.");
        audiQ5.setDrivers(new ArrayList<>(List.of(steve, monica)));
        carService.update(audiQ5);
        System.out.println(carService.getAll());

        System.out.println(System.lineSeparator() + "Add driver Bill to Audi Q5.");
        carService.addDriverToCar(bill, audiQ5);
        System.out.println(audiQ5.getDrivers());

        System.out.println(System.lineSeparator() + "Remover driver Bill from Mercedes GLS.");
        carService.removeDriverFromCar(bill, mercedesGls);
        System.out.println(mercedesGls.getDrivers());

        System.out.println(System.lineSeparator() + "Get all cars by drivers: Steve, Bill, Monica");
        System.out.println(carService.getAllByDriver(steve.getId()));
        System.out.println(carService.getAllByDriver(bill.getId()));
        System.out.println(carService.getAllByDriver(monica.getId()));

        System.out.println(System.lineSeparator() + "Add driver Kate to VW Tiguan with undefined "
                + "list of drivers.");
        carService.addDriverToCar(kate, vwTiguan);
        System.out.println(vwTiguan);
    }
}
