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
    private static final ManufacturerService manufacturerService = (ManufacturerService)
            injector.getInstance(ManufacturerService.class);
    private static final DriverService driverService = (DriverService)
            injector.getInstance(DriverService.class);
    private static final CarService carService = (CarService)
            injector.getInstance(CarService.class);

    public static void main(String[] args) {
        final Manufacturer manufacturerFord = new Manufacturer("Ford", "USA");
        final Manufacturer manufacturerBmw = new Manufacturer("BMW", "Germany");
        final Manufacturer manufacturerToyota = new Manufacturer("Toyota", "Japan");

        final Driver driverMaxim = new Driver("Maxim", "A1");
        final Driver driverJon = new Driver("Jon", "A2");
        final Driver driverBob = new Driver("Bob", "A3");
        final Driver driverKarl = new Driver("Karl", "A4");
        final Driver driverMark = new Driver("Mark", "B1");
        final Driver driverBill = new Driver("Bill", "B2");
        final Driver driverEva = new Driver("Eva", "B3");

        manufacturerService.create(manufacturerFord);
        manufacturerService.create(manufacturerBmw);
        manufacturerService.create(manufacturerToyota);

        driverService.create(driverMaxim);
        driverService.create(driverJon);
        driverService.create(driverBob);
        driverService.create(driverKarl);
        driverService.create(driverMark);
        driverService.create(driverBill);
        driverService.create(driverEva);

        Car toyotaCar = new Car("Corolla", manufacturerToyota, new ArrayList<>());
        carService.create(toyotaCar);
        carService.addDriverToCar(driverMaxim, toyotaCar);
        carService.addDriverToCar(driverJon, toyotaCar);
        carService.update(toyotaCar);

        Car fordCar = new Car("Focus", manufacturerFord, new ArrayList<>());
        carService.create(fordCar);
        carService.addDriverToCar(driverBob, fordCar);
        carService.addDriverToCar(driverKarl, fordCar);
        carService.update(fordCar);

        Car bmwCar = new Car("X5", manufacturerBmw, new ArrayList<>());
        carService.create(bmwCar);
        carService.addDriverToCar(driverMark, bmwCar);
        carService.addDriverToCar(driverBill, bmwCar);
        carService.addDriverToCar(driverEva, bmwCar);
        carService.update(bmwCar);

        carService.removeDriverFromCar(driverEva, bmwCar);

        System.out.println(carService.getAll());

        toyotaCar.setModel("Camry");
        System.out.println(carService.update(toyotaCar));
    }
}
