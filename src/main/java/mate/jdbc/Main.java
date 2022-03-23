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
        ManufacturerService manufacturerService =
                (ManufacturerService) injector.getInstance(ManufacturerService.class);

        Manufacturer manufacturer1 = new Manufacturer("manufacturer1", "country1");
        Manufacturer manufacturer2 = new Manufacturer("manufacturer2", "country2");
        Manufacturer manufacturer3 = new Manufacturer("manufacturer3", "country3");
        manufacturerService.create(manufacturer1);
        manufacturerService.create(manufacturer2);
        manufacturerService.create(manufacturer3);
        manufacturerService.getAll().forEach(System.out::println);

        Driver driver1 = new Driver("driver1", "license1");
        Driver driver2 = new Driver("driver2", "license2");
        Driver driver3 = new Driver("driver3", "license3");
        Driver driver4 = new Driver("driver4", "license4");
        Driver driver5 = new Driver("driver5", "license5");
        Driver driver6 = new Driver("driver6", "license6");

        DriverService driverService = (DriverService) injector.getInstance(DriverService.class);

        driverService.create(driver1);
        driverService.create(driver2);
        driverService.create(driver3);
        driverService.create(driver4);
        driverService.create(driver5);
        driverService.create(driver6);
        driverService.getAll().forEach(System.out::println);

        Car car1 = new Car("model1",manufacturerService.get(manufacturer1.getId()));
        Car car2 = new Car("model2",manufacturerService.get(manufacturer2.getId()));
        Car car3 = new Car("model3",manufacturerService.get(manufacturer3.getId()));

        car1.setDrivers(List.of(driverService.get(driver1.getId()),
                driverService.get(driver2.getId())));
        car2.setDrivers(List.of(driverService.get(driver3.getId()),
                driverService.get(driver4.getId())));
        car3.setDrivers(List.of(driverService.get(driver5.getId()),
                driverService.get(driver6.getId())));

        CarService carService = (CarService) injector.getInstance(CarService.class);

        carService.create(car1);
        carService.create(car2);
        carService.create(car3);

        car3 = carService.get(car3.getId());
        car3.setManufacturer(manufacturerService.get(manufacturer2.getId()));
        car3.setDrivers(List.of(driverService.get(driver1.getId()),
                driverService.get(driver2.getId()),
                driverService.get(driver3.getId()),
                driverService.get(driver4.getId())));
        System.out.println(carService.update(car3));

        System.out.println(carService.delete(car1.getId()));

        carService.removeDriverFromCar(driverService.get(driver1.getId()), car3);
        car3.getDrivers().forEach(System.out::println);

        carService.addDriverToCar(driverService.get(driver6.getId()), car3);
        car3.getDrivers().forEach(System.out::println);
        carService.getAll().forEach(System.out::println);

        carService.getAllByDriver(car3.getId()).forEach(System.out::println);
    }
}
