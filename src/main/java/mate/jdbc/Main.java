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
        DriverService driverService = (DriverService) injector.getInstance(DriverService.class);
        Driver driver1 = new Driver("Driver1", "111111-111");
        Driver driver2 = new Driver("Driver2", "2222-2222");
        Driver driver3 = new Driver("Driver3", "333-33-3");
        Driver driver4 = new Driver("Driver4", "44-44-444");
        driverService.create(driver1);
        driverService.create(driver2);
        driverService.create(driver3);
        driverService.create(driver4);

        ManufacturerService manufacturerService =
                (ManufacturerService) injector.getInstance(ManufacturerService.class);
        Manufacturer manufacturer1 = new Manufacturer("M_name1", "country1");
        Manufacturer manufacturer2 = new Manufacturer("M_name2", "country2");
        Manufacturer manufacturer3 = new Manufacturer("M_name3", "country3");
        manufacturerService.create(manufacturer1);
        manufacturerService.create(manufacturer2);
        manufacturerService.create(manufacturer3);

        CarService carService = (CarService) injector.getInstance(CarService.class);
        Car car1 = new Car("car1",manufacturer1, List.of(driver1, driver2, driver3));
        Car car2 = new Car("car2",manufacturer3, List.of(driver3, driver4, driver1, driver2));
        Car car3 = new Car("car3",manufacturer3, List.of(driver1, driver3, driver4));
        Car car4 = new Car("car4",manufacturer2, List.of(driver1, driver2));
        carService.create(car1);
        carService.create(car2);
        carService.create(car3);
        carService.create(car4);

        System.out.println("--------------Get All----------------");
        carService.getAll().forEach(System.out::println);
        System.out.println("--------------Delete-----------------");
        System.out.println(carService.delete(2L));
        System.out.println("----------Get all by driver----------");
        carService.getAllByDriver(2L).forEach(System.out::println);
        car1.setManufacturer(manufacturer2);
        carService.update(car1);
        System.out.println("---------Get car after update--------");
        System.out.println(car1 = carService.get(car1.getId()));
        carService.removeDriverFromCar(driver1, car1);
        carService.removeDriverFromCar(driver2, car1);
        carService.addDriverToCar(driver4, car1);
        System.out.println("---Get car after remove&add driver---");
        System.out.println(carService.get(1L));
        System.out.println("--------------Get All----------------");
        carService.getAll().forEach(System.out::println);
    }
}
