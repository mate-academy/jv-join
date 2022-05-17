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

        Manufacturer manufacturerKia = new Manufacturer("Kia", "Korea");
        Manufacturer manufacturerVolkswagen = new Manufacturer("Volkswagen", "Germany");
        manufacturerService.create(manufacturerKia);
        manufacturerService.create(manufacturerVolkswagen);

        Driver driverTaras = new Driver("Taras", "111111");
        Driver driverDenis = new Driver("Denis", "22222");
        Driver driverKseniia = new Driver("Ksenia", "33333");
        Driver driverDima = new Driver("Dima", "55555555");

        DriverService driverService
                = (DriverService) injector.getInstance(DriverService.class);
        driverService.create(driverTaras);
        driverService.create(driverDenis);
        driverService.create(driverKseniia);
        driverService.create(driverDima);

        List<Driver> driversKia = new ArrayList<>();
        driversKia.add(driverTaras);
        driversKia.add(driverDenis);
        Car carForteS = new Car("Forte S", manufacturerKia, driversKia);

        List<Driver> driversVolkswagen = new ArrayList<>();
        driversVolkswagen.add(driverKseniia);
        driversVolkswagen.add(driverDima);
        Car carVolkswagenJetta = new Car("Jetta", manufacturerVolkswagen, driversVolkswagen);

        CarService carService
                = (CarService) injector.getInstance(CarService.class);
        carService.create(carForteS);
        carService.create(carVolkswagenJetta);

        System.out.println(carService.getAllByDriver(driverTaras.getId()));
        carService.addDriverToCar(driverTaras, carVolkswagenJetta);
        carService.getAll().forEach(System.out::println);
        driverService.delete(driverDenis.getId());
        carService.removeDriverFromCar(driverDenis, carForteS);
        System.out.println(carService.getAllByDriver(driverDenis.getId()));
        System.out.println(carService.get(carForteS.getId()));
        carService.delete(carVolkswagenJetta.getId());
    }
}
