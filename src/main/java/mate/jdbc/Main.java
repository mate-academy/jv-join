package mate.jdbc;

import java.util.ArrayList;
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
        Driver driver1 = driverService.create(new Driver("Bob", "1234ew22q"));
        Driver driver2 = driverService.create(new Driver("John", "44eet34"));
        Driver driver3 = driverService.create(new Driver("Mark", "24fet37"));

        ManufacturerService manufacturerService =
                (ManufacturerService) injector.getInstance(ManufacturerService.class);
        List<Driver> driversBmw = new ArrayList<>();
        driversBmw.add(driver1);
        driversBmw.add(driver2);
        driversBmw.add(driver3);

        Manufacturer bmw = manufacturerService.create(new Manufacturer("BMW", "Germany"));
        Car bmwX5Car = new Car();
        bmwX5Car.setModel("X5");
        bmwX5Car.setManufacturer(bmw);
        bmwX5Car.setDrivers(driversBmw);

        CarService carService = (CarService) injector.getInstance(CarService.class);
        bmwX5Car = carService.create(bmwX5Car);

        List<Driver> driversDaewoo = new ArrayList<>();
        driversDaewoo.add(driver1);
        driversDaewoo.add(driver2);

        Manufacturer daewoo = manufacturerService.create(new Manufacturer("Daewoo", "South Korea"));
        Car daewooAveoCar = new Car();
        daewooAveoCar.setModel("Aveo");
        daewooAveoCar.setManufacturer(daewoo);
        daewooAveoCar.setDrivers(driversDaewoo);
        daewooAveoCar = carService.create(daewooAveoCar);

        driversDaewoo.add(driver1);
        Manufacturer cherry = manufacturerService.create(new Manufacturer("Cherry", "China"));
        Car cherryQqCar = new Car();
        cherryQqCar.setModel("QQ");
        cherryQqCar.setManufacturer(cherry);
        List<Driver> driversCherry = new ArrayList<>();
        cherryQqCar.setDrivers(driversCherry);
        cherryQqCar = carService.create(cherryQqCar);

        System.out.println("BMW X5 car" + carService.get(bmwX5Car.getId()));
        carService.removeDriverFromCar(driver1, bmwX5Car);
        System.out.println("BMW X5 list of drivers after removing driver from car" + driver1
                + "\n" + carService.get(bmwX5Car.getId()).getDrivers());

        System.out.println("BMW X5 list of drivers after adding driver to car" + driver1);
        carService.addDriverToCar(driver1, bmwX5Car);
        System.out.println("BMW X5 list of drivers"
                + carService.get(bmwX5Car.getId()).getDrivers());

        System.out.println("Cars in DB:");
        carService.getAll().forEach(System.out::println);

        System.out.println("Cars in DB after delete BMW");
        carService.delete(bmwX5Car.getId());
        carService.getAll().forEach(System.out::println);

        System.out.println("Add driver" + driver3 + "to" + cherryQqCar);
        cherryQqCar.getDrivers().add(driver3);
        carService.update(cherryQqCar);
        System.out.println("Drivers in car: " + cherryQqCar);
    }
}
