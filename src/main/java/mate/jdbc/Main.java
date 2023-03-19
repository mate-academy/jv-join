package mate.jdbc;

import java.util.List;
import mate.jdbc.lib.Injector;
import mate.jdbc.model.Car;
import mate.jdbc.model.Driver;
import mate.jdbc.service.CarService;
import mate.jdbc.service.CarServiceImpl;
import mate.jdbc.service.DriverService;
import mate.jdbc.service.DriverServiceImpl;
import mate.jdbc.service.ManufacturerService;
import mate.jdbc.service.ManufacturerServiceImpl;

public class Main {
    private static Injector injector = Injector.getInstance("mate.jdbc");

    public static void main(String[] args) {
        DriverServiceImpl driverService = (DriverServiceImpl)
                injector.getInstance(DriverService.class);
        ManufacturerServiceImpl manufacturerService = (ManufacturerServiceImpl)
                injector.getInstance(ManufacturerService.class);
        CarServiceImpl carService = (CarServiceImpl)
                injector.getInstance(CarService.class);
        // create new Car
        List<Driver> drivers = List.of(driverService.get(1L),
                driverService.get(2L), driverService.get(3L));
        Car newCar = new Car("m5", manufacturerService.get(1L), drivers);
        System.out.println(carService.create(newCar));

        // update car
        newCar.setModel("x6");
        System.out.println(System.lineSeparator() + carService.update(newCar));

        // get car
        System.out.println(System.lineSeparator() + carService.get(newCar.getId())
                + System.lineSeparator());

        // delete car
        carService.delete(newCar.getId());

        // get all car
        List<Car> newAll = carService.getAll();
        for (Car car: newAll) {
            System.out.println(car);
        }

        // add new driver to car
        Car car = carService.get(6L);
        Driver newdriver = driverService.get(7L);
        carService.addDriverToCar(newdriver, car);
        System.out.println(System.lineSeparator() + carService.get(car.getId()));

        //delete new driver form car
        carService.removeDriverFromCar(newdriver, car);
        System.out.println(carService.get(car.getId()));

        //get all cars by driver
        System.out.println(System.lineSeparator());
        List<Car> allCars = carService.getAllByDriver(newdriver.getId());
        for (Car cars: allCars) {
            System.out.println(cars);
        }
    }
}
