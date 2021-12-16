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

public class TaxiService {
    private static final Injector injector = Injector.getInstance("mate.jdbc");
    private static ManufacturerService manufacturerService;
    private static DriverService driverService;
    private static CarService carService;

    public static void main(String[] args) {
        manufacturerService = (ManufacturerService) injector.getInstance(ManufacturerService.class);
        driverService = (DriverService) injector.getInstance(DriverService.class);
        carService = (CarService) injector.getInstance(CarService.class);

        Manufacturer toyota = new Manufacturer();
        toyota.setName("Toyota");
        toyota.setCountry("Japan");
        Manufacturer bmw = new Manufacturer();
        bmw.setName("BMW");
        bmw.setCountry("Germany");
        Manufacturer ford = new Manufacturer();
        ford.setName("Ford");
        ford.setCountry("USA");

        manufacturerService.create(toyota);
        manufacturerService.create(bmw);
        manufacturerService.create(ford);

        Driver john = new Driver();
        john.setName("John");
        john.setLicenseNumber("777");
        Driver rick = new Driver();
        rick.setName("Rick");
        rick.setLicenseNumber("779");
        Driver nicole = new Driver();
        nicole.setName("Nicole");
        nicole.setLicenseNumber("888");

        driverService.create(john);
        driverService.create(rick);
        driverService.create(nicole);

        Manufacturer man = manufacturerService.create(toyota);
        man.setName("Tata");
        man.setCountry("India");
        manufacturerService.update(man);
        manufacturerService.delete(man.getId());
        System.out.println(manufacturerService.get(3L));

        rick.setLicenseNumber("555");
        driverService.update(rick);
        System.out.println(driverService.get(1L));

        List<Driver> drivers = new ArrayList<>();
        drivers.add(john);
        drivers.add(rick);
        List<Driver> driversFord = new ArrayList<>();
        driversFord.add(nicole);
        Car bentley = new Car();
        bentley.setModel("Gt");
        bentley.setManufacturer(bmw);
        bentley.setDrivers(drivers);
        carService.create(bentley);

        carService.getAllByDriver(john.getId());
        carService.removeDriverFromCar(rick, bentley);

        Car truck = new Car();
        truck.setModel("f150");
        truck.setManufacturer(ford);
        truck.setDrivers(driversFord);
        carService.addDriverToCar(rick, bentley);

        carService.create(truck);
        carService.addDriverToCar(nicole, truck);

        carService.get(1L);

        carService.getAll().forEach(System.out::println);
        manufacturerService.getAll().forEach(System.out::println);
        driverService.getAll().forEach(System.out::println);
    }
}
