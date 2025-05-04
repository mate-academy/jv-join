package mate.jdbc;

import java.util.ArrayList;
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
        ManufacturerService manufacturerService
                = (ManufacturerService) injector.getInstance(ManufacturerService.class);
        DriverService driverService
                = (DriverService) injector.getInstance(DriverService.class);
        CarService carService
                = (CarService) injector.getInstance(CarService.class);

        System.out.println(manufacturerService.getAll());

        System.out.println(driverService.getAll());

        Manufacturer manufacturerHonda = manufacturerService.create(new Manufacturer(null,
                "Honda", "Japan"));
        Car carHonda = carService.create(new Car(null,
                "civic", manufacturerHonda, new ArrayList<>()));
        Driver driverLord = driverService.create(new Driver(null,
                "Lord", "PTV-95945"));
        carService.addDriverToCar(driverLord, carHonda);
        Driver driverFord = driverService.create(new Driver(null,
                "Ford", "PTF-15645"));
        carService.addDriverToCar(driverFord, carHonda);
        Driver driverLeo = driverService.create(new Driver(null,
                "Leo", "LEO-15645"));
        carService.addDriverToCar(driverLeo, carHonda);
        Manufacturer manufacturerToyota = manufacturerService.create(new Manufacturer(null,
                "Toyota", "Japan"));
        Car carToyota = carService.create(new Car(null,
                "corolla", manufacturerToyota, new ArrayList<>()));
        carService.addDriverToCar(driverLeo, carToyota);
        Car carHonda2 = carService.create(new Car(null,
                "cleo", manufacturerHonda, new ArrayList<>()));
        carService.addDriverToCar(driverLord, carHonda2);
        carService.addDriverToCar(driverFord, carHonda2);
        //carService.create(carToyota);
        System.out.println(carService.getAll());
        carService.delete(1L);
        carService.addDriverToCar(driverLord, carService.get(2L));
        System.out.println(carService.getAllByDriver(1L));

        Car car2 = carService.get(2L);
        System.out.println(car2);
        Manufacturer oldManufacturer = car2.getManufacturer();
        Manufacturer manufacturerGuty = manufacturerService.create(new Manufacturer(null,
                "Guty", "Korea"));
        manufacturerService.create(manufacturerGuty);
        car2.setManufacturer(manufacturerGuty);
        carService.update(car2);
        System.out.println(carService.get(car2.getId()));
    }
}
