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
        ManufacturerService manufacturerService = (ManufacturerService)
                injector.getInstance(ManufacturerService.class);

        Manufacturer nissan = new Manufacturer("Nissan", "Japan");
        manufacturerService.create(nissan);

        DriverService driverService = (DriverService)
                injector.getInstance(DriverService.class);

        Driver driverZhi = new Driver("Zhi", "9697");
        Driver driverXhi = new Driver("Xhi", "9494");
        Driver driverChi = new Driver("Chi", "9797");
        driverService.create(driverZhi);
        driverService.create(driverXhi);
        driverService.create(driverChi);

        CarService carService = (CarService)
                injector.getInstance(CarService.class);
        Car car = new Car("Silvia", nissan, List.of(driverZhi, driverXhi, driverChi));
        carService.create(car);
        System.out.println(carService.get(6L));
        carService.getAll().forEach(System.out::println);
        System.out.println(carService.delete(6L));
        car.setModel("Maxima");
        carService.update(car);
        carService.getAll().forEach(System.out::println);

    }
}
