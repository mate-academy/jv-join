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
        System.out.println("Create manufacturers");
        Manufacturer manufacturer1 = new Manufacturer();
        manufacturer1.setName("BMW Group");
        manufacturer1.setCountry("Germany");
        Manufacturer manufacturer2 = new Manufacturer();
        manufacturer2.setName("Honda Motor");
        manufacturer2.setCountry("Japan");

        ManufacturerService manufacturerService = (ManufacturerService) injector
                .getInstance(ManufacturerService.class);

        manufacturer1 = manufacturerService.create(manufacturer1);
        System.out.println(manufacturer1);
        manufacturer2 = manufacturerService.create(manufacturer2);
        System.out.println(manufacturer2);

        System.out.println("Create drivers");
        Driver driver1 = new Driver();
        driver1.setName("John");
        driver1.setLicenseNumber("123456");
        Driver driver2 = new Driver();
        driver2.setName("Bill");
        driver2.setLicenseNumber("112233");

        DriverService driverService = (DriverService) injector
                .getInstance(DriverService.class);

        driver1 = driverService.create(driver1);
        System.out.println(driver1);
        driver2 = driverService.create(driver2);
        System.out.println(driver2);

        System.out.println("Create cars");
        Car car1 = new Car();
        car1.setModel("The X1");
        car1.setManufacturer(manufacturer1);
        car1.setDrivers(List.of(driver1));
        Car car2 = new Car();
        car2.setModel("2023 HR-V");
        car2.setManufacturer(manufacturer2);
        car2.setDrivers(List.of(driver2));

        CarService carService = (CarService) injector
                .getInstance(CarService.class);

        car1 = carService.create(car1);
        System.out.println(car1);
        car2 = carService.create(car2);
        System.out.println(car2);

        System.out.println("Get car");
        car1 = carService.get(car1.getId());
        System.out.println(car1);

        System.out.println("Get all cars");
        List<Car> cars = carService.getAll();
        cars.forEach(System.out::println);

        System.out.println("Update car");
        car1.setModel("2022 Pilot");
        car1.setManufacturer(manufacturer2);
        car1 = carService.update(car1);
        System.out.println(car1);

        System.out.println("Add driver to car");
        carService.addDriverToCar(driver2, car1);
        car1 = carService.get(car1.getId());
        System.out.println(car1);

        System.out.println("Remove driver from car");
        carService.removeDriverFromCar(driver2, car1);
        car1 = carService.get(car1.getId());
        System.out.println(car1);

        System.out.println("Get all cars by driver");
        carService.addDriverToCar(driver2, car1);
        List<Car> carsByDriver = carService.getAllByDriver(driver2.getId());
        carsByDriver.forEach(System.out::println);

        System.out.println("Delete car");
        System.out.println(carService.delete(car2.getId()));
        cars = carService.getAll();
        cars.forEach(System.out::println);
    }
}
