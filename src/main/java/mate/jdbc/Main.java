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
        ManufacturerService manufacturerService = (ManufacturerService)
                injector.getInstance(ManufacturerService.class);
        CarService carService = (CarService) injector.getInstance(CarService.class);
        DriverService driverService = (DriverService) injector.getInstance(DriverService.class);
        Manufacturer mercedes = new Manufacturer("bmw", "germany");
        mercedes = manufacturerService.create(mercedes);
        Manufacturer bmw = new Manufacturer("mercedes", "germany");
        bmw = manufacturerService.create(bmw);
        Car bmw330 = new Car("330", bmw);
        Car merdesC = new Car("c20", mercedes);
        Car firstCar = carService.create(bmw330);
        Car secondCar = carService.create(merdesC);
        Driver ivan = new Driver("Ivan", "d2jk2dw");
        ivan = driverService.create(ivan);
        Driver petro = new Driver("Petro", "djk22dw");
        petro = driverService.create(ivan);
        firstCar.setModel("332");
        System.out.println(carService.get(1L));
        System.out.println(carService.getAll());
        List<Driver> drivers = new ArrayList<>();
        drivers.add(ivan);
        drivers.add(petro);
        firstCar.setDrivers(drivers);
        carService.update(firstCar);
        System.out.println(firstCar);
        Driver mikola = new Driver("Mikola","ddddd33");
        mikola = driverService.create(mikola);
        carService.addDriverToCar(mikola, firstCar);
        carService.removeDriverFromCar(ivan, firstCar);
        System.out.println(firstCar);
        carService.getAllByDriver(petro.getId());
    }
}
