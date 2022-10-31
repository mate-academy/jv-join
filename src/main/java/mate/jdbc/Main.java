package mate.jdbc;

import java.util.List;
import mate.jdbc.lib.Injector;
import mate.jdbc.model.Car;
import mate.jdbc.service.CarService;
import mate.jdbc.service.DriverService;
import mate.jdbc.service.ManufacturerService;

public class Main {
    private static final Injector injector = Injector.getInstance("mate.jdbc");

    public static void main(String[] args) {
        ManufacturerService manufacturerService =
                (ManufacturerService) injector.getInstance(ManufacturerService.class);
        DriverService driverService = (DriverService) injector.getInstance(DriverService.class);
        CarService carService = (CarService) injector.getInstance(CarService.class);
        //        Manufacturer bmw = manufacturerService.get(18L);
        //        Car X5 = new Car("X5",bmw);
        //        carService.create(X5);
        //        System.out.println(carService.get(1L));

        //        Driver fedya = new Driver("fedya","de987456");
        //        Driver misha = new Driver("misha","au111111");
        //        Driver vitya = driverService.get(4L);
        //        Manufacturer getbmw = manufacturerService.get(12L);
        //        System.out.println(vitya);
        //        Car car = new Car("Land Cruser",getbmw, List.of(vitya));
        //        System.out.println(carService.create(car));

        //        List<Car> cars = carService.getAll();
        //        System.out.println(cars);
        // carService.delete(2L);

        List<Car> cars = carService.getAll();
        cars.forEach((x) -> System.out.println(x));
    }
}
