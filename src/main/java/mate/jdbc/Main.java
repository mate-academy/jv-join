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
        Manufacturer manufacturer1 = new Manufacturer("Nissan", "Japan");
        Manufacturer manufacturer2 = new Manufacturer("Toyota", "Japan");
        manufacturer1 = mnfService.create(manufacturer1);
        manufacturer2 = mnfService.create(manufacturer2);

        Driver driverFirst = new Driver("Driver First", "License #1");
        Driver driverSecond = new Driver("Driver Second", "License #2");
        Driver driverThird = new Driver("Driver Third", "License #3");

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
        carOne.setManufacturer(manufacturer1);
        carOne.setModel("80");
        carOne.setDrivers(carOneDrivers);

        Car carTwo = new Car();
        carTwo.setManufacturer(manufacturer2);
        carTwo.setModel("1844");
        carTwo.setDrivers(carTwoDrivers);

        CarService carService = (CarService) injector.getInstance(CarService.class);

        carOne = carService.create(carOne);
        System.out.println(carService.get(carOne.getId()));

        carOne.setManufacturer(manufacturer2);
        carOne.setDrivers(carTwoDrivers);
        carService.update(carOne);
        System.out.println(carService.get(carOne.getId()));

        Driver driverFourth = drvService.create(new Driver("Driver Fourth", "License #4"));
        carService.addDriverToCar(driverFourth, carOne);
        System.out.println(carService.get(carOne.getId()));

        carService.removeDriverFromCar(driverThird, carOne);
        System.out.println(carService.get(carOne.getId()));

        carService.delete(carOne.getId());

        System.out.println(carService.getAllByDriver(driverFirst.getId()));
    }
}
