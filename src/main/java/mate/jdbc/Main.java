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
        ManufacturerService manufacturerService =
                (ManufacturerService) injector.getInstance(ManufacturerService.class);
        Manufacturer maserati = new Manufacturer("Maserati", "Italy");
        manufacturerService.create(maserati);

        Car maseratiLevante = new Car("Maserati Levante", maserati, new ArrayList<>());
        Car maseratiGhibli = new Car("Maserati Ghibli", maserati, new ArrayList<>());
        CarService carService = (CarService) injector.getInstance(CarService.class);
        carService.create(maseratiLevante);
        carService.create(maseratiGhibli);

        Driver antonio = new Driver("Antonio", "1q2w3e");
        Driver maria = new Driver("Maria", "o9i8u7");
        Driver cusya = new Driver("Cusya", "4r5t6y");
        Driver paul = new Driver("Paul", "6y7u8i");
        DriverService driverService = (DriverService) injector.getInstance(DriverService.class);
        driverService.create(maria);
        driverService.create(antonio);
        driverService.create(cusya);
        driverService.create(paul);

        carService.addDriverToCar(maria, maseratiGhibli);
        carService.addDriverToCar(antonio, maseratiGhibli);
        carService.addDriverToCar(cusya, maseratiGhibli);
        carService.addDriverToCar(paul, maseratiGhibli);
        carService.addDriverToCar(antonio, maseratiLevante);
        carService.addDriverToCar(paul, maseratiLevante);

        carService.getCarsByDriverId(antonio.getId()).forEach(System.out::println);
        carService.getCarsByDriverId(maria.getId()).forEach(System.out::println);
        carService.removeDriverFromCar(antonio, maseratiGhibli);
        carService.getCarsByDriverId(antonio.getId()).forEach(System.out::println);
    }
}
