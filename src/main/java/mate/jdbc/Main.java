package mate.jdbc;

import mate.jdbc.lib.Injector;
import mate.jdbc.model.Car;
import mate.jdbc.model.Driver;
import mate.jdbc.model.Manufacturer;
import mate.jdbc.service.CarService;
import mate.jdbc.service.DriverService;
import mate.jdbc.service.ManufacturerService;

public class Main {
    private static final Injector injector = Injector.getInstance("mate.jdbc");
    private static final ManufacturerService manufacturerService =
            (ManufacturerService) injector.getInstance(ManufacturerService.class);
    private static final CarService carService =
            (CarService) injector.getInstance(CarService.class);
    private static final DriverService driverService =
            (DriverService) injector.getInstance(DriverService.class);

    public static void main(String[] args) {
        Manufacturer manufacturerCitroen = new Manufacturer("Citroen", "France");
        Manufacturer manufacturerSuzuki = new Manufacturer("Suzuki", "Japan");
        manufacturerService.create(manufacturerCitroen);
        manufacturerService.create(manufacturerSuzuki);
        Car citroenC4 = new Car("C4", manufacturerCitroen);
        carService.create(citroenC4);
        carService.getAll().forEach(System.out::println);
        Driver driverSophia = new Driver("Sophia", "00012");
        driverService.create(driverSophia);
        System.out.println("Add driver to car and get all tests:");
        carService.addDriverToCar(driverService.get(10L), carService.get(8L));
        System.out.println(carService.getAllByDriver(driverService.get(10L).getId()));
        carService.getAll().forEach(System.out::println);
        System.out.println("Add one more driver to car:");
        Driver driverDenis = new Driver("Denis", "786778");
        carService.addDriverToCar(driverService.create(driverDenis), carService.get(8L));
        carService.getAll().forEach(System.out::println);
        System.out.println("Get all cars by driver:");
        System.out.println(carService.getAllByDriver(10L));
        System.out.println("Remove new driver from car:");
        carService.removeDriverFromCar(driverService.get(11L), carService.get(8L));
        carService.getAll().forEach(System.out::println);
        System.out.println("Update car:");
        Car updatedCar = new Car(8L, "New c4 Hybrid", manufacturerService.get(22L));
        carService.update(updatedCar);
        carService.getAll().forEach(System.out::println);
        System.out.println("Get all tests:");
        carService.create(new Car("Grand Vitara", manufacturerSuzuki));
        carService.addDriverToCar(driverService.get(11L), carService.get(9L));
        carService.addDriverToCar(driverService.get(10L), carService.get(9L));
        carService.addDriverToCar(driverService.get(10L), carService.get(8L));
        System.out.println(carService.getAllByDriver(10L));
        System.out.println("Delete car:");
        carService.delete(8L);
        carService.getAll().forEach(System.out::println);
    }
}
