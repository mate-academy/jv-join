package mate.jdbc;

import java.util.ArrayList;
import java.util.List;
import mate.jdbc.lib.Injector;
import mate.jdbc.model.Car;
import mate.jdbc.model.Driver;
import mate.jdbc.service.CarService;
import mate.jdbc.service.DriverService;
import mate.jdbc.service.ManufacturerService;

public class Main {
    private static final Injector injector = Injector.getInstance("mate.jdbc");

    public static void main(String[] args) {
        DriverService driverService
                = (DriverService) injector.getInstance(DriverService.class);
        List<Driver> driverList = new ArrayList<>();
        driverList.add(driverService.get(6L));
        driverList.add(driverService.get(7L));

        ManufacturerService manufacturerService
                = (ManufacturerService) injector.getInstance(ManufacturerService.class);

        Car toyotaCar = new Car();
        toyotaCar.setModel("ToyotaRav4");
        toyotaCar.setManufacturer(manufacturerService.get(38L));
        toyotaCar.setDrivers(driverList);

        CarService carService
                = (CarService) injector.getInstance(CarService.class);
        System.out.println(carService.create(toyotaCar));
        //get
        System.out.println("Get");
        System.out.println(carService.get(toyotaCar.getId()));
        //update
        System.out.println("update");
        Car volvoCar = new Car();
        volvoCar.setId(3L);
        volvoCar.setModel("Volvo TT");
        volvoCar.setManufacturer(manufacturerService.get(44L));
        volvoCar.setDrivers(driverList);
        System.out.println(carService.update(volvoCar));
        //delete
        System.out.println("delete");
        System.out.println(carService.delete(volvoCar.getId()));
        //get all by driver
        System.out.println("get all cars");
        System.out.println(carService.getAllByDriver(3L));
        //add a driver
        System.out.println("add a driver");
        carService.addDriverToCar(driverService.get(6L), toyotaCar);
        //remove a driver
        System.out.println("remove a driver");
        carService.removeDriverFromCar(driverService.get(6L), toyotaCar);
        //get all
        System.out.println("get all");
        carService.getAll().forEach(System.out::println);
    }
}
