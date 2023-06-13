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
        DriverService driverService = (DriverService) injector.getInstance(DriverService.class);
        Driver oleksandr = driverService.create(new Driver("Oleksandr", "11119"));
        Driver vasil = driverService.create(new Driver("Vasil", "11120"));
        System.out.println(driverService.get(oleksandr.getId()));
        System.out.println(driverService.get(vasil.getId()));
        List<Driver> driverList = new ArrayList<>();
        driverList.add(vasil);
        driverList.add(oleksandr);
        ManufacturerService manufacturerService
                = (ManufacturerService) injector.getInstance((ManufacturerService.class));
        Manufacturer audi = manufacturerService.create(new Manufacturer("Audi", "Germany"));
        Manufacturer audiFromDB = manufacturerService.get(audi.getId());
        CarService carService = (CarService) injector.getInstance(CarService.class);
        Car car = new Car("Sportback", audiFromDB, driverList);
        carService.create(car);
        System.out.println(carService.get(car.getId()));
        car.setModel("Q5 Sportback");
        carService.update(car);
        carService.getAll().stream().forEach(System.out::println);
        Driver mykola = driverService.create(new Driver("Mykola", "11121"));
        carService.addDriverToCar(mykola, car);
        carService.getAll().stream().forEach(System.out::println);
        carService.removeDriverFromCar(oleksandr, car);
        carService.getAll().stream().forEach(System.out::println);
        System.out.println(carService.getAllByDriver(vasil.getId()));
        carService.delete(car.getId());
        carService.getAll().stream().forEach(System.out::println);
    }
}
