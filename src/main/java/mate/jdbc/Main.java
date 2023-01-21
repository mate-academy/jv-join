package mate.jdbc;

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
        //Drivers
        DriverService driverService = (DriverService) injector.getInstance(DriverService.class);
        Driver john = new Driver();
        john.setName("John");
        john.setLicenseNumber("AB1234");
        john = driverService.create(john);
        Driver bob = new Driver();
        bob.setName("Bob");
        bob.setLicenseNumber("XYZ1234");
        bob = driverService.create(bob);
        Driver jack = new Driver();
        jack.setName("Jack");
        jack.setLicenseNumber("MNO1234");
        jack = driverService.create(jack);
        //Manufacturers
        ManufacturerService manufacturerService
                = (ManufacturerService) injector.getInstance(ManufacturerService.class);
        Manufacturer bmw = new Manufacturer();
        bmw.setName("BMW");
        bmw.setCountry("Germany");
        bmw = manufacturerService.create(bmw);
        Manufacturer opel = new Manufacturer();
        opel.setName("Opel");
        opel.setCountry("Germany");
        opel = manufacturerService.create(opel);
        //Car
        CarService carService = (CarService) injector.getInstance(CarService.class);
        Car car = new Car();
        car.setModel("I3");
        List<Driver> drivers = List.of(john, bob, jack);
        car.setDrivers(drivers);
        car.setManufacturer(bmw);
        //Operations with car
        Car carWithID = carService.create(car);
        carWithID.setManufacturer(opel);
        Car opelCar = carService.update(carWithID);
        carWithID = carService.get(carWithID.getId());
        if (!opelCar.equals(carWithID)) {
            System.out.println("Cars are not equals: " + opelCar + carWithID);
        }
        carService.removeDriverFromCar(john, carWithID);
        Car carWithoutJohn = carService.get(carWithID.getId());
        System.out.println(carWithoutJohn);
        carService.addDriverToCar(john, carWithID);
        Car carWithJohn = carService.get(carWithID.getId());
        System.out.println(carWithJohn);
        if (!carService.delete(carWithID.getId())) {
            throw new RuntimeException("Can't delete car by id " + carWithID.getId());
        }
    }
}
