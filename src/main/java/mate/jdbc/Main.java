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
    private static ManufacturerService manufacturerService;
    private static DriverService driverService;
    private static CarService carService;

    public static void main(String[] args) {
        manufacturerService = (ManufacturerService) injector
                .getInstance(ManufacturerService.class);
        carService = (CarService) injector.getInstance(CarService.class);
        driverService = (DriverService)injector.getInstance(DriverService.class);

        init();

        carService.getAll().forEach(System.out::println);
        Car car = carService.get(1L);
        car.setModel("FORD");
        Driver max = new Driver("mad Max", "no license never was");
        max = driverService.create(max);
        List<Driver> drivers = car.getDrivers();
        drivers.add(max);
        car.setDrivers(drivers);
        carService.update(car);
        System.out.println(car);
        carService.delete(car.getId());
        carService.getAll().forEach(System.out::println);
    }

    private static void init() {
        Driver alonso = new Driver("Alonso", "Alpin#40");
        alonso = driverService.create(alonso);
        Driver vettel = new Driver("Vettel", "AstonMartin#34");
        vettel = driverService.create(vettel);
        Driver verstappen = new Driver("Verstappen", "RedBull#24");
        verstappen = driverService.create(verstappen);
        Driver perez = new Driver("Perez", "RedBull#32");
        perez = driverService.create(perez);
        Driver norris = new Driver("Norris", "McLaren#22");
        norris = driverService.create(norris);
        Driver ricardo = new Driver("Ricardo", "McLaren#32");
        ricardo = driverService.create(ricardo);
        Driver vandonne = new Driver("Alonso", "McLaren#30");
        vandonne = driverService.create(vandonne);

        Manufacturer mcLaren = new Manufacturer("McLaren Technology Group", "UK");
        mcLaren = manufacturerService.create(mcLaren);
        Manufacturer redBull = new Manufacturer("Red Bull GmbH", "Austria");
        redBull = manufacturerService.create(redBull);
        Manufacturer astonMartin = new Manufacturer("Aston Martin Aramco Team", "UK");
        astonMartin = manufacturerService.create(astonMartin);
        Manufacturer alpin = new Manufacturer("BWT Alpine F1 Team", "France");
        alpin = manufacturerService.create(alpin);

        Car mcl36 = new Car("McLaren MCL36", mcLaren, List.of(norris, ricardo, vandonne));
        carService.create(mcl36);
        Car rb18 = new Car("Red Bull RB18", redBull, List.of(verstappen, perez));
        carService.create(rb18);
        Car amr21 = new Car("Aston Martin AMR21", astonMartin, List.of(vettel));
        carService.create(amr21);
        Car a522 = new Car("Alpin A522", alpin, List.of(alonso));
        carService.create(a522);
    }
}
