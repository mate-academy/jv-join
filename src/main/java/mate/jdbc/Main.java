package mate.jdbc;

import java.util.ArrayList;
import java.util.List;
import mate.jdbc.lib.Injector;
import mate.jdbc.model.Car;
import mate.jdbc.model.Driver;
import mate.jdbc.model.Manufacturer;
import mate.jdbc.service.CarService;

public class Main {
    private static final Injector injector = Injector.getInstance("mate.jdbc");

    public static void main(String[] args) {
        Manufacturer manufacturer = new Manufacturer(3L,"Audi","German");
        List<Driver> drivers = new ArrayList<>();
        Car carAudi = new Car("audi",manufacturer,drivers);

        CarService carService = (CarService) injector.getInstance(CarService.class);
        carService.create(carAudi);

        Car carVolkswagen = carService.get(carAudi.getId());
        System.out.println(carVolkswagen);

        carVolkswagen.setModel(" Volkswagen");
        carService.update(carVolkswagen);
        System.out.println(carService.get(carVolkswagen.getId()));

        System.out.println(carService.delete(2L));

        Driver driver = new Driver();
        driver.setName("Yulia");
        carService.addDriverToCar(driver, carAudi);
        List<Car> allByDriver = carService.getAllByDriver(2L);
        System.out.println(allByDriver);
        carService.getAll().forEach(System.out::println);
    }
}

