package mate.jdbc;

import java.util.ArrayList;
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
    private static CarService carService = (CarService) injector.getInstance(CarService.class);
    private static ManufacturerService manufacturerService
            = (ManufacturerService) injector.getInstance(ManufacturerService.class);
    private static DriverService driverService
            = (DriverService) injector.getInstance(DriverService.class);

    public static void main(String[] args) {
        List<Driver> drivers = new ArrayList<>();
        drivers.add(new Driver(1L, "Valeriy", "148823"));
        drivers.add(new Driver(3L, "Artem", "5374563457"));
        Driver driver = new Driver(2L, "Danylo", "6452646");
        for (Driver driver1 : drivers) {
            driverService.create(driver1);
        }
        Manufacturer manufacturer = new Manufacturer(1L,"Toyota", "Japan");
        Manufacturer manufacturer1 = new Manufacturer(2L, "BMW", "Germany");
        manufacturerService.create(manufacturer);
        manufacturerService.create(manufacturer1);
        Car car1 = new Car(1L, "Camry", manufacturer, drivers);
        Car car2 = new Car(2L, "M5", manufacturer1, drivers);
        carService.create(car1);
        carService.create(car2);
        car1.setModel("X5");
        carService.update(car1);
        for (Car car : carService.getAll()) {
            System.out.println(car);
        }
    }
}
