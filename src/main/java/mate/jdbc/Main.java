package mate.jdbc;

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
        ManufacturerService manufacturerService =
                (ManufacturerService) injector.getInstance(ManufacturerService.class);
        DriverService driverService =
                (DriverService) injector.getInstance(DriverService.class);

        Manufacturer manufacturerTesla = new Manufacturer(null, "Tesla", "USA");
        manufacturerService.create(manufacturerTesla);

        Driver driverArtem = new Driver(null, "Artem Shevchenko", "12345");
        driverService.create(driverArtem);

        Driver driverAmina = new Driver(null, "Amina Franko", "56789");
        driverService.create(driverAmina);

        CarService carService =
                (CarService) injector.getInstance(CarService.class);

        Car carTeslaOne = new Car(null, "Tesla", manufacturerTesla, null);
        carService.create(carTeslaOne);

        Car carTeslaTwo = new Car(null, "Tesla", manufacturerTesla, null);
        carService.create(carTeslaTwo);

        carService.addDriverToCar(driverArtem, carTeslaOne);
        carService.addDriverToCar(driverAmina, carTeslaTwo);
        System.out.println(carTeslaOne);
        System.out.println(carTeslaTwo);

        carService.removeDriverFromCar(driverArtem, carTeslaOne);
        System.out.println(carTeslaOne);

        List<Car> allCarsDriverArtem = carService.getAllByDriver(driverAmina.getId());
        for (Car car : allCarsDriverArtem) {
            System.out.println(car);
        }

        System.out.println(carService.get(55L));

        carService.delete(55L);

        carService.getAll().forEach(System.out::println);
    }
}
