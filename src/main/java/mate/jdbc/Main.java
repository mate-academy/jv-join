package mate.jdbc;

import mate.jdbc.lib.Injector;
import mate.jdbc.model.Car;
import mate.jdbc.model.Driver;
import mate.jdbc.model.Manufacturer;
import mate.jdbc.service.CarService;
import mate.jdbc.service.DriverService;
import mate.jdbc.service.ManufacturerService;
import mate.jdbc.service.impl.CarServiceImpl;

import java.util.List;

public class Main {

    private static final Injector injector = Injector.getInstance("mate.jdbc");
    private static final DriverService driverService
            = (DriverService) injector.getInstance(DriverService.class);
    private static final ManufacturerService manufacturerService
            = (ManufacturerService) injector.getInstance(ManufacturerService.class);
    private static final CarService carService
            = (CarService) injector.getInstance(CarService.class);
    public static void main(String[] args) {
        Driver driverKobe = new Driver("Koby", "24242424224");
        Driver driverWade = new Driver("Wade", "33333333333");
        Driver driverJenna = new Driver("Jenna", "1515151515");
        Driver driverJenifer = new Driver("Jenifer", "2323232323");
        Manufacturer mercedes = new Manufacturer("Mercedes", "Germany");
        Manufacturer bmw = new Manufacturer("Bmw", "Germany");
        Manufacturer audi = new Manufacturer("Audi", "Germany");
        driverService.create(driverKobe);
        driverService.create(driverJenna);
        driverService.create(driverWade);
        driverService.create(driverJenifer);
        manufacturerService.create(mercedes);
        manufacturerService.create(bmw);
        manufacturerService.create(audi);

        Car mercedesS = new Car("S", mercedes, List.of(driverKobe,driverJenna));
        Car bmwX5 = new Car("X5", bmw, List.of(driverWade, driverJenifer));
        Car audiR8 = new Car("R8", audi, List.of(driverKobe));
        carService.create(mercedesS);
        carService.create(bmwX5);
        carService.create(audiR8);
        System.out.println(carService.getAll());

        Car carAudi = carService.get(3L);
        Car carBmw = carService.get(2L);
        carBmw.setModel("X6");
        carService.update(carBmw);
        carService.removeDriverFromCar(driverService.get(4L), carBmw);
        carService.addDriverToCar(driverService.get(4L), carAudi);
        System.out.println(carService.getAll());

        List<Car> cars = carService.getAllByDriver(1L);
        for (Car car : cars) {
            System.out.println(car);
        }
        carService.delete(carAudi.getId());
        System.out.println(carService.getAll());
    }
}
