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
        Manufacturer audi = new Manufacturer("Audi", "Germany");
        Manufacturer bmw = new Manufacturer("BMW", "Germany");
        Manufacturer ford = new Manufacturer("Ford", "USA");

        manufacturerService.create(audi);
        manufacturerService.create(bmw);
        manufacturerService.create(ford);

        DriverService driverService = (DriverService) injector.getInstance(DriverService.class);
        Driver driverMax = new Driver("Max", "d123456");
        Driver driverRoman = new Driver("Roman", "d234567");
        Driver driverVlad = new Driver("Vlad", "d345678");

        driverService.create(driverMax);
        driverService.create(driverRoman);
        driverService.create(driverVlad);

        List<Driver> audiList = new ArrayList<>();
        Car audiCar = new Car("TT", audi);
        audiCar.setDriverList(audiList);
        List<Driver> bmwList = new ArrayList<>();
        Car bmwCar = new Car("X5", bmw);
        bmwCar.setDriverList(bmwList);
        List<Driver> fordList = new ArrayList<>();
        Car fordCar = new Car("Mustang GT", ford);
        fordCar.setDriverList(fordList);

        CarService carService = (CarService) injector.getInstance(CarService.class);

        carService.create(audiCar);
        carService.create(bmwCar);
        carService.create(fordCar);

        carService.addDriverToCar(driverMax, audiCar);
        carService.addDriverToCar(driverRoman, bmwCar);
        carService.addDriverToCar(driverVlad, fordCar);

        List<Car> carList = carService.getAll();
        for (Car car : carList) {
            System.out.println(car);
        }

        carService.removeDriverFromCar(driverVlad, fordCar);
        carService.addDriverToCar(driverVlad, audiCar);

        List<Car> carList1 = carService.getAll();
        for (Car car : carList1) {
            System.out.println(car);
        }

        System.out.println(carService.getAllByDriver(driverRoman.getId()));
    }
}
