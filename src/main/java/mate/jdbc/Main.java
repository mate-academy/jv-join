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
        Manufacturer firstManufacturer = new Manufacturer("Tesla", "USA");
        Manufacturer secondManufacturer = new Manufacturer("Opel", "Germany");
        manufacturerService.create(firstManufacturer);
        manufacturerService.create(secondManufacturer);

        Driver driverMicha = new Driver("Mihalich", "88005553535");
        Driver driverBob = new Driver("Bob", "133712353424");
        Driver driverTaras = new Driver("Taras", "2345234534");
        Driver driverYarik = new Driver("Yarik", "6345634565");

        DriverService driverService = (DriverService) injector.getInstance(DriverService.class);
        driverService.create(driverMicha);
        driverService.create(driverBob);
        driverService.create(driverTaras);
        driverService.create(driverYarik);

        List<Driver> firstDriverList = new ArrayList<>();
        List<Driver> secondDriverList = new ArrayList<>();

        firstDriverList.add(driverMicha);
        firstDriverList.add(driverTaras);
        secondDriverList.add(driverBob);
        secondDriverList.add(driverYarik);

        CarService carService = (CarService) injector.getInstance(CarService.class);
        Car firstCarModel = new Car("Model 3", firstManufacturer, firstDriverList);
        Car secondCarModel = new Car("Corsa", secondManufacturer, secondDriverList);
        carService.create(firstCarModel);
        carService.create(secondCarModel);

        carService.getAll().forEach(System.out::println);
        firstCarModel.setModel("Corsa 5D");
        carService.removeDriverFromCar(driverTaras,firstCarModel);
        firstCarModel.setManufacturer(manufacturerService.get(firstManufacturer.getId()));
        carService.update(firstCarModel);
        carService.delete(secondCarModel.getId());
        carService.getAllByDriver(driverMicha.getId()).forEach(System.out::println);
        carService.getAllByDriver(firstCarModel.getId());
    }
}
