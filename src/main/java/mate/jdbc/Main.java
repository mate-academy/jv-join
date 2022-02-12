package mate.jdbc;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import mate.jdbc.lib.Injector;
import mate.jdbc.model.Car;
import mate.jdbc.model.Driver;
import mate.jdbc.service.CarService;
import mate.jdbc.service.ManufacturerService;

public class Main {
    private static final Injector injector = Injector.getInstance("mate.jdbc");

    public static void main(String[] args) {

        ManufacturerService manufacturerService =
                (ManufacturerService) injector.getInstance(ManufacturerService.class);
        CarService carService = (CarService) injector.getInstance(CarService.class);

        Driver driver6 = new Driver("Stas", "121212");
        driver6.setId(12L);

        Driver driver5 = new Driver("Niks", "101010");
        driver5.setId(10L);

        List<Driver> driverList = new ArrayList<>(List.of(driver5, driver6));

        Car carGetCreate = Car.builder()
                .model("Batmobile")
                .manufacturer(manufacturerService.get(4L))
                .drivers(driverList)
                .build();

        Car car2 = carService.create(carGetCreate);
        System.out.println(car2);

        Optional<Car> car1 = carService.get(2L);
        System.out.println(car1);

        System.out.println(carService.delete(car1.get()));

        carService.getAll().forEach(System.out::println);

        carService.getAllByDriver(12L).forEach(System.out::println);

        Car carUpdate = Car.builder()
                .id(2L)
                .model("Batmobile")
                .manufacturer(manufacturerService.get(5L))
                .drivers(driverList)
                .build();

        carService.update(carUpdate);
        carService.addDriverToCar(driver6, carUpdate);
        System.out.println(carService.get(carUpdate.getId()));

        Driver driver55 = new Driver("Sergo", "505050");

        carService.removeDriverFromCar(driver6, carUpdate);
        carService.addDriverToCar(driver55, carUpdate);
        System.out.println(carService.get(carUpdate.getId()));
    }
}
