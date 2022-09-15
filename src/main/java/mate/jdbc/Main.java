package mate.jdbc;

import mate.jdbc.lib.Injector;
import mate.jdbc.model.Car;
import mate.jdbc.model.Driver;
import mate.jdbc.model.Manufacturer;
import mate.jdbc.service.CarService;
import java.util.ArrayList;
import java.util.List;


public class Main {
    public static final Injector injector = Injector.getInstance("mate.jdbc");

    public static void main(String[] args) {
        CarService carService = (CarService) injector.getInstance(CarService.class);

        // get car
        System.out.println(carService.get(2L));

        // get all cars
        carService.getAll().forEach(System.out::println);

        // update car
        Car car = new Car();
        car.setId(2L);
        car.setModel("mini");

        Manufacturer manufacturer = new Manufacturer();
        manufacturer.setId(10L);
        car.setManufacturer(manufacturer);

        Driver von = new Driver();
        von.setId(12L);
        Driver tim = new Driver();
        tim.setId(11L);
        List<Driver> driverList = new ArrayList<>();
        driverList.add(von);
        driverList.add(tim);
        car.setDriverList(driverList);

        carService.update(car);
    }
}
