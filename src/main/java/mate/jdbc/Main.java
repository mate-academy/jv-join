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
    private static Injector injector = Injector.getInstance("mate.jdbc");

    public static void main(String[] args) {

        ManufacturerService manufacturerService =
                (ManufacturerService) injector.getInstance(ManufacturerService.class);
        final CarService carService =
                (CarService) injector.getInstance(CarService.class);
        final DriverService driverService =
                (DriverService) injector.getInstance(DriverService.class);

        Manufacturer manufacturer1 = new Manufacturer("Toyota", "Japan");
        Manufacturer manufacturer2 = new Manufacturer("BMW", "Germany");
        Manufacturer manufacturer3 = new Manufacturer("Renault", "France");
        Manufacturer manufacturer4 = new Manufacturer("FIAT", "Italy");
        manufacturerService.create(manufacturer1);
        manufacturerService.create(manufacturer2);
        manufacturerService.create(manufacturer3);
        manufacturerService.create(manufacturer4);

        final Driver driver1 = new Driver("Ivan","I049");
        final Driver driver2 = new Driver("Bob","I456");
        final Driver driver3 = new Driver("Alice","I187");
        final Driver driver4 = new Driver("Dave","I569");
        final Driver driver5 = new Driver("Nick","I345");
        final Driver driver6 = new Driver("Mike","I672");
        final Driver driver7 = new Driver("Santa","I612");
        final Driver driver8 = new Driver("Chi","I674");
        List<Driver> firstDriversArray = new ArrayList<>();
        firstDriversArray.add(driver1);
        firstDriversArray.add(driver2);
        List<Driver> secondDriversArray = new ArrayList<>();
        secondDriversArray.add(driver3);
        secondDriversArray.add(driver4);
        List<Driver> thirdDriversArray = new ArrayList<>();
        thirdDriversArray.add(driver5);
        thirdDriversArray.add(driver6);
        List<Driver> fourthDriversArray = new ArrayList<>();
        fourthDriversArray.add(driver7);
        fourthDriversArray.add(driver8);
        driverService.create(driver1);
        driverService.create(driver2);
        driverService.create(driver3);
        driverService.create(driver4);
        driverService.create(driver5);
        driverService.create(driver6);
        driverService.create(driver7);
        driverService.create(driver8);

        Car car1 = new Car("Camry",manufacturer1,firstDriversArray);
        Car car2 = new Car("X1",manufacturer2,secondDriversArray);
        Car car3 = new Car("Clio",manufacturer3,thirdDriversArray);
        Car car4 = new Car("Punto",manufacturer4,fourthDriversArray);
        carService.create(car1);
        carService.create(car2);
        carService.create(car3);
        carService.create(car4);
        carService.get(1L);
        carService.getAll();
        carService.addDriverToCar(driver1,car1);
        carService.getAllByDriver(1L);
        carService.delete(1L);
        carService.removeDriverFromCar(driver3,car2);
    }
}
