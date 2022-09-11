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
        DriverService driverService = (DriverService) injector.getInstance(DriverService.class);
        ManufacturerService manufacturerService =
                (ManufacturerService) injector.getInstance(ManufacturerService.class);
        Manufacturer lexus = manufacturerService.create(
                new Manufacturer(null, "Lexus", "Japan"));
        Manufacturer bmw = manufacturerService.create(
                new Manufacturer(null, "BMW", "Germany"));
        Driver vettel = driverService.create(
                new Driver(null, "Seb Vettel", "GE2011BR"));
        Driver alonso = driverService.create(
                new Driver(null, "Fernando Alonso", "CHAMP06"));
        Driver hamilton = driverService.create(
                new Driver(null, "Lewis Hamilton", "LOH21CENT"));
        Car lexusCar = carService.create(
                new Car(null, "IS200", lexus, List.of(hamilton, vettel)));
        Car bmwCar = carService.create(
                new Car(null, "525", bmw, List.of(hamilton, alonso)));
        carService.delete(bmwCar.getId());
        carService.addDriverToCar(alonso, lexusCar);
        carService.removeDriverFromCar(hamilton, lexusCar);
        System.out.println(carService.get(lexusCar.getId()));
        carService.getAll().forEach(System.out::println);
        System.out.println("------------");
        carService.getAllByDriver(vettel.getId()).forEach(System.out::println);
    }
}
