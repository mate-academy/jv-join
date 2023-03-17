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
        Manufacturer manufacturerUsa = new Manufacturer(null,"Ford", "USA");
        Manufacturer manufacturerJapan = new Manufacturer(null,"Toyota", "Japan");
        Manufacturer manufacturerSweden = new Manufacturer(null,"Volvo", "Sweden");
        manufacturerService.create(manufacturerUsa);
        manufacturerService.create(manufacturerJapan);
        manufacturerService.create(manufacturerSweden);

        DriverService driverService =
                (DriverService) injector.getInstance(DriverService.class);
        Driver john = new Driver(null,"John","12345678");
        Driver bob = new Driver(null,"Bob","23456789");
        Driver alex = new Driver(null,"Alex","34567890");
        driverService.create(john);
        driverService.create(bob);
        driverService.create(alex);

        List<Driver> driverList = new ArrayList<>();
        driverList.add(john);
        driverList.add(bob);
        driverList.add(alex);

        CarService carService = (CarService) injector.getInstance(CarService.class);
        Car focus = new Car(null,"Focus", manufacturerUsa,driverList);
        Car s60 = new Car(null,"S60",manufacturerSweden,driverList);
        Car corolla = new Car(null,"Corolla", manufacturerJapan, driverList);
        carService.create(focus);
        carService.create(s60);
        carService.create(corolla);

        carService.addDriverToCar(john, focus);
        carService.addDriverToCar(bob, s60);
        carService.addDriverToCar(alex, corolla);

        System.out.println("Car service original: \n" + carService.getAll());

        Car volvoUpdated = carService.get(2L);
        volvoUpdated.setModel("sc90");
        volvoUpdated.setDriver(driverList);
        carService.update(volvoUpdated);

        System.out.println("Car service updated: \n" + carService.getAll());

        carService.delete(3L);
        System.out.println("Car service delete: \n" + carService.getAll());
    }
}
