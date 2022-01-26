package mate.jdbc;

import mate.jdbc.lib.Injector;
import mate.jdbc.model.Car;
import mate.jdbc.model.Driver;
import mate.jdbc.service.CarService;
import mate.jdbc.service.ManufacturerService;

import java.util.ArrayList;
import java.util.List;

public class Main {
    private static final Injector injector = Injector.getInstance("mate.jdbc");

    public static void main(String[] args) {

        ManufacturerService manufacturerService = (ManufacturerService) injector.getInstance(ManufacturerService.class);

        Driver driver6 = new Driver("Gadiya 12", "121212");
        driver6.setId(12L);

        Driver driver5 = new Driver("Gadiya 10", "101010");
        driver5.setId(10L);

        List<Driver> driverList = new ArrayList<>(List.of(driver5, driver6));

        Driver driver55 = new Driver("Gadiya 55", "505050");

        Car carGetCreate = Car.builder()
                .model("Tazik7")
                .manufacturer(manufacturerService.get(4L))
                .drivers(driverList)
                .build();

        Car carUpdate = Car.builder()
                .id(1L)
                .model("Tazik7")
                .manufacturer(manufacturerService.get(4L))
                .drivers(driverList)
                .build();



        CarService carService = (CarService) injector.getInstance(CarService.class);
        //Car car2 = carService.create(car);
        //System.out.println(car2);
        //Car car1 = carService.get(1L);
        //System.out.println(car1);
        //System.out.println(carService.delete(car1.getId()));

        //carService.getAll().forEach(System.out::println);
        // carService.getAllByDriver(12L).forEach(System.out::println);
        //System.out.println(carService.get(1L));
        System.out.println("................");
        System.out.println("................");
        //carService.update(carUpdate);
        //carService.addDriverToCar(driver6, carUpdate);
        System.out.println(carService.get(carUpdate.getId()));
        //carService.removeDriverFromCar(driver6, carUpdate);
        carService.addDriverToCar(driver55, carUpdate);
        System.out.println(carService.get(carUpdate.getId()));
    }
}
