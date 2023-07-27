package mate.jdbc;

import mate.jdbc.lib.Injector;
import mate.jdbc.model.Car;
import mate.jdbc.service.CarService;
import mate.jdbc.service.DriverService;
import mate.jdbc.service.ManufacturerService;

public class Main {
    private static Injector injector = Injector.getInstance("mate.jdbc");

    public static void main(String[] args) {
        // test your code here
        CarService carService = (CarService) injector.getInstance(CarService.class);
        ManufacturerService manufacturerService = (ManufacturerService) injector
                .getInstance(ManufacturerService.class);
        DriverService driverService = (DriverService) injector.getInstance(DriverService.class);
        carService.getAll().forEach(System.out::println);
        Car createdCar = carService.create(new Car(null, "Prius",
                manufacturerService.get(1L), driverService.getAll()));
        System.out.println("Created car: " + createdCar);
        Car receivedCar = carService.get(1L);
        System.out.println("Received car: " + receivedCar);
        createdCar.setModel("Corolla");
        Car updatedCar = carService.update(createdCar);
        System.out.println(updatedCar);
        System.out.println("Cars that have driver with id 1: ");
        carService.getAllByDriver(1L).forEach(System.out::println);
        System.out.println("Car before adding driver with id 1: " + updatedCar);
        carService.addDriverToCar(driverService.get(1L), updatedCar);
        System.out.println("Car after adding driver with id 1: " + updatedCar);
        System.out.println("Car before removing driver with id 1: " + updatedCar);
        carService.removeDriverFromCar(driverService.get(1L), updatedCar);
        System.out.println("Car after removing driver with id 1: " + updatedCar);
        boolean idDeletedCar = carService.delete(receivedCar.getId());
        System.out.println("Is deleted car with id 1: " + idDeletedCar);
    }
}
