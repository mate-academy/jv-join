package mate.jdbc;

import mate.jdbc.lib.Injector;
import mate.jdbc.model.Car;
import mate.jdbc.model.Driver;
import mate.jdbc.model.Manufacturer;
import mate.jdbc.service.CarService;
import mate.jdbc.service.DriverService;
import mate.jdbc.service.ManufacturerService;

import java.util.ArrayList;
import java.util.List;

public class Main {
    private static final Injector injector = Injector.getInstance("mate.jdbc");
    public static void main(String[] args) {
        // test your code here
        ManufacturerService manufacturerService = (ManufacturerService)
                injector.getInstance(ManufacturerService.class);
        DriverService driverService = (DriverService)
                injector.getInstance(DriverService.class);
        CarService carService = (CarService)
                injector.getInstance(CarService.class);

        Manufacturer bmw = manufacturerService.get(1L);
        Manufacturer toyota = manufacturerService.get(2L);
        Manufacturer ford = manufacturerService.get(3L);
//        Manufacturer zaz = manufacturerService.get(4L);
//        Manufacturer kia = manufacturerService.get(5L);
        Manufacturer volvo = manufacturerService.get(6L);

//        List<Driver> driverList1 = new ArrayList<>();
        List<Driver> driverList2 = new ArrayList<>();
//        List<Driver> driverList3 = new ArrayList<>();
        List<Driver> driverList4 = new ArrayList<>();

//        driverList1.add(driverService.get(1L));
//        driverList1.add(driverService.get(2L));
        driverList2.add(driverService.get(3L));
//        driverList3.add(driverService.get(1L));
        driverList4.add(driverService.get(2L));
        driverList4.add(driverService.get(4L));

        Car car1 = new Car("Volvo 940", volvo);
        Car car2 = new Car("Mustang", ford);
        car2.setDrivers(driverList2);
        Car car3 = new Car("Prado", toyota);
        Car car4 = new Car("745i", bmw);
        car4.setDrivers(driverList4);

        //create new car without list of drivers
        System.out.println("create new car without list of drivers--------");
        System.out.println("Was: " + car1);
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
        car3.setId(3L);
        System.out.println("Was: " + car3);
        car3 = carService.update(car3);
        System.out.println("Is: " + car3);


        //update car with empty list of drivers

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

        //get all cars by driver
        for (long i = 1L; i < 4L; i++) {
            System.out.println("All cars for driver ID=" + i);
            carService.getAllByDriver(i).forEach(System.out::println);
        }
    }
}
