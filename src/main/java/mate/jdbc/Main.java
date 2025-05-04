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
        final CarService carService = (CarService) injector
                .getInstance(CarService.class);

        Driver driverSasha = new Driver();
        driverSasha.setName("Sasha Hamlet");
        driverSasha.setLicenseNumber("AH6768AE");
        driverService.create(driverSasha);

        Driver driverRoma = new Driver();
        driverRoma.setName("Roma Valet");
        driverRoma.setLicenseNumber("AH8909AE");
        driverService.create(driverRoma);

        Driver driverValentina = new Driver();
        driverValentina.setName("Valentina Raf");
        driverValentina.setLicenseNumber("AH8908AE");
        driverService.create(driverValentina);

        Manufacturer manufacturerKrh = new Manufacturer();
        manufacturerKrh.setCountry("Germany");
        manufacturerKrh.setName("KRH");
        manufacturerService.create(manufacturerKrh);

        Manufacturer manufacturerFastCorp = new Manufacturer();
        manufacturerFastCorp.setCountry("USA");
        manufacturerFastCorp.setName("FastCorp");
        manufacturerService.create(manufacturerFastCorp);

        Car carKrh = new Car();
        carKrh.setManufacturer(manufacturerKrh);
        carKrh.setModel("KRH-truck");
        List<Driver> krhTruckDrivers = new ArrayList<>();
        krhTruckDrivers.add(driverSasha);
        carKrh.setDrivers(krhTruckDrivers);
        carService.create(carKrh);
        carService.addDriverToCar(driverRoma, carKrh);

        Car carFastCorp = new Car();
        carFastCorp.setManufacturer(manufacturerFastCorp);
        carFastCorp.setModel("Fast Corp Car");
        List<Driver> fastCorpDrivers = new ArrayList<>();
        fastCorpDrivers.add(driverSasha);
        fastCorpDrivers.add(driverRoma);
        fastCorpDrivers.add(driverValentina);
        carFastCorp.setDrivers(fastCorpDrivers);
        carService.removeDriverFromCar(driverValentina, carFastCorp);
        carFastCorp.setModel("Faster Corp Car");
        carService.update(carFastCorp);
        System.out.println(carService.get(carFastCorp.getId()));
        System.out.println(carService.getAll());
        System.out.println(carService.getAllByDriver(driverSasha.getId()));
        System.out.println(carService.getAllByDriver(driverValentina.getId()));
    }
}
