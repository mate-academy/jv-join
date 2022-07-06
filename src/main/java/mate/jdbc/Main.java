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
    private static final Injector injector =
            Injector.getInstance("mate.jdbc");
    private static final CarService carService =
            (CarService) injector.getInstance(CarService.class);
    private static final ManufacturerService manufacturerService =
            (ManufacturerService) injector.getInstance(ManufacturerService.class);
    private static final DriverService driverService =
            (DriverService) injector.getInstance(DriverService.class);

    public static void main(String[] args) {
        Manufacturer manufacturerDaewoo = new Manufacturer("Daewoo", "Ukraine");
        Manufacturer manufacturerSkoda = new Manufacturer("Skoda", "Chez Republic");
        Manufacturer manufacturerMazda = new Manufacturer("Mazda", "Japan");

        manufacturerService.create(manufacturerDaewoo);
        manufacturerService.create(manufacturerSkoda);
        manufacturerService.create(manufacturerMazda);

        Driver driverPetya = new Driver("Petya", "AX0001AX");
        Driver driverIvan = new Driver("Ivan", "AH0002AH");
        Driver driverMariya = new Driver("Mariya", "AA0003AA");
        driverService.create(driverPetya);
        driverService.create(driverIvan);
        driverService.create(driverMariya);

        List<Driver> driverList = new ArrayList<>();
        driverList.add(driverPetya);
        driverList.add(driverIvan);
        driverList.add(driverMariya);

        Car carModelOctavia = new Car("Octavia", manufacturerSkoda, driverList);
        carService.create(carModelOctavia);

        carService.getAllByDriver(carModelOctavia.getId());
        carModelOctavia.setManufacturer(manufacturerService.get(manufacturerMazda.getId()));
        carService.update(carModelOctavia);
        carService.getAll().forEach(System.out::println);
        carService.removeDriverFromCar(driverIvan, carModelOctavia);
        carService.getAllByDriver(driverIvan.getId()).forEach(System.out::println);
    }
}
