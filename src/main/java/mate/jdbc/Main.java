package mate.jdbc;

import mate.jdbc.lib.Injector;
import mate.jdbc.model.Car;
import mate.jdbc.model.Driver;
import mate.jdbc.model.Manufacturer;
import mate.jdbc.service.CarService;
import mate.jdbc.service.DriverService;
import mate.jdbc.service.ManufacturerService;

public class Main {
    public static final Injector injector = Injector
            .getInstance("mate.jdbc");
    public static final CarService carService = (CarService) injector
            .getInstance(CarService.class);
    public static final ManufacturerService manufacturerService = (ManufacturerService) injector
            .getInstance(ManufacturerService.class);
    public static final DriverService driverService = (DriverService) injector
            .getInstance(DriverService.class);

    public static void main(String[] args) {
        Manufacturer dodge = new Manufacturer();
        dodge.setName("Dodge");
        dodge.setCountry("USA");

        Manufacturer astonmartin = new Manufacturer();
        astonmartin.setName("Aston Martin");
        astonmartin.setCountry("Great Britain");

        manufacturerService.create(dodge);
        manufacturerService.create(astonmartin);

        Driver vindiesel = new Driver();
        vindiesel.setName("Dominik");
        vindiesel.setLicenseNumber("09870987");

        Driver paulwalker = new Driver();
        paulwalker.setName("Bryan");
        paulwalker.setLicenseNumber("12341234");

        Driver mrshudson = new Driver();
        mrshudson.setName("Martha Louise");
        mrshudson.setLicenseNumber("07070707");

        Driver watson = new Driver();
        watson.setName("John");
        watson.setLicenseNumber("01020304");

        driverService.create(vindiesel);
        driverService.create(paulwalker);
        driverService.create(mrshudson);
        driverService.create(watson);

        Car charger = new Car();
        charger.setModel("Charger");
        charger.setManufacturer(dodge);

        Car db9 = new Car();
        db9.setModel("DB9");
        db9.setManufacturer(astonmartin);

        carService.create(charger);
        carService.create(db9);

        Car carById = carService.get(charger.getId());
        System.out.println(carById + System.lineSeparator());

        carService.addDriverToCar(vindiesel, charger);
        carService.addDriverToCar(watson, charger);
        carService.addDriverToCar(mrshudson, db9);

        carService.getAll().forEach(System.out::println);
        System.out.println(System.lineSeparator());

        carService.removeDriverFromCar(watson, charger);
        carService.addDriverToCar(paulwalker, charger);
        carService.addDriverToCar(watson, db9);
        carService.addDriverToCar(mrshudson, charger);

        carService.getAll().forEach(System.out::println);
        System.out.println(System.lineSeparator());
        carService.getAllByDriver(mrshudson.getId());
    }
}
