package mate.jdbc;

import java.util.List;
import mate.jdbc.lib.Injector;
import mate.jdbc.model.Car;
import mate.jdbc.model.Driver;
import mate.jdbc.model.Manufacturer;
import mate.jdbc.service.CarService;
import mate.jdbc.service.ManufacturerService;

public class Main {
    private static final Injector injector = Injector.getInstance("mate.jdbc");

    public static void main(String[] args) {
        //create drivers
        Driver driverLuka = new Driver(1L, "Luca", "999");
        Driver driverValera = new Driver(2L, "Valera", "none");

        //create manufacture
        ManufacturerService manufacturerService =
                (ManufacturerService) injector.getInstance(ManufacturerService.class);
        Manufacturer manufacturerPorsche =
                manufacturerService.create(
                        new Manufacturer(1L, "Porsche", "Germany"));

        //create cars
        Car firstCar = new Car(1L, "911", manufacturerPorsche);
        Car secondCar = new Car(2L, "Macan", manufacturerPorsche);

        firstCar.setDrivers(List.of(driverLuka, driverValera));
        secondCar.setDrivers(List.of(driverLuka, driverValera));

        //update car
        CarService carService = (CarService) injector.getInstance(CarService.class);
        System.out.println("UPDATE IS WORKING");
        firstCar.setId(2L);
        System.out.println(carService.update(firstCar));
        System.out.println("--------------------------------------------");

        carService.create(firstCar);
        carService.create(secondCar);

        //get cars by id
        System.out.println(carService.get(firstCar.getId()));
        System.out.println(carService.get(secondCar.getId()));
        System.out.println("--------------------------------------------");

        //get all cars
        System.out.println(carService.getAll());
        System.out.println("--------------------------------------------");

        //getAllByDriver
        System.out.println(carService.getAllByDriver(driverLuka.getId()));
        System.out.println("--------------------------------------------");

        //delete car
        System.out.println(carService.delete(firstCar.getId()));
        System.out.println("--------------------------------------------");
    }
}
