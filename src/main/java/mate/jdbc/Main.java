package mate.jdbc;

import java.util.List;
import mate.jdbc.lib.Injector;
import mate.jdbc.model.Car;
import mate.jdbc.model.Driver;
import mate.jdbc.model.Manufacturer;
import mate.jdbc.service.CarService;
import mate.jdbc.service.DriverService;
import mate.jdbc.service.ManufacturerService;

public class Main {
    private static Injector injector = Injector.getInstance("mate.jdbc");

    public static void main(String[] args) {
        ManufacturerService manufacturerService = (ManufacturerService) injector.getInstance(ManufacturerService.class);
        DriverService driverService = (DriverService) injector.getInstance(DriverService.class);
        CarService carService = (CarService) injector.getInstance(CarService.class);
        Driver driver = new Driver( "Petro", "122334");
        Driver secondDriver = new Driver( "Ivan", "19987");
        driverService.create(driver);
        driverService.create(secondDriver);
        Manufacturer manufacturer = new Manufacturer("Wolksvagen", "Germany");
        manufacturerService.create(manufacturer);
        Car car = new Car("transporter", manufacturer, List.of(driver,secondDriver));
        carService.create(car);
        System.out.println(carService.get(1L).toString());
        System.out.println("Car was created, method get was tested " + System.lineSeparator());
        carService.removeDriverFromCar(driver, car);
        System.out.println(car.toString());
        System.out.println("Driver Petro was remover from car" + System.lineSeparator());
        carService.addDriverToCar(driver, car);
        List<Car> allCars = carService.getAll();
        allCars.forEach(System.out::println);
        System.out.println("Driver was added to car, method getAll was tested" + System.lineSeparator());
        allCars = carService.getAllByDriver(driver.getId());
        allCars.forEach(System.out::println);
        System.out.println("All car by driver" + System.lineSeparator());
        carService.delete(car.getId());
        System.out.println("Car was deleted" + System.lineSeparator());
    }
}
