package mate.jdbc;

import java.util.ArrayList;
import java.util.List;
import mate.jdbc.dao.CarDao;
import mate.jdbc.lib.Injector;
import mate.jdbc.model.Car;
import mate.jdbc.model.Driver;
import mate.jdbc.model.Manufacturer;
import mate.jdbc.service.CarService;
import mate.jdbc.service.DriverService;
import mate.jdbc.service.ManufacturerService;

public class Main {
    public static void main(String[] args) {
        // test your code here
        Injector injector = Injector.getInstance("mate.jdbc");
        CarDao carDao = (CarDao) injector.getInstance(CarDao.class);
        final CarService carService = (CarService) injector.getInstance(CarService.class);
        ManufacturerService manService =
                (ManufacturerService) injector.getInstance(ManufacturerService.class);
        DriverService driverService = (DriverService) injector.getInstance(DriverService.class);
        Manufacturer manufacturer = new Manufacturer("BMW", "Germany");
        Manufacturer manufacturer2 = new Manufacturer("Audi", "Germany");
        Manufacturer manToInsert = manService.create(manufacturer);
        Manufacturer manToInsert2 = manService.create(manufacturer2);
        Car bmw = new Car("M3", manToInsert);
        final Car audi = new Car("S4", manToInsert2);
        Driver driver = new Driver("Dupinder", "123456");
        Driver driver2 = new Driver("Carolina", "12345344");
        Driver driverToInsert = driverService.create(driver);
        Driver driverToInsert2 = driverService.create(driver2);
        List<Driver> drivers = new ArrayList<>();
        drivers.add(driverToInsert);
        drivers.add(driverToInsert2);
        bmw.setDrivers(drivers);
        audi.setDrivers(drivers);
        carService.create(bmw);
        Car returnedCar = carService.get(3L);
        System.out.println(returnedCar);
        System.out.println(carService.getAll());
        audi.setId(2L);
        carService.update(audi);
        carService.addDriverToCar(driverToInsert2, audi);
        carService.removeDriverFromCar(driverToInsert, audi);
        System.out.println(carService.getAllByDriver(driverToInsert2.getId()));
        carService.delete(3L);
    }
}
