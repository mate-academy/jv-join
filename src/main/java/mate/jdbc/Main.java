package mate.jdbc;

import java.util.ArrayList;
import mate.jdbc.lib.Injector;
import mate.jdbc.model.Car;
import mate.jdbc.model.Driver;
import mate.jdbc.service.CarService;
import mate.jdbc.service.DriverService;
import mate.jdbc.service.ManufacturerService;

public class Main {
    private static final Injector injector = Injector.getInstance("mate.jdbc");

    public static void main(String[] args) {
        ManufacturerService manufacturerService
                = (ManufacturerService) injector.getInstance(ManufacturerService.class);
        DriverService driverService = (DriverService) injector.getInstance(DriverService.class);
        //create
        Driver bill = driverService.get(6L);
        Driver maria = driverService.get(4L);
        Car car = new Car();
        car.setManufacturer(manufacturerService.get(2L));
        car.setModel("AMG GLS 350 d");
        car.setDrivers(new ArrayList<>() {{
                add(bill);
                add(maria);
                }
        });
        CarService carService = (CarService) injector.getInstance(CarService.class);
        car = carService.create(car);
        System.out.println(car);
        //read
        System.out.println(carService.get(5L));
        carService.getAll().forEach(System.out::println);
        //update
        Driver max = driverService.get(5L);
        car.setModel("VSC HWK 33");
        car.getDrivers().add(max);
        carService.update(car);
        System.out.println(carService.get(5L));
        //delete
        carService.delete(5L);
        System.out.println(carService.get(5L));
        //add driver to car
        Car x3 = carService.get(1L);
        System.out.println(x3);
        carService.addDriverToCar(bill, x3);
        System.out.println(x3);
        System.out.println(carService.get(1L));
        //remove driver from car
        carService.removeDriverFromCar(max, x3);
        System.out.println(x3);
        System.out.println(carService.get(1L));
        //get all cars by driver id
        carService.getAllByDriver(4L).forEach(System.out::println);
    }
}
