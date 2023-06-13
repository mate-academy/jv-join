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
    private static final Injector injector = Injector.getInstance("mate.jdbc");
    private static final CarService carService
            = (CarService) injector.getInstance(CarService.class);
    private static final ManufacturerService manufacturerService
            = (ManufacturerService) injector.getInstance(ManufacturerService.class);
    private static final DriverService driverService
            = (DriverService) injector.getInstance(DriverService.class);

    public static void main(String[] args) {
        Manufacturer manufacturer = new Manufacturer("Toyota", "Japan");
        manufacturerService.create(manufacturer);

        Driver driverOne = new Driver("Hannibal", "216");
        Driver driverTwo = new Driver("Vercingetorix", "52");
        driverService.create(driverOne);
        driverService.create(driverTwo);

        System.out.println(driverService.getAll());

        Car car = new Car("Prius", manufacturer, List.of(driverOne, driverTwo));
        carService.create(car);

        carService.get(car.getId());
        carService.addDriverToCar(driverOne,car);
        carService.addDriverToCar(driverTwo,car);

        carService.removeDriverFromCar(driverTwo, car);
        carService.addDriverToCar(driverTwo,car);
        carService.getAll();

        carService.getAllByDriver(driverOne.getId());
        carService.delete(car.getId());
        carService.getAll();
    }
}
