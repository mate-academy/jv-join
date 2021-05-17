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
    private static final Driver DRIVER = new Driver("Ahmed Mohamed", "OKR 654891");
    private static final Driver DRIVER_2 = new Driver("Nayara Mohamed", "SFT 254211");
    private static final Driver DRIVER_3 = new Driver("Herman Mayer", "BRD 890721");
    private static final Manufacturer MANUFACTURER = new Manufacturer("Toyota", "Japan");
    private static final Manufacturer MANUFACTURER_2 = new Manufacturer("Honda", "Japan");
    private static final List<Driver> DRIVER_LIST = new ArrayList<>();
    private static final Car CAR = new Car(1998, "CA9021OP", DRIVER_LIST, MANUFACTURER);
    
    public static void main(String[] args) {
        DriverService driverService = (DriverService) injector.getInstance(DriverService.class);
        driverService.create(DRIVER);
        driverService.create(DRIVER_2);
        driverService.create(DRIVER_3);
        
        ManufacturerService manufacturerService = (ManufacturerService) injector
                .getInstance(ManufacturerService.class);
        manufacturerService.create(MANUFACTURER);
        manufacturerService.create(MANUFACTURER_2);
        
        CarService carService = (CarService) injector.getInstance(CarService.class);
        DRIVER_LIST.add(DRIVER);
        carService.create(CAR);
        carService.getAll().forEach(System.out::println);
        System.out.println(carService.get(4L));
        
        MANUFACTURER_2.setId(3L);
        CAR.setYear(2015);
        DRIVER_LIST.add(DRIVER_2);
        CAR.setManufacturer(MANUFACTURER_2);
        System.out.println(carService.update(CAR));
        
        System.out.println("get all by driver: ");
        carService.getAllByDriver(DRIVER_2.getId()).forEach(System.out::println);
        
        System.out.println(System.lineSeparator() + "adding driver to car");
        carService.addDriverToCar(DRIVER_3, CAR);
        System.out.println(
                System.lineSeparator() + "get car after adding driver: " + System.lineSeparator()
                        + CAR + System.lineSeparator());
        carService.removeDriverFromCar(DRIVER, CAR);
        System.out.println("get car after deleting driver: " + System.lineSeparator() + CAR + System
                .lineSeparator());
        System.out.println("deleting car" + CAR);
        System.out.println(carService.delete(CAR.getId()));
        System.out.println(System.lineSeparator() + "get all after deletion:");
        carService.getAll().forEach(System.out::println);
    }
}
