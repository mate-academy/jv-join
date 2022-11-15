package mate.jdbc;

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
        ManufacturerService manufacturerService =
                (ManufacturerService) INJECTOR.getInstance(ManufacturerService.class);
        Manufacturer fordManufacturer = new Manufacturer("Ford Motor", "USA");
        Manufacturer porscheManufacturer = new Manufacturer("Porsche", "Germany");
        manufacturerService.create(fordManufacturer);
        manufacturerService.create(porscheManufacturer);

        DriverService driverService = (DriverService) INJECTOR.getInstance(DriverService.class);
        Driver johnDriver = new Driver("John", "ued1234567");
        Driver nickDriver = new Driver("Nick", "prh6543210");
        Driver alexDriver = new Driver("Alex", "emq9632580");
        Driver denisDriver = new Driver("Denis", "yye32191530");
        driverService.create(johnDriver);
        driverService.create(nickDriver);
        driverService.create(alexDriver);
        driverService.create(denisDriver);

        CarService carService = (CarService) INJECTOR.getInstance(CarService.class);
        Car fordCar = new Car("Capri", fordManufacturer,
                List.of(johnDriver, alexDriver));
        Car porscheCar = new Car("GT4 RS", porscheManufacturer,
                List.of(nickDriver, alexDriver));
        carService.create(fordCar);
        carService.create(porscheCar);
        carService.removeDriverFromCar(nickDriver, fordCar);
        carService.addDriverToCar(denisDriver, fordCar);
        carService.get(fordCar.getId());
        carService.getAllByDriver(alexDriver.getId()).forEach(System.out::println);
    }
}
