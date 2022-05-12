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
    public static void main(String[] args) {
        Injector injector = Injector.getInstance("mate.jdbc");

        DriverService driverService = (DriverService) injector.getInstance(DriverService.class);
        List<Driver> drivers = new ArrayList<>();
        drivers.add(driverService.create(new Driver("Alice", "123456")));
        drivers.add(driverService.create(new Driver("Bob", "123457")));
        driverService.create(new Driver("John", "123458"));

        ManufacturerService manufacturerService =
                (ManufacturerService) injector.getInstance(ManufacturerService.class);
        Manufacturer daewoo = manufacturerService.create(new Manufacturer("Daewoo", "Ukraine"));
        Manufacturer toyota = manufacturerService.create(new Manufacturer("Toyota", "Japan"));

        CarService carService = (CarService) injector.getInstance(CarService.class);
        carService.create(new Car("Lanos", daewoo, drivers));
        carService.create(new Car("Sens", daewoo, drivers));
        carService.create(new Car("Nubira", daewoo, drivers));
        carService.create(new Car("Corolla", toyota, drivers));
        carService.create(new Car("Yaris", toyota, drivers));
        carService.create(new Car("Camry", toyota, drivers));

        System.out.println("Testing get(id) method\n" + carService.get(1L));
        System.out.println("Testing getAll() method\n" + carService.getAll());

        Car carToUpdate = carService.get(6L);
        carToUpdate.setModel("Prius");
        System.out.println("Testing update(car) method\n" + carService.update(carToUpdate));

        carService.delete(1L);
        System.out.println("Testing delete(id) method\n" + carService.getAll());

        System.out.println("Before adding driver\n" + carService.getAll());
        carService.addDriverToCar(driverService.get(3L), carService.get(4L));
        System.out.println("After adding driver\n" + carService.getAll());
        carService.removeDriverFromCar(driverService.get(3L), carService.get(4L));
        System.out.println("After removing driver\n" + carService.getAll());

        System.out.println("Testing getAllByDriver(driverId) method\n"
                + carService.getAllByDriver(1L));
    }
}
