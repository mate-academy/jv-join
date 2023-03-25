package mate.jdbc;

import java.util.List;
import mate.jdbc.lib.Injector;
import mate.jdbc.model.Car;
import mate.jdbc.model.Driver;
import mate.jdbc.model.Manufacturer;
import mate.jdbc.service.CarService;
import mate.jdbc.service.DriverService;
import mate.jdbc.service.ManufacturerService;
import mate.jdbc.service.impl.CarServiceImpl;
import mate.jdbc.service.impl.DriverServiceImpl;
import mate.jdbc.service.impl.ManufacturerServiceImpl;

public class Main {
    private static Injector injector = Injector.getInstance("mate.jdbc");

    public static void main(String[] args) {
        DriverServiceImpl driverService = (DriverServiceImpl)
                injector.getInstance(DriverService.class);
        ManufacturerServiceImpl manufacturerService = (ManufacturerServiceImpl)
                injector.getInstance(ManufacturerService.class);
        Driver firstDriver = driverService.create(new Driver("Pavlo", "1235789"));
        Driver secondDriver = driverService.create(new Driver("Ivan", "78675643"));
        Driver thirdDriver = driverService.create(new Driver("Slava", "75643976"));
        List<Driver> drivers = List.of(driverService.get(firstDriver.getId()),
                driverService.get(secondDriver.getId()), driverService.get(thirdDriver.getId()));
        Manufacturer manufacturer = manufacturerService.create(
                new Manufacturer("SKODA", "Czech Republic"));
        Car newCar = new Car("m5", manufacturerService.get(manufacturer.getId()), drivers);
        CarServiceImpl carService = (CarServiceImpl)
                injector.getInstance(CarService.class);

        // create new Car
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
        newAll.stream().forEach(System.out::println);

        // add new driver to car
        Car car = carService.create(newCar);
        Car secondCar = carService.get(car.getId());
        Driver driver = new Driver("Petro", "234567");
        Driver fourDriver = driverService.create(driver);
        carService.addDriverToCar(fourDriver, secondCar);
        System.out.println(System.lineSeparator() + carService.get(car.getId()));

        //delete new driver form car
        carService.removeDriverFromCar(fourDriver, secondCar);
        System.out.println(carService.get(car.getId()));

        //get all cars by driver
        System.out.println(System.lineSeparator());
        List<Car> allCars = carService.getAllByDriver(firstDriver.getId());
        allCars.stream().forEach(System.out::println);

    }
}
