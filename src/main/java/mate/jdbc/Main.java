package mate.jdbc;

import java.util.ArrayList;
import java.util.List;
import mate.jdbc.dao.DriverDaoImpl;
import mate.jdbc.dao.ManufacturerDaoImpl;
import mate.jdbc.lib.Injector;
import mate.jdbc.model.Car;
import mate.jdbc.model.Driver;
import mate.jdbc.service.CarService;

public class Main {
    public static void main(String[] args) {
        List<Driver> drivers = new ArrayList<>();
        drivers.add(new DriverDaoImpl().get(2L).get());
        drivers.add(new DriverDaoImpl().get(3L).get());

        Car car = new Car();
        car.setId(5L);
        car.setModel("FORD");
        car.setManufacturer(new ManufacturerDaoImpl().get(1L).get());
        car.setDrivers(drivers);

        Injector injector = Injector.getInstance("mate.jdbc");
        CarService carService = (CarService) injector.getInstance(CarService.class);

        //carService.create(car);
        //carService.get(1l);
        //carService.getAll();
        //carService.getAllByDriver(2L);
        //carService.update(car);
        //carService.delete(4L);
        //carService.addDriverToCar(new DriverDaoImpl().get(2L)
        // .get(), carService.get(5L).get());
        //carService.removeDriverFromCar(new DriverDaoImpl().get(2L)
        // .get(), carService.get(5L).get());
        //carService.get(5L);
    }
}
