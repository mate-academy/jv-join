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
        ManufacturerService manufacturerService =
                (ManufacturerService) injector.getInstance(ManufacturerService.class);
        Manufacturer ford = new Manufacturer("Ford", "USA");
        System.out.println(manufacturerService.create(ford));
        Manufacturer volkswagen = new Manufacturer("Volkswagen", "Germany");
        System.out.println(manufacturerService.create(volkswagen));
        Manufacturer nissan = new Manufacturer("Nissan", "Japan");
        System.out.println(manufacturerService.create(nissan));

        DriverService driverService =
                (DriverService) injector.getInstance(DriverService.class);
        List<Driver> fordDrivers = new ArrayList<>();
        Driver gogi = new Driver("Gogi", "AA0777AE");
        System.out.println(driverService.create(gogi));
        System.out.println(fordDrivers.add(gogi));
        List<Driver> volkswagenDrivers = new ArrayList<>();
        Driver kaka = new Driver("Kaka", "OI9999KK");
        System.out.println(driverService.create(kaka));
        System.out.println(volkswagenDrivers.add(kaka));
        List<Driver> nissanDrivers = new ArrayList<>();
        Driver vano = new Driver("Vano", "AA0001AA");
        System.out.println(driverService.create(vano));
        System.out.println(nissanDrivers.add(vano));

        CarService carService =
                (CarService) injector.getInstance(CarService.class);
        Car fordCar = new Car("Focus", ford, fordDrivers);
        System.out.println(carService.create(fordCar));
        Car volkswagenCar = new Car("Jetta", volkswagen, volkswagenDrivers);
        System.out.println(carService.create(volkswagenCar));
        Car nissanCar = new Car("Tune", nissan, nissanDrivers);
        System.out.println(carService.create(nissanCar));

        List<Car> allCars = carService.getAll();
        allCars.forEach(System.out::println);
    }
}
