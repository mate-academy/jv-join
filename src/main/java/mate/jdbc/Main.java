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
        ManufacturerService manufacturerService
                = (ManufacturerService) injector.getInstance(ManufacturerService.class);
        Manufacturer manufacturerToyota = new Manufacturer("Toyota", "Japan");
        Manufacturer manufacturerMercedes = new Manufacturer("Mercedes", "Germany");
        manufacturerService.create(manufacturerToyota);
        manufacturerService.create(manufacturerMercedes);

        Driver driverIra = new Driver("Ira", "1234678767778");
        Driver driverVova = new Driver("Vova", "576543298790");
        Driver driverOstap = new Driver("Ostap", "46789789876");
        Driver driverLilia = new Driver("Lilia", "1223456987");

        DriverService driverService
                = (DriverService) injector.getInstance(DriverService.class);
        driverService.create(driverIra);
        driverService.create(driverVova);
        driverService.create(driverOstap);
        driverService.create(driverLilia);

        List<Driver> driverListToyota = new ArrayList<>();
        List<Driver> driverListMercedes = new ArrayList<>();

        driverListToyota.add(driverIra);
        driverListToyota.add(driverOstap);
        driverListMercedes.add(driverVova);
        driverListMercedes.add(driverLilia);

        CarService carService = (CarService) injector.getInstance(CarService.class);
        Car carModelToyota = new Car("Camry", manufacturerToyota, driverListToyota);
        Car carModelMercedes = new Car("GLE", manufacturerMercedes, driverListMercedes);
        carService.create(carModelToyota);
        carService.create(carModelMercedes);

        carService.getAllByDriver(carModelToyota.getId());
        carModelToyota.setManufacturer(manufacturerService.get(manufacturerToyota.getId()));
        carService.update(carModelToyota);
        carService.getAll().forEach(System.out::println);
        carService.removeDriverFromCar(driverOstap,carModelToyota);
        carService.getAllByDriver(driverIra.getId()).forEach(System.out::println);

        carService.delete(carModelMercedes.getId());

    }
}
