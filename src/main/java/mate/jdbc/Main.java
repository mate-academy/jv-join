package mate.jdbc;

import java.util.ArrayList;
import java.util.List;
import mate.jdbc.dao.DriverDao;
import mate.jdbc.dao.ManufacturerDao;
import mate.jdbc.lib.Injector;
import mate.jdbc.model.Car;
import mate.jdbc.model.Driver;
import mate.jdbc.model.Manufacturer;
import mate.jdbc.service.CarService;

public class Main {
    private static final Injector injector = Injector.getInstance("mate.jdbc");

    public static void main(String[] args) {
        Manufacturer manufacturer = new Manufacturer();
        manufacturer.setName("test");
        manufacturer.setCountry("Ukraine");
        ManufacturerDao manufacturerDao = (ManufacturerDao)
                injector.getInstance(ManufacturerDao.class);
        manufacturerDao.create(manufacturer);

        Driver alice = new Driver();
        alice.setName("Alice");
        alice.setLicenseNumber("001");
        Driver bob = new Driver();
        bob.setName("Bob");
        bob.setLicenseNumber("002");
        DriverDao driverDao = (DriverDao) injector.getInstance(DriverDao.class);
        driverDao.create(alice);
        driverDao.create(bob);

        List<Driver> firstCarDrivers = new ArrayList<>();
        firstCarDrivers.add(alice);
        firstCarDrivers.add(bob);
        Car firstCar = new Car("Toyota", manufacturer, firstCarDrivers);
        CarService carService = (CarService) injector.getInstance(CarService.class);
        System.out.println(carService.create(firstCar) + System.lineSeparator());

        System.out.println(carService.get(firstCar.getId()) + System.lineSeparator());

        List<Driver> secondCarDrivers = new ArrayList<>();
        secondCarDrivers.add(bob);
        Car secondCar = new Car("Ford", manufacturer, secondCarDrivers);
        carService.create(secondCar);
        carService.getAll().forEach(System.out::println);
        System.out.println();

        secondCarDrivers.remove(bob);
        secondCarDrivers.add(alice);
        secondCar.setDrivers(secondCarDrivers);
        System.out.println(carService.update(secondCar) + System.lineSeparator());

        System.out.println(carService.delete(firstCar.getId()));
        carService.getAll().forEach(System.out::println);
        System.out.println();

        carService.addDriverToCar(bob, secondCar);
        System.out.println(secondCar);
        carService.removeDriverFromCar(alice, secondCar);
        System.out.println(secondCar + System.lineSeparator());

        List<Driver> thirdCarDrivers = List.of(bob, alice);
        Car thirdCar = new Car("Lamboghini", manufacturer, thirdCarDrivers);
        carService.create(thirdCar);
        carService.getAllByDriver(bob.getId()).forEach(System.out::println);
    }
}
