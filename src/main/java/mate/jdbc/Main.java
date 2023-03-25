package mate.jdbc;

import java.util.List;
import mate.jdbc.lib.Injector;
import mate.jdbc.model.Car;
import mate.jdbc.model.Manufacturer;
import mate.jdbc.service.CarService;

public class Main {
    private static final Injector injector = Injector.getInstance("mate.jdbc");

    public static void main(String[] args) {

//        Car car = new Car();
//        car.setId(4L);
//        car.setModel("Mondeo");
//        car.setManufacturer(new Manufacturer(13L, "Mahindra", "India"));

        CarService carService = (CarService) injector.getInstance(CarService.class);

        for(Car car: carService.getAllByDriver(6L)) {
            System.out.println(car);
        }
    }
}
