package mate.jdbc;

import java.util.ArrayList;
import java.util.List;
import mate.jdbc.dao.CarDao;
import mate.jdbc.dao.DriverDao;
import mate.jdbc.dao.ManufacturerDao;
import mate.jdbc.dao.impl.CarDaoImpl;
import mate.jdbc.dao.impl.DriverDaoImpl;
import mate.jdbc.dao.impl.ManufacturerDaoImpl;
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
        CarService carService =
                (CarService) injector.getInstance(CarService.class);
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

        Car carTesla = new Car(null, "Tesla", manufacturerTesla, null);
        carService.create(carTesla);
        System.out.println(carTesla);

        carService.addDriverToCar(driverArtem, carTesla);
        carService.addDriverToCar(driverAmina, carTesla);
        System.out.println(carTesla);

        carService.removeDriverFromCar(driverAmina, carTesla);
        System.out.println(carTesla);


        List<Car> allCars = carService.getAll();
        for (Car car : allCars) {
            System.out.println(car);
        }

        System.out.println(" --- ");

        List<Car> allCarsDriverArtem = carService.getAllByDriver(driverArtem.getId());
        for (Car car : allCarsDriverArtem) {
            System.out.println(car);
        }

        System.out.println(carService.get(55L));

        //carService.delete(carTesla.getId());




        //carService.update(carTesla);
        //System.out.println("Updated car with ID: " + carTeslaId);

        /*if (carTesla.getDrivers() == null) {
            carTesla.setDrivers(new ArrayList<>());
        }
        List<Driver> updatedDrivers = new ArrayList<>(carTesla.getDrivers());
        updatedDrivers.add(driverAmina);
        carService.addDriverToCar(driverAmina, carTesla);
        carTesla.setDrivers(updatedDrivers);
*/
        //System.out.println(carService.get(carId));
        //carService.getAll().forEach(System.out::println)
        //carService.getAll().forEach(System.out::println);
        /*Driver driverAmina = new Driver(null, "Amina", "23456");
        driverService.create(driverAmina);
        Car carTeslaWithTwoDrivers = new Car(null, "Tesla", manufacturerTesla, List.of(driverArtem, driverAmina));
        carService.create(carTeslaWithTwoDrivers);
        */
        //carService.getAll().forEach(System.out::println);
        //carService.get()
        /*carService.getAll().forEach(System.out::println);
        driverService.getAll().forEach(System.out::println);
        manufacturerService.getAll().forEach(System.out::println);*/
    }
}
