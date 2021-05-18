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
    private static final Injector injector = Injector.getInstance("mate.jdbc");
    private static final ManufacturerService manufacturerService =
            (ManufacturerService) injector.getInstance(ManufacturerService.class);
    private static final DriverService driverService =
            (DriverService) injector.getInstance(DriverService.class);
    private static final CarService carService =
            (CarService) injector.getInstance(CarService.class);

    public static void main(String[] args) {
        Manufacturer manufacturerBmw = new Manufacturer("BMW","Germany");
        manufacturerService.create(manufacturerBmw);

        Driver driverSemen = new Driver("Semen", "1234");
        Driver driverOleg = new Driver("Oleg", "98080");
        Driver driverStanislav = new Driver("Stanislav", "39412");
        driverService.create(driverOleg);
        driverService.create(driverSemen);
        driverService.create(driverStanislav);
        List<Driver> driverList = new ArrayList<>();
        driverList.add(driverOleg);
        driverList.add(driverSemen);

        Car car = new Car();
        car.setManufacturer(manufacturerBmw);
        car.setModel("Bumer");
        car.setDrivers(driverList);

        carService.create(car);
        carService.get(car.getId());
        carService.getAll();
        carService.removeDriverFromCar(driverSemen,car);
        carService.addDriverToCar(driverStanislav,car);
        carService.getAllByDriver(driverStanislav.getId());
        carService.delete(car.getId());
        System.out.println(carService.getAll());
    }
}
