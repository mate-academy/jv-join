package mate.jdbc;

/*
import java.util.ArrayList;
import java.util.List;
import mate.jdbc.lib.Injector;
import mate.jdbc.model.Car;
import mate.jdbc.model.Driver;
import mate.jdbc.model.Manufacturer;
import mate.jdbc.service.CarService;
import mate.jdbc.service.DriverService;
import mate.jdbc.service.ManufacturerService;
*/

public class Main {
    public static void main(String[] args) {
        // test your code here
        /*
        Injector injector = Injector.getInstance("mate.jdbc");
        ManufacturerService manufacturerService = (ManufacturerService) injector
                .getInstance(ManufacturerService.class);
        DriverService driverService = (DriverService) injector.getInstance(DriverService.class);
        CarService carService = (CarService) injector.getInstance(CarService.class);
        List<Driver> drivers = new ArrayList<>();
        drivers.add(driverService.get(2L));
        Manufacturer manufacturer = manufacturerService.get(1L);
        Car car = new Car("Outback", manufacturer);
        car.setId(1L);
        car.setDrivers(drivers);
        carService.update(car);
*/
        //Manufacturer subaru = new Manufacturer("Subaru", "Japan");
        //subaru = manufacturerService.create(subaru);
        //Car car = new Car("Forester", subaru);
        //System.out.println(carService.create(car));
        //System.out.println(carService.get(1L));
        //System.out.println(carService.getAll());
    }
}
