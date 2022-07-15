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

        List<Driver> listOfOneDriver = new ArrayList<>();
        List<Driver> listOfTwoDrivers = new ArrayList<>();

        listOfOneDriver.add(driverService.get(3L));
        listOfTwoDrivers.add(driverService.get(2L));
        listOfTwoDrivers.add(driverService.get(4L));

        ManufacturerService manufacturerService = (ManufacturerService)
                injector.getInstance(ManufacturerService.class);

        Manufacturer ford = manufacturerService.get(3L);
        Car carMustang = new Car("Mustang", ford);
        carMustang.setDrivers(listOfOneDriver);
        Manufacturer bmw = manufacturerService.get(1L);
        Car carBmw745 = new Car("745i", bmw);
        carBmw745.setDrivers(listOfTwoDrivers);

        //create new car without list of drivers
        System.out.println("create new car without list of drivers--------");
        Manufacturer volvo = manufacturerService.get(6L);
        Car carVolvo940 = new Car("Volvo 940", volvo);
        System.out.println("Was: " + carVolvo940);
        CarService carService = (CarService) injector.getInstance(CarService.class);
        carVolvo940 = carService.create(carVolvo940);
        System.out.println("Is: " + carVolvo940);

        //create new car with list of drivers
        System.out.println("create new car with list of drivers------------");
        System.out.println("Was: " + carMustang);
        carMustang = carService.create(carMustang);
        System.out.println("Is: " + carMustang);

        //get car
        System.out.println("get car---------");
        System.out.println(carService.get(1L));

        //get all cars
        System.out.println("get all cars----------------");
        carService.getAll().forEach(System.out::println);

        //update car with null list of drivers
        System.out.println("update car with null list of drivers-------------");
        Manufacturer toyota = manufacturerService.get(2L);
        Car carPrado = new Car("Prado", toyota);
        carPrado.setId(3L);
        System.out.println("Was: " + carPrado);
        carPrado = carService.update(carPrado);
        System.out.println("Is: " + carPrado);

        //update car with not empty list of drivers
        System.out.println("update car with not empty list of drivers-------------");
        carBmw745.setId(4L);
        System.out.println("Was: " + carBmw745);
        carBmw745 = carService.update(carBmw745);
        System.out.println("Is: " + carBmw745);

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
