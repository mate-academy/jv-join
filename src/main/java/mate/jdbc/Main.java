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

        DriverService driverService = (DriverService)
                injector.getInstance(DriverService.class);
        Driver driverOne = new Driver("Shinyo Nakano", "NO467386");
        driverService.create(driverOne);
        Driver driverTwo = new Driver("Tetsuya Harada", "SK867442");
        Driver driverThree = new Driver("Valentino Rossi", "KV573988");
        driverService.create(driverOne);
        driverService.create(driverTwo);
        driverService.create(driverThree);
        List<Driver> drivers = new ArrayList<>();
        drivers.add(driverTwo);
        drivers.add(driverOne);

        ManufacturerService manufacturerService = (ManufacturerService)
                injector.getInstance(ManufacturerService.class);
        Manufacturer manufacturerOne = new Manufacturer("DeLorean", "USA");
        Manufacturer manufacturerTwo = new Manufacturer("Nissan", "Japan");
        Manufacturer manufacturerThree = new Manufacturer("Mercedes-Benz", "Germany");
        manufacturerService.create(manufacturerOne);
        manufacturerService.create(manufacturerTwo);
        manufacturerService.create(manufacturerThree);

        Car carOne = new Car();
        carOne.setModel("DeLorean DMC-12, 1981");
        carOne.setManufacturer(manufacturerOne);
        carOne.setDrivers(drivers);
        Car carTwo = new Car();
        carTwo.setModel("Nissan Leopard, 1981");
        carTwo.setManufacturer(manufacturerTwo);
        carTwo.setDrivers(drivers);
        Car carThree = new Car();
        carThree.setModel("Mercedes-Benz 126, 1987");
        carThree.setManufacturer(manufacturerThree);
        carThree.setDrivers(drivers);
        CarService carService = (CarService) injector.getInstance(CarService.class);
        carService.create(carOne);
        carService.create(carTwo);
        carService.create(carThree);
        System.out.println(carService.get(carOne.getId()));
        carService.addDriverToCar(driverOne, carOne);
        System.out.println(carService.getAll());
        carService.removeDriverFromCar(driverThree,carOne);
        System.out.println(carService.get(carOne.getId()));
        System.out.println(carService.getAllByDriver(carOne.getId()));
    }
}
