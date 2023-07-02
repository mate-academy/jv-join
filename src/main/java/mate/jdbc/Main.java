package mate.jdbc;

import java.util.ArrayList;
import java.util.Arrays;
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
        ManufacturerService manufacturerService =
                (ManufacturerService) injector.getInstance(ManufacturerService.class);
        Manufacturer manufacturerToyota = new Manufacturer(null, "Toyota", "Japan");
        manufacturerService.create(manufacturerToyota);
        Manufacturer manufacturerVolkswagen = new Manufacturer(null, "Volkswagen","Germany");
        manufacturerService.create(manufacturerVolkswagen);

        DriverService driverService = (DriverService) injector.getInstance(DriverService.class);
        Driver driverVasyl = new Driver(null, "Vasyl", "6783");
        driverService.create(driverVasyl);
        Driver driverDmytro = new Driver(null, "Dmytro", "3567");
        driverService.create(driverDmytro);

        CarService carService = (CarService) injector.getInstance(CarService.class);
        Car carToyota = new Car(null, "ToyotaYaris", manufacturerToyota);
        carToyota.setDrivers(new ArrayList<>(Arrays.asList(driverVasyl, driverDmytro)));
        carService.create(carToyota);
        Car carVolkswagen = new Car(null, "VolkswagenPollo", manufacturerVolkswagen);
        carVolkswagen.setDrivers(new ArrayList<>(Arrays.asList(driverVasyl)));
        carService.create(carVolkswagen);

        System.out.println("Dmytro drives: "
                + carService.getAllByDriver(driverDmytro.getId()));
        System.out.println("Car: " + carService.get(carToyota.getId()));
        System.out.println("All cars: " + carService.getAll());

        carService.removeDriverFromCar(driverDmytro, carToyota);
        carService.update(carToyota);
        System.out.println("Car Toyota after removing driver Dmytro: "
                + carService.get(carToyota.getId()));

        carService.delete(carVolkswagen.getId());
        System.out.println("All cars after deleting car Volkswagen: "
                + carService.getAll());
    }
}
