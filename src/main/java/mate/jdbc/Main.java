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
        ManufacturerService manufacturerService =
                (ManufacturerService) injector.getInstance(ManufacturerService.class);
        Manufacturer manufacturerAudi = new Manufacturer("Audi", "Germany");
        Manufacturer manufacturerBmw = new Manufacturer("BMW", "Germany");
        manufacturerService.create(manufacturerAudi);
        manufacturerService.create(manufacturerBmw);

        DriverService driverService = (DriverService) injector.getInstance(DriverService.class);
        Driver driverBob = new Driver("Bob", "BXP123456");
        Driver driverAlice = new Driver("Alice", "BXP654321");
        Driver driverBill = new Driver("Bill", "BXP122211");
        Driver driverJohn = new Driver("John", "BXP000001");
        driverService.create(driverBob);
        driverService.create(driverAlice);
        driverService.create(driverBill);
        driverService.create(driverJohn);
        List<Driver> audiDrivers = new ArrayList<>();
        audiDrivers.add(driverBob);
        audiDrivers.add(driverAlice);
        List<Driver> bmwDrivers = new ArrayList<>();
        bmwDrivers.add(driverBill);

        final CarService carService = (CarService) injector.getInstance(CarService.class);
        Car carAudi = new Car();
        carAudi.setModel("Q7");
        carAudi.setManufacturer(manufacturerAudi);
        carAudi.setDrivers(audiDrivers);
        Car carBmw = new Car();
        carBmw.setModel("X5");
        carBmw.setManufacturer(manufacturerBmw);
        carBmw.setDrivers(bmwDrivers);

        carService.create(carAudi);
        carService.create(carBmw);
        carAudi.setModel("RS6");
        carService.update(carAudi);
        audiDrivers.add(driverBill);
        carAudi.setDrivers(audiDrivers);
        carService.update(carAudi);
        carService.delete(carBmw.getId());
        carService.removeDriverFromCar(driverAlice, carAudi);
        carService.addDriverToCar(driverJohn, carAudi);

        System.out.println("Bob's cars: " + carService.getAllByDriver(driverBob.getId()));
    }
}
