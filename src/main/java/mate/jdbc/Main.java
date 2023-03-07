package mate.jdbc;

import java.util.ArrayList;
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
        final CarService carService = (CarService) injector
                .getInstance(CarService.class);
        final ManufacturerService manufacturerService = (ManufacturerService) injector
                .getInstance(ManufacturerService.class);
        final Driver driverTaxi = new Driver(13L, "Sami", "22222");
        Car car = new Car();
        car.setId(3L);
        car.setModel("SLAVUTA");
        car.setManufacturer(manufacturerService.get(5L));
        List<Driver> list = new ArrayList<>();
        list.add(new Driver(12L, "Taras", "123456"));
        car.setDriver(list);

        Car carUp = new Car();
        List<Driver> listUp = new ArrayList<>();
        listUp.add(new Driver(4L, "John", "004"));
        carUp.setId(3L);
        carUp.setModel("ZAZ");
        carUp.setManufacturer(new Manufacturer(2L, "AvtoZaz", "Ukraine"));
        carUp.setDriver(listUp);

        System.out.println(carService.create(car));

        System.out.println(carService.get(3L));

        carService.getAll().forEach(System.out::println);

        System.out.println(carService.update(carUp));

        System.out.println(carService.delete(4L));

        carService.addDriverToCar(driverTaxi, carUp);

        carService.removeDriverFromCar(driverTaxi, carUp);

        carService.getAllByDriver(10L).forEach(System.out::println);

        carService.addDriverToCar(driverTaxi, car);
        System.out.println(car);

        carService.removeDriverFromCar(driverTaxi, car);
        System.out.println(car);
    }
}
