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

    }
}
