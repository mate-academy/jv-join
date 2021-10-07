package mate.jdbc;

import mate.jdbc.dao.CarDao;
import mate.jdbc.dao.CarDaoImpl;
import mate.jdbc.lib.Injector;
import mate.jdbc.model.Car;
import mate.jdbc.model.Driver;
import mate.jdbc.model.Manufacturer;
import mate.jdbc.service.DriverService;
import mate.jdbc.service.ManufacturerService;

import java.util.List;
import java.util.Optional;

public class Main {
    private static final Injector injector = Injector.getInstance("mate.jdbc");

    public static void main(String[] args) {
        Driver driver = new Driver("nameCreated", "licenseCreated");
        DriverService driverService = (DriverService) injector.getInstance(DriverService.class);
        ManufacturerService manufacturerService = (ManufacturerService) injector.getInstance(ManufacturerService.class);
        CarDao carDao = new CarDaoImpl();
        List<Driver> drivers = driverService.getAll();
        Manufacturer manufacturer = manufacturerService.get(14L);
        Optional<Car> car;
        car = carDao.get(3L);
        Car recCar = car.orElseThrow();
        recCar.setModel("UpdatedModel");
        System.out.println(carDao.delete(recCar.getId()));
    }
}
