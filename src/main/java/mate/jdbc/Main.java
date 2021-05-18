package mate.jdbc;

import mate.jdbc.dao.DriverDao;
import mate.jdbc.dao.ManufacturerDao;
import mate.jdbc.lib.Injector;
import mate.jdbc.model.Car;
import mate.jdbc.model.Driver;
import mate.jdbc.model.Manufacturer;
import mate.jdbc.service.CarService;

import java.util.ArrayList;
import java.util.List;

public class Main {
    private final static Injector injector = Injector.getInstance("mate.jdbc");

    public static void main(String[] args) {
        ManufacturerDao manufacturerDao = (ManufacturerDao) injector
                .getInstance(ManufacturerDao.class);
        Manufacturer manufacturer = new Manufacturer("BMW", "Germany");
        Car car = new Car("coolBMW", manufacturerDao.create(manufacturer));

        DriverDao driverDao = (DriverDao) injector.getInstance(DriverDao.class);
        List<Driver> drivers = new ArrayList<>();
        Driver firstDriver = new Driver("Johnathan", "1234321442");
        Driver secondDriver = new Driver("Alice", "32958325");
        drivers.add(driverDao.create(firstDriver));
        drivers.add(driverDao.create(secondDriver));
        car.setDrivers(drivers);

        CarService carService = (CarService) injector.getInstance(CarService.class);
        Car createdCar = carService.create(car);
        System.out.println(createdCar);

        System.out.println(carService.get(createdCar.getId()));

        System.out.println(carService.delete(createdCar.getId()));

        System.out.println(carService.getAll());

        Manufacturer updatedManufacturer = new Manufacturer("Audi", "Germany");
        manufacturerDao.create(updatedManufacturer);
        Car updatedCar = new Car("notCoolAudi", updatedManufacturer);
        updatedCar.setId(car.getId());
        System.out.println(carService.update(updatedCar));

        System.out.println(carService.getAllByDriver(firstDriver.getId()));
    }
}
