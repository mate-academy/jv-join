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
    private static final DriverService driverService
            = (DriverService) injector.getInstance(DriverService.class);
    private static final ManufacturerService manufacturerService
            = (ManufacturerService) injector.getInstance(ManufacturerService.class);
    private static final CarService carService
            = (CarService) injector.getInstance(CarService.class);

    public static void main(String[] args) {
        final Driver driverKobe = new Driver("Koby", "24242424224");
        final Driver driverWade = new Driver("Wade", "33333333333");
        final Driver driverJenna = new Driver("Jenna", "1515151515");

        final Manufacturer mercedes = new Manufacturer("Mercedes", "Germany");
        final Manufacturer bmw = new Manufacturer("Bmw", "Germany");
        final Manufacturer audi = new Manufacturer("Audi", "Germany");
        driverService.create(driverKobe);
        driverService.create(driverWade);
        driverService.create(driverJenna);
        manufacturerService.create(mercedes);
        manufacturerService.create(bmw);
        manufacturerService.create(audi);

        Car mercedesS = new Car("S", mercedes, List.of(driverKobe,driverJenna));
        Car bmwX5 = new Car("X5", bmw, List.of(driverWade, driverJenna));
        Car audiR8 = new Car("R8", audi, List.of(driverKobe));
        carService.create(mercedesS);
        carService.create(bmwX5);
        carService.create(audiR8);
        System.out.println(carService.getAll());

        final Car carAudi = carService.get(3L);
        final Car carMercedes = carService.get(1L);
        carMercedes.setModel("GLE");
        carService.update(carMercedes);
        carService.removeDriverFromCar(driverService.get(3L), carMercedes);
        carService.addDriverToCar(driverService.get(3L), carAudi);
        System.out.println(carService.getAll());

        List<Car> cars = carService.getAllByDriver(1L);
        for (Car car : cars) {
            System.out.println(car);
        }
        carService.delete(carAudi.getId());
        System.out.println(carService.getAll());
    }
}
