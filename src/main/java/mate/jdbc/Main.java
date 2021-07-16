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
    private static final CarService carService = (CarService) injector
            .getInstance(CarService.class);
    private static final DriverService driverService = (DriverService) injector
            .getInstance(DriverService.class);
    private static final ManufacturerService manufacturerService = (ManufacturerService) injector
            .getInstance(
                    ManufacturerService.class);

    public static void main(String[] args) {
        final ManufacturerService manufacturerService =
                (ManufacturerService) injector.getInstance(ManufacturerService.class);
        final DriverService driverService =
                (DriverService) injector.getInstance(DriverService.class);
        final CarService carService =
                (CarService) injector.getInstance(CarService.class);
        Driver yurka = new Driver("Ykura", "100751");
        driverService.create(yurka);
        Driver masyanya = new Driver("masyan4ya", "0102426");
        driverService.create(masyanya);
        Manufacturer bmw = new Manufacturer("bmw", "Germany");
        Manufacturer honda = new Manufacturer("HONDA", "Korea");
        manufacturerService.create(bmw);
        manufacturerService.create(honda);
        List<Driver> driverList1 = List.of(yurka, masyanya);
        List<Driver> driverList2 = new ArrayList<>();
        Car myBmv = new Car("BMW", bmw);
        myBmv.setDrivers(driverList1);
        carService.create(myBmv);
        Car noMyHonda = new Car("Honda", honda);
        noMyHonda.setDrivers(driverList2);
        carService.create(noMyHonda);
        carService.getAllByDriver(17L).forEach(System.out::println);
        carService.delete(17L);
        carService.getAll().forEach(System.out::println);
    }
}

