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
        // create drivers
        DriverService driverService =
                (DriverService) injector.getInstance(DriverService.class);
        Driver driverDima = new Driver(null, "Dima", "12345");
        Driver driverOleg = new Driver(null, "Oleg", "56789");
        driverService.create(driverDima);
        driverService.create(driverOleg);
        // create manufacturers
        ManufacturerService manufacturerService =
                (ManufacturerService) injector.getInstance(ManufacturerService.class);
        Manufacturer hyundai = new Manufacturer(null, "hyundai", "Korea");
        Manufacturer kia = new Manufacturer(null, "KIA", "Korea");
        manufacturerService.create(hyundai);
        manufacturerService.create(kia);
        //method create
        List<Driver> drivers = new ArrayList<>();
        drivers.add(driverDima);
        drivers.add(driverOleg);
        CarService carService = (CarService) injector.getInstance(CarService.class);
        Car carSonata = new Car(null, "Sonata", hyundai, drivers);
        carService.create(carSonata);
        // method get
        Car carFromDb = carService.get(1L);
        System.out.println(carFromDb);
        // method getAll
        Car carKia = new Car(null, "Kia", kia, drivers);
        carService.create(carKia);
        List<Car> cars = carService.getAll();
        cars.stream().forEach(System.out::println);
        // method update
        drivers.remove(0);
        carKia.setModel("NO");
        carService.update(carKia);
        System.out.println(carService.get(carKia.getId()));
        // method delete
        System.out.println("----- expected one car -----");
        carService.delete(2L);
        System.out.println("Number of cars: " + carService.getAll().size());
        // method addDriverToCar
        Driver driverBob = new Driver(null,"Bob", "66666");
        driverService.create(driverBob);
        Car carOne = carService.get(1L);
        System.out.println("Before: " + carOne);
        carService.addDriverToCar(driverBob, carOne);
        carOne = carService.get(1L);
        System.out.println("After: " + carOne);
        // method removeDriverFromCar
        carService.removeDriverFromCar(driverBob, carOne);
        System.out.println(carService.get(1L));
        // method getAllByDriver
        System.out.println("----- expected one car with driver Dima -----");
        carService.getAllByDriver(driverDima.getId()).stream().forEach(System.out::println);
    }
}
