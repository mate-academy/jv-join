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
    private static final CarService carService =
            (CarService) injector.getInstance(CarService.class);
    private static final ManufacturerService manufacturerService = (ManufacturerService)
            injector.getInstance(ManufacturerService.class);
    private static final DriverService driverService =
            (DriverService) injector.getInstance(DriverService.class);

    public static void main(String[] args) {
        Driver driverBob = new Driver("Bob", "1234");
        Driver driverAlice = new Driver("Alice", "4567");
        Driver driverBill = new Driver("Bill", "7890");
        driverService.create(driverAlice);
        driverService.create(driverBill);
        driverService.create(driverBob);
        System.out.println(driverService.get(driverAlice.getId()));
        System.out.println("-------------------------------------------------");
        System.out.println(driverService.getAll());
        System.out.println("-------------------------------------------------");
        driverService.delete(driverAlice.getId());
        System.out.println(driverService.getAll());
        System.out.println("-------------------------------------------------");
        driverBob.setLicenseNumber("0001");
        System.out.println(driverService.update(driverBob));
        System.out.println("-------------------------------------------------");
        System.out.println("-------------------------------------------------");

        Manufacturer manufacturerAudi = new Manufacturer("AUDI", "Germany");
        Manufacturer manufacturerRenault = new Manufacturer("Renault", "France");
        Manufacturer manufacturerToyota = new Manufacturer("Toyota", "Japan");
        manufacturerService.create(manufacturerAudi);
        manufacturerService.create(manufacturerToyota);
        manufacturerService.create(manufacturerRenault);
        System.out.println(manufacturerService.get(manufacturerAudi.getId()));
        System.out.println("-------------------------------------------------");
        System.out.println(manufacturerService.getAll());
        System.out.println("-------------------------------------------------");
        manufacturerService.delete(manufacturerAudi.getId());
        System.out.println(manufacturerService.getAll());
        System.out.println("-------------------------------------------------");
        manufacturerRenault.setCountry("Ukraine");
        System.out.println(manufacturerService.update(manufacturerRenault));

        //test create method
        List<Driver> driverList = new ArrayList<>();
        driverList.add(driverAlice);
        driverList.add(driverBill);
        driverList.add(driverBob);
        Car carAudi = new Car("Q7", manufacturerAudi, driverList);
        Car carToyota = new Car("A12", manufacturerToyota, driverList);
        Car carRenault = new Car("E51", manufacturerRenault, driverList);
        carService.create(carAudi);
        carService.create(carToyota);
        carService.create(carRenault);
        //test get method
        System.out.println(carService.get(carAudi.getId()));
        System.out.println("-------------------------------------------------");
        //test getAll method
        System.out.println(carService.getAll());
        System.out.println("-------------------------------------------------");
        //test update method
        System.out.println("Model before update " + carAudi.getModel());
        carAudi.setModel("1234");
        carService.update(carAudi);
        System.out.println("Model after update " + carAudi.getModel());
        System.out.println("-------------------------------------------------");
        //test delete method
        carService.delete(carRenault.getId());
        System.out.println(carService.getAll());
        System.out.println("-------------------------------------------------");
        //test method add driver to car
        carService.addDriverToCar(driverBob, carToyota);
        System.out.println(carService.get(carToyota.getId()));
        System.out.println("-------------------------------------------------");
        //test method remove driver from car
        carService.removeDriverFromCar(driverBob, carToyota);
        System.out.println(carService.get(carToyota.getId()));
        System.out.println("-------------------------------------------------");
        //test getAllByDriver
        System.out.println(carService.getAllByDriver(driverBill.getId()));
    }
}
