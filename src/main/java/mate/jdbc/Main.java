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
        ManufacturerService manufacturerService = (ManufacturerService) injector
                .getInstance(ManufacturerService.class);
        Manufacturer shkoda = new Manufacturer("Skoda", "Czech");
        manufacturerService.create(shkoda);

        DriverService driverService = (DriverService) injector.getInstance(DriverService.class);
        Driver elvis = new Driver("Elvis", "AA0ET");
        Driver jan = new Driver("Jan","ACLKI");

        driverService.create(jan);
        driverService.create(elvis);

        CarService carService = (CarService) injector.getInstance(CarService.class);
        Car jetta = new Car("Jetta",shkoda,new ArrayList<>());
        carService.create(jetta);
        carService.addDriverToCar(jan,jetta);
        carService.addDriverToCar(elvis,jetta);

        carService.getAll().forEach(System.out::println);
        carService.getAllByDriver(elvis.getId());

        carService.delete(jetta.getId());

        jetta.setModel("tiguan");
        carService.update(jetta);

        carService.removeDriverFromCar(jan,jetta);

    }
}
