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
    private static Injector injector = Injector.getInstance("mate.jdbc");

    public static void main(String[] args) {
        DriverService driverService = (DriverService) injector.getInstance(DriverService.class);
        ManufacturerService manufacturerService = (ManufacturerService) injector
                .getInstance(ManufacturerService.class);

        Manufacturer manufacturer = manufacturerService.get(2L);
        Driver driver1 = new Driver("Leo", "711199");
        Driver driver2 = new Driver("Alexa", "922278");
        driverService.create(driver1);
        driverService.create(driver2);
        Car car = new Car(6L,"S1", manufacturer);
        car.setDrivers(List.of(driver1, driver2));

        CarService carService = (CarService) injector.getInstance(CarService.class);
        System.out.println(carService.create(car));
        System.out.println(carService.get(6L).toString());
        carService.getAll().forEach(System.out::println);
        System.out.println(carService.delete(3L));

        Driver driver3 = new Driver("Masha", "933399");
        Driver driver4 = new Driver("Stepan", "944491");
        driverService.create(driver3);
        driverService.create(driver4);
        Car carUpdate = new Car(6L,"S3", manufacturer);
        carUpdate.setDrivers(List.of(driver3, driver4));
        System.out.println(carService.update(carUpdate));

        Car car3 = carService.get(1L);
        System.out.println(carService.get(1L));
        Driver driver5 = new Driver("Alisa", "123479");
        driverService.create(driver5);
        carService.addDriverToCar(driver5, car3);
        System.out.println(carService.get(1L));
        System.out.println(carService.delete(1L));
    }
}
