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
        // test your code here
        DriverService driverService = (DriverService) injector.getInstance(DriverService.class);

        List<Driver> driverList2 = new ArrayList<>();
        List<Driver> driverList4 = new ArrayList<>();

        driverList2.add(driverService.get(3L));
        driverList4.add(driverService.get(2L));
        driverList4.add(driverService.get(4L));

        ManufacturerService manufacturerService = (ManufacturerService)
                injector.getInstance(ManufacturerService.class);

        Manufacturer ford = manufacturerService.get(3L);
        Car car2 = new Car("Mustang", ford);
        car2.setDrivers(driverList2);
        Manufacturer bmw = manufacturerService.get(1L);
        Car car4 = new Car("745i", bmw);
        car4.setDrivers(driverList4);

        //create new car without list of drivers
        System.out.println("create new car without list of drivers--------");
        Manufacturer volvo = manufacturerService.get(6L);
        Car car1 = new Car("Volvo 940", volvo);
        System.out.println("Was: " + car1);
        CarService carService = (CarService) injector.getInstance(CarService.class);
        car1 = carService.create(car1);
        System.out.println("Is: " + car1);

        //create new car with list of drivers
        System.out.println("create new car with list of drivers------------");
        System.out.println("Was: " + car2);
        car2 = carService.create(car2);
        System.out.println("Is: " + car2);

        //get car
        System.out.println("get car---------");
        System.out.println(carService.get(1L));

        //get all cars
        System.out.println("get all cars----------------");
        carService.getAll().forEach(System.out::println);

        //update car with null list of drivers
        System.out.println("update car with null list of drivers-------------");
        Manufacturer toyota = manufacturerService.get(2L);
        Car car3 = new Car("Prado", toyota);
        car3.setId(3L);
        System.out.println("Was: " + car3);
        car3 = carService.update(car3);
        System.out.println("Is: " + car3);

        //update car with not empty list of drivers
        System.out.println("update car with not empty list of drivers-------------");
        car4.setId(4L);
        System.out.println("Was: " + car4);
        car4 = carService.update(car4);
        System.out.println("Is: " + car4);

        //delete car
        System.out.println("delete car-------------------");
        System.out.println("Car table:");
        carService.getAll().forEach(System.out::println);
        carService.delete(6L);
        System.out.println("Car table:");
        carService.getAll().forEach(System.out::println);

        //addDriverToCar test
        System.out.println("add driver id=5 to car id=5-------------------");
        System.out.println("Car table before adding:");
        carService.getAllByDriver(5L).forEach(System.out::println);
        carService.addDriverToCar(driverService.get(5L), carService.get(5L));
        System.out.println("Car table after adding:");
        carService.getAllByDriver(5L).forEach(System.out::println);

        //removeDriverFromCar test
        System.out.println("remove driver id=5 from car id=5-------------------");
        System.out.println("Car table before removing:");
        carService.getAllByDriver(5L).forEach(System.out::println);
        carService.removeDriverFromCar(driverService.get(5L), carService.get(5L));
        System.out.println("Car table after removing:");
        carService.getAllByDriver(5L).forEach(System.out::println);

        //get all cars by driver
        for (long i = 1L; i < 4L; i++) {
            System.out.println("All cars for driver ID=" + i);
            carService.getAllByDriver(i).forEach(System.out::println);
        }
    }
}
