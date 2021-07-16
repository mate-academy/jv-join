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
    private static Injector injector = Injector.getInstance("mate.jdbc");

    public static void main(String[] args) {
        ManufacturerService manufacturerService =
                (ManufacturerService) injector.getInstance(ManufacturerService.class);

        Manufacturer manufacturerJapan = new Manufacturer("Toyota", "Japan");
        Manufacturer manufacturerUS = new Manufacturer("Tesla", "USA");
        Manufacturer manufacturerGermany = new Manufacturer("BMW", "Germany");
        manufacturerService.create(manufacturerJapan);
        manufacturerService.create(manufacturerUS);
        manufacturerService.create(manufacturerGermany);
        System.out.println(manufacturerService.get(2L));

        DriverService driverService =
                (DriverService) injector.getInstance(DriverService.class);
        Driver driver1 = new Driver("Bob", "001");
        Driver driver2 = new Driver("Bond", "007");
        Driver driver3 = new Driver("Alice", "666");
        driverService.create(driver1);
        driverService.create(driver2);
        driverService.create(driver3);

        Car carTesla = new Car("Model S", manufacturerService.get(2L));
        Car carToyota = new Car("Corolla", manufacturerService.get(1L));
        Car carBayern = new Car("M4", manufacturerService.get(3L));
        carTesla.setDrivers(List.of(driverService.get(2L), driverService.get(1L)));
        carToyota.setDrivers(List.of(driverService.get(3L)));
        carBayern.setDrivers(driverService.getAll());
        CarService carService =
                (CarService) injector.getInstance(CarService.class);
        carService.create(carTesla);
        carService.create(carToyota);
        carService.create(carBayern);
        carService.delete(3L);
        Car carTemp = new Car();
        carTemp = carService.get(1L);
        carTemp.setManufacturer(manufacturerService.get(1L));
        carTemp.setDrivers(driverService.getAll());
        carService.create(carTemp);
        carService.removeDriverFromCar(driverService.get(2L), carTemp);
        carService.update(carTemp);
    }
}
