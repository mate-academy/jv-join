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
    private static final Injector INJECTOR = Injector.getInstance("mate.jdbc");

    public static void main(String[] args) {
        CarService carService = (CarService) INJECTOR.getInstance(CarService.class);
        DriverService driverService = (DriverService) INJECTOR.getInstance(DriverService.class);
        Driver driverPetroPyatochkin = new Driver("Petro Pyatochkin", "BAH 123456");
        Driver driverTarasDudko = new Driver("Taras Dudko", "DAH 654321");
        Driver driverMaximPrus = new Driver("Maxim Prus", "SAT 546782");
        driverPetroPyatochkin = driverService.create(driverPetroPyatochkin);
        driverTarasDudko = driverService.create(driverTarasDudko);
        driverMaximPrus = driverService.create(driverMaximPrus);
        List<Driver> drivers = new ArrayList<>();
        drivers.add(driverPetroPyatochkin);
        drivers.add(driverTarasDudko);
        ManufacturerService manufacturerService =
                (ManufacturerService) INJECTOR.getInstance(ManufacturerService.class);
        Manufacturer audi = new Manufacturer("Audi", "Germany");
        Manufacturer opel = new Manufacturer("Opel", "Germany");
        audi = manufacturerService.create(audi);
        opel = manufacturerService.create(opel);
        Car car_1 = new Car();
        car_1.setManufacturer(audi);
        car_1.setModel("A8");
        car_1.setDrivers(drivers);
        car_1 = carService.create(car_1);
        Car car_2 = new Car();
        car_2.setManufacturer(opel);
        car_2.setDrivers(drivers);
        car_2.setModel("Astra");
        car_2 = carService.create(car_2);
        carService.getAll().forEach(System.out::println);
        System.out.println("Removed driver: " + driverPetroPyatochkin.getName()
                + " ------------------------------------------------------------------------");
        carService.removeDriverFromCar(driverPetroPyatochkin, car_1);
        carService.getAll().forEach(System.out::println);
        System.out.println("Add driver: " + driverMaximPrus.getName()
                + " ------------------------------------------------------------------------");
        carService.addDriverToCar(driverMaximPrus, car_2);
        carService.getAll().forEach(System.out::println);
    }
}
