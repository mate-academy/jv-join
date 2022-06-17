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
        ManufacturerService mnfService = (ManufacturerService)
                injector.getInstance(ManufacturerService.class);
        Manufacturer manufacturerOne = new Manufacturer("Nissan", "Japan");
        Manufacturer manufacturerTwo = new Manufacturer("Toyota", "Japan");
        manufacturerOne = mnfService.create(manufacturerOne);
        manufacturerTwo = mnfService.create(manufacturerTwo);

        Driver driverFirst = new Driver("Driver First", "License #1111111");
        Driver driverSecond = new Driver("Driver Second", "License #2222222");
        Driver driverThird = new Driver("Driver Third", "License #3333333");

        DriverService drvService = (DriverService)
                injector.getInstance(DriverService.class);
        driverFirst = drvService.create(driverFirst);
        driverSecond = drvService.create(driverSecond);
        driverThird = drvService.create(driverThird);

        List<Driver> carOneDrivers = new ArrayList<>();
        carOneDrivers.add(driverFirst);
        carOneDrivers.add(driverSecond);

        List<Driver> carTwoDrivers = new ArrayList<>();
        carTwoDrivers.add(driverSecond);
        carTwoDrivers.add(driverThird);

        Car carOne = new Car();
        carOne.setManufacturer(manufacturerOne);
        carOne.setModel("80");
        carOne.setDrivers(carOneDrivers);

        Car carTwo = new Car();
        carTwo.setManufacturer(manufacturerTwo);
        carTwo.setModel("1844");
        carTwo.setDrivers(carTwoDrivers);

        CarService carService = (CarService) injector.getInstance(CarService.class);

        carOne = carService.create(carOne);
        System.out.println("carOne just created: " + carService.get(carOne.getId()));

        carOne.setManufacturer(manufacturerTwo);
        carOne.setDrivers(carTwoDrivers);
        carService.update(carOne);
        System.out.println("carOne updated: " + carService.get(carOne.getId()));

        Driver driverFourth = drvService.create(new Driver("Driver Fourth", "License #4444444"));
        carService.addDriverToCar(driverFourth, carOne);
        System.out.println("carOne, new driver added: " + carService.get(carOne.getId()));

        carService.removeDriverFromCar(driverSecond, carOne);
        System.out.println("carOne, driver removed: " + carService.get(carOne.getId()));

        System.out.println("All cars: " + carService.getAll());
        System.out.println("Car by driver id: " + carService.getAllByDriver(driverThird.getId()));

        carService.delete(carOne.getId());
    }
}
