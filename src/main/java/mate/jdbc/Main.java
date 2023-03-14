package mate.jdbc;

import java.util.List;
import mate.jdbc.dao.CarDaoImpl;
import mate.jdbc.dao.DriverDaoImpl;
import mate.jdbc.dao.ManufacturerDaoImpl;
import mate.jdbc.model.Car;
import mate.jdbc.model.Driver;
import mate.jdbc.service.CarServic;
import mate.jdbc.service.CarServiceImpl;
import mate.jdbc.service.DriverService;
import mate.jdbc.service.DriverServiceImpl;
import mate.jdbc.service.ManufacturerService;
import mate.jdbc.service.ManufacturerServiceImpl;

public class Main {
    public static void main(String[] args) {
        CarServic carServic = new CarServiceImpl(new CarDaoImpl(), new DriverDaoImpl());
        Driver driver = new Driver("Jon", "332");
        Car car = new Car();
        carServic.create(car);
        carServic.addDriverToCar(driver, carServic.get(1L));
        carServic.getAll().forEach(System.out::println);
        car.setModel("Brabus");
        ManufacturerService manufacturerService =
                new ManufacturerServiceImpl(new ManufacturerDaoImpl());
        car.setManufacturer(manufacturerService.get(1L));
        DriverService driverService = new DriverServiceImpl(new DriverDaoImpl());
        List<Driver> driverList = List.of(driverService.get(1L), driverService.get(2L));
        car.setDriverList(driverList);
    }
}
