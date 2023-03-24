package mate.jdbc;

import java.util.List;
import mate.jdbc.lib.Injector;
import mate.jdbc.model.Car;
import mate.jdbc.model.Manufacturer;
import mate.jdbc.service.CarService;

public class Main {
    private static final Injector injector = Injector.getInstance("mate.jdbc");
    public static void main(String[] args) {

        Manufacturer manufacturer = new Manufacturer(18L, "FORD", "USA");
        manufacturer.setId(18L);


        Car car = new Car();
        car.setModel("306");
        car.setId(3L);
        car.setManufacturer(manufacturer);

        CarService carService = (CarService) injector.getInstance(CarService.class);
        System.out.println(carService.update(car));


        //add more data to DB
    }
}
