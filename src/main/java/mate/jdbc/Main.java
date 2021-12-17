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
        ManufacturerService manufacturerService = (ManufacturerService) injector
                .getInstance(ManufacturerService.class);
        DriverService driverService = (DriverService) injector
                .getInstance(DriverService.class);
        Manufacturer teslaManufacturer = new Manufacturer("Tesla", "USA");
        manufacturerService.create(teslaManufacturer);
        List<Driver> drivers = new ArrayList<>();
        Driver driverJohn = new Driver("John", "12131415");
        Driver driverMatthew = new Driver("Matthew", "16171819");
        driverJohn = driverService.create(driverJohn);
        driverMatthew = driverService.create(driverMatthew);
        drivers.add(driverJohn);
        drivers.add(driverMatthew);

        CarService carService = (CarService) injector
                .getInstance(CarService.class);
        Car anotherCar = new Car("Model X", teslaManufacturer, drivers);
        carService.create(anotherCar);
        System.out.println(carService.get(anotherCar.getId()));
        Driver driverBob = new Driver("Bob", "19181716");
        driverService.create(driverBob);
        System.out.println();
        anotherCar.setModel("Model Z");
        carService.update(anotherCar);
        System.out.println();
        carService.removeDriverFromCar(driverMatthew, anotherCar);
        carService.removeDriverFromCar(driverJohn, anotherCar);
        carService.addDriverToCar(driverBob, anotherCar);
        List<Car> cars = carService.getAll();
        cars.forEach(System.out::println);
        System.out.println();
        System.out.println(carService.getAllByDriver(driverBob.getId()));
        System.out.println();
        System.out.println(carService.get(anotherCar.getId()));
        System.out.println(carService.delete(anotherCar.getId()));
    }
}
