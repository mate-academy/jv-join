package mate.jdbc;

import java.util.ArrayList;
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
        ManufacturerService manufacturerService = (ManufacturerService)
                injector.getInstance(ManufacturerService.class);
        Manufacturer alfaRomeo = new Manufacturer(null, "Alfa Romeo", "Italy");
        Manufacturer porsche = new Manufacturer(null, "Porsche", "Germany");
        manufacturerService.create(alfaRomeo);
        manufacturerService.create(porsche);
        DriverService driverService = (DriverService)
                injector.getInstance(DriverService.class);
        Driver francescoRavioli = new Driver(null, "Francesco Ravioli", "69396");
        Driver karlSchmidt = new Driver(null, "Karl Schmidt", "36963");
        driverService.create(francescoRavioli);
        driverService.create(karlSchmidt);
        CarService carService = (CarService)
                injector.getInstance(CarService.class);
        Car car = new Car(null, "159", alfaRomeo, new ArrayList<>());
        car.getDrivers().add(francescoRavioli);
        carService.create(car);
        carService.get(car.getId());
        car.setManufacturer(porsche);
        car.setModel("Cayenne");
        carService.update(car);
        carService.removeDriverFromCar(francescoRavioli, car);
        carService.addDriverToCar(karlSchmidt, car);
        carService.getAll().forEach(System.out::println);
        carService.delete(car.getId());
    }
}
