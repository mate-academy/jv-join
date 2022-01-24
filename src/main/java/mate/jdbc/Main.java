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

        Car carMercedes = new Car();
        carMercedes.setModel("E220");
        Car carFiat = new Car();
        carFiat.setModel("Doblo");
        Car carToyota = new Car();
        carToyota.setModel("Yaris");

        ManufacturerService manufacturerService =
                (ManufacturerService) injector.getInstance(ManufacturerService.class);
        Manufacturer mercedes = new Manufacturer("Mercedes", "Germany");
        Manufacturer fiat = new Manufacturer("Fiat", "Italy");
        Manufacturer toyota = new Manufacturer("Toyota", "Japan");
        manufacturerService.create(mercedes);
        manufacturerService.create(fiat);
        manufacturerService.create(toyota);
        carMercedes.setManufacturer(manufacturerService.get(1L));
        carFiat.setManufacturer(manufacturerService.get(2L));
        carToyota.setManufacturer(manufacturerService.get(3L));

        DriverService driverService = (DriverService) injector.getInstance(DriverService.class);
        Driver bob = new Driver("Bob","10001");
        Driver anna = new Driver("Anna", "10002");
        Driver den = new Driver("Den", "10003");
        Driver ben = new Driver("Ben", "10004");
        Driver sofia = new Driver("Sofia", "10005");
        Driver bill = new Driver("Bill", "10006");
        driverService.create(bob);
        driverService.create(anna);
        driverService.create(den);
        driverService.create(ben);
        driverService.create(sofia);
        driverService.create(bill);

        List<Driver> driversMercedes = new ArrayList<>();
        driversMercedes.add(driverService.get(1L));
        driversMercedes.add(driverService.get(2L));
        carMercedes.setDrivers(driversMercedes);
        List<Driver> driversFiat = new ArrayList<>();
        driversFiat.add(driverService.get(3L));
        driversFiat.add(driverService.get(4L));
        carFiat.setDrivers(driversFiat);
        List<Driver> driversToyota = new ArrayList<>();
        driversToyota.add(driverService.get(5L));
        driversToyota.add(driverService.get(6L));
        carToyota.setDrivers(driversToyota);

        CarService carService = (CarService) injector.getInstance(CarService.class);

        carService.create(carMercedes);
        carService.create(carFiat);
        carService.create(carToyota);
        System.out.println(carService.get(10L));
        System.out.println(carService.getAll());
        carToyota.setModel("Corola");
        carToyota.setId(10L);
        carService.update(carToyota);
        carService.delete(9L);
        carService.addDriverToCar(driverService.get(2L),carToyota);
        carService.removeDriverFromCar(driverService.get(5L), carToyota);
        System.out.println(carService.get(10L));
        System.out.println(carService.getAll());
        // test your code here
    }
}
