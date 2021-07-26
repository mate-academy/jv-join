package mate.jdbc;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import mate.jdbc.lib.Injector;
import mate.jdbc.model.Car;
import mate.jdbc.model.Driver;
import mate.jdbc.model.Manufacturer;
import mate.jdbc.service.CarService;
import mate.jdbc.service.DriverService;

public class Main {
    private static final Injector injector = Injector.getInstance("mate.jdbc");
    private static final DriverService driverService =
            (DriverService) injector.getInstance(DriverService.class);
    private static final CarService carService =
            (CarService) injector.getInstance(CarService.class);

    public static void main(String[] args) {

        Driver driver1 = new Driver("Peter", "XX777777");
        Driver driver2 = new Driver("John", "ZZ333333");
        Driver driver3 = new Driver("George", "YY444444");

        List<Driver> drivers1 = new ArrayList<>();
        drivers1.add(driver1);
        drivers1.add(driver2);
        drivers1.add(driver3);

        List<Driver> drivers2 = new ArrayList<>();
        drivers2.add(driver1);
        drivers2.add(driver3);

        List<Driver> drivers3 = new ArrayList<>();
        drivers3.add(driver2);

        Manufacturer manufacturer1 = new Manufacturer("VW", "Germany");
        Manufacturer manufacturer2 = new Manufacturer("Peugeot", "France");

        Car car1 = new Car("Caddy", manufacturer1, drivers1);
        Car car2 = new Car("Partner", manufacturer2, drivers2);
        Car car3 = new Car("Caddy", manufacturer1, drivers3);

        car1 = carService.create(car1);
        carService.create(car2);
        car3 = carService.create(car3);
        System.out.println("3 х Single car creation");
        carService.getAll().forEach(System.out::println);
        System.out.println(System.lineSeparator());

        System.out.println("Get car by id 2");
        try {
            Car car = carService.get(2L);
            System.out.println(car);
        } catch (NoSuchElementException e) {
            System.out.println(e.getMessage() + System.lineSeparator());
        }
        System.out.println(System.lineSeparator());

        System.out.println("Get all car by driver id 1");
        carService.getAllByDriver(1L).forEach(System.out::println);
        System.out.println(System.lineSeparator());

        System.out.println("Delete car by id 2");
        boolean deleteResult = carService.delete(2L);
        carService.getAll().forEach(System.out::println);
        System.out.println("Delete result: " + deleteResult);
        System.out.println(System.lineSeparator());

        System.out.println("Updating model of car id 1");
        car1.setModel("Transporter");
        carService.update(car1);
        carService.getAll().forEach(System.out::println);
        System.out.println(System.lineSeparator());

        System.out.println("Adding driver to car id 1");
        Driver driver4 = new Driver("Lucia", "PP555555");
        driver4 = driverService.create(driver4);
        carService.addDriverToCar(driver4, car1);
        carService.getAll().forEach(System.out::println);
        System.out.println(System.lineSeparator());

        System.out.println("Removing driver John from car id 1");
        carService.removeDriverFromCar(driver2, car1);
        carService.getAll().forEach(System.out::println);
        System.out.println(System.lineSeparator());
    }
}
