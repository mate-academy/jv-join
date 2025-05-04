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

    public static void main(String[] args) {
        CarService carService = (CarService) injector.getInstance(CarService.class);
        ManufacturerService manufacturerService =
                (ManufacturerService) injector.getInstance(ManufacturerService.class);
        DriverService driverService = (DriverService) injector.getInstance(DriverService.class);

        Manufacturer manufacturer = new Manufacturer(null, "Audi", "Germany");
        Manufacturer audi = manufacturerService.create(manufacturer);
        Driver driver = new Driver(null, "Ivan", "1234");
        Driver ivanDriver = driverService.create(driver);
        Car audiCar = carService.create(new Car(null, "Q7", audi, List.of(ivanDriver)));
        Long idAudiCar = audiCar.getId();

        System.out.println(carService.get(idAudiCar));
        System.out.println(carService.getAll());

        System.out.println(carService.delete(idAudiCar));
        System.out.println(carService.getAll());

        System.out.println(carService.getAllByDriver(ivanDriver.getId()));

        Driver driver1 = new Driver(null, "Oleh", "4321");
        Driver olehDriver = driverService.create(driver1);
        carService.update(new Car(idAudiCar, "R8", audi,
                List.of(olehDriver)));

        carService.addDriverToCar(ivanDriver, audiCar);
        carService.removeDriverFromCar(ivanDriver, audiCar);
    }
}
