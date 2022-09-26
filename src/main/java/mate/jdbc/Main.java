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
    private static final Injector injector = Injector.getInstance("mate.jdbc");

    public static void main(String[] args) {
        CarService carService =
                (CarService) injector.getInstance(CarService.class);
        DriverService driverService = (DriverService) injector.getInstance(DriverService.class);
        ManufacturerService manufacturerService =
                (ManufacturerService) injector.getInstance(ManufacturerService.class);
        Manufacturer renault =
                manufacturerService.create(Manufacturer.of("Renault", "France"));
        Driver newDriver1 = driverService.create(Driver.of("Jason Statham", "OPU987098"));
        Driver newDriver2 = driverService.create(Driver.of("Sargis Hakobyan", "JHG765543"));
        Driver existingDriver = driverService.get(8L);
        List<Driver> driversForCreate = List.of(newDriver1, newDriver2, existingDriver);
        System.out.println(carService.create(Car.of("Logan", renault, driversForCreate)));
        System.out.println(carService.get(3L));
        System.out.println(carService.delete(14L));
        carService.getAll().forEach(System.out::println);
        carService.getAllByDriver(3L).forEach(System.out::println);
        Manufacturer peugeot =
                manufacturerService.create(Manufacturer.of("Peugeot", "France"));
        Driver olhaTarasenko = driverService.create(Driver.of("Olha Tarasenko", "PPU983498"));
        Driver arikNasibian = driverService.create(Driver.of("Arik Nasibian", "JLK765123"));
        Driver existingDriver2 = driverService.get(5L);
        List<Driver> driversForUpdate = List.of(olhaTarasenko, arikNasibian, existingDriver2);
        System.out.println(carService.update(Car.of(4L,"408", peugeot, driversForUpdate)));
        carService.addDriverToCar(driverService.get(10L), carService.get(3L));
        carService.removeDriverFromCar(driverService.get(10L), carService.get(3L));
    }
}
