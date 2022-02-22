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
        final ManufacturerService manufacturerService = (ManufacturerService)
                injector.getInstance(ManufacturerService.class);
        final CarService carService = (CarService)
                injector.getInstance(CarService.class);
        final DriverService driverService = (DriverService)
                injector.getInstance(DriverService.class);
        Manufacturer manufacturer1 = new Manufacturer("SAAB", "Sweden");
        Manufacturer manufacturer2 = new Manufacturer("Chevrolet", "USA");
        Manufacturer manufacturer3 = new Manufacturer("Alfa Romeo", "Italy");
        Manufacturer manufacturer4 = new Manufacturer("Peugeot", "France");
        manufacturerService.create(manufacturer1);
        manufacturerService.create(manufacturer2);
        manufacturerService.create(manufacturer3);
        manufacturerService.create(manufacturer4);
        final Driver driver1 = new Driver("Beowulf","ui425387");
        final Driver driver2 = new Driver("Darin","oi769305");
        final Driver driver3 = new Driver("Balin","kl628527");
        final Driver driver4 = new Driver("Frodo","pa762900");
        final Driver driver5 = new Driver("Gimli","jw920035");
        final Driver driver6 = new Driver("Aragorn","ak628411");
        List<Driver> firstDriversArray = new ArrayList<>();
        firstDriversArray.add(driver1);
        firstDriversArray.add(driver2);
        List<Driver> secondDriversArray = new ArrayList<>();
        secondDriversArray.add(driver3);
        secondDriversArray.add(driver4);
        List<Driver> thirdDriversArray = new ArrayList<>();
        thirdDriversArray.add(driver5);
        thirdDriversArray.add(driver6);
        driverService.create(driver1);
        driverService.create(driver2);
        driverService.create(driver3);
        driverService.create(driver4);
        driverService.create(driver5);
        driverService.create(driver6);
        Car car1 = new Car("9-4X",manufacturer1,firstDriversArray);
        Car car2 = new Car("Volt",manufacturer2,secondDriversArray);
        Car car3 = new Car("Giulia",manufacturer3,thirdDriversArray);
        carService.create(car1);
        carService.create(car2);
        carService.create(car3);
        carService.get(1L);
        carService.addDriverToCar(driver1,car1);
        carService.getAllByDriver(1L);
        carService.delete(1L);
        carService.removeDriverFromCar(driver3,car2);
    }
}
