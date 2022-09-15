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

    public static void main(String[] args) {
        ManufacturerService manufacturerService =
                (ManufacturerService) injector.getInstance(ManufacturerService.class);
        Manufacturer cadillac = new Manufacturer();
        cadillac.setName("Cadillac");
        cadillac.setCountry("USA");
        manufacturerService.create(cadillac);

        Manufacturer audi = new Manufacturer();
        audi.setName("Audi");
        audi.setCountry("Germany");
        manufacturerService.create(audi);

        Manufacturer honda = new Manufacturer();
        honda.setName("Honda");
        honda.setCountry("China");
        manufacturerService.create(honda);

        manufacturerService.update(new Manufacturer(honda.getId(),
                "Honda", "Japan"));
        manufacturerService.delete(cadillac.getId());
        manufacturerService.getAll().forEach(System.out::println);
        manufacturerService.get(honda.getId());

        DriverService driverService = (DriverService) injector.getInstance(DriverService.class);

        Driver michaelSchumacher = new Driver("Michael Schumacher","123");
        Driver fernandoAlonso = new Driver("Fernando Alonso", "456");
        Driver kimiRaikkonen = new Driver("Kimi Raikkonen","789");
        Driver mikaHakkinen = new Driver("MikaHakkinen","741");
        Driver paulWalker = new Driver("Paul Walker","852");

        driverService.create(michaelSchumacher);
        driverService.create(fernandoAlonso);
        driverService.create(kimiRaikkonen);
        driverService.create(mikaHakkinen);
        driverService.create(paulWalker);
        List<Driver> audiDrivers = new ArrayList<>();
        audiDrivers.add(michaelSchumacher);
        List<Driver> hondaDrivers = new ArrayList<>();
        hondaDrivers.add(fernandoAlonso);

        driverService.delete(paulWalker.getId());
        driverService.getAll().forEach(System.out::println);
        mikaHakkinen.setLicenseNumber("963");
        driverService.update(mikaHakkinen);
        System.out.println(driverService.get(mikaHakkinen.getId()));
        System.out.println(driverService.get(mikaHakkinen.getId()));

        Car audiModel = new Car();
        audiModel.setModel("Q7");
        audiModel.setDrivers(audiDrivers);
        audiModel.setManufacturer(audi);

        CarService carService = (CarService) injector.getInstance(CarService.class);
        carService.create(audiModel);

        Car civic = new Car();
        civic.setManufacturer(honda);
        civic.setDrivers(hondaDrivers);
        civic.setModel("Civic");
        carService.create(civic);

        carService.getAll().forEach(System.out::println);
        carService.removeDriverFromCar(fernandoAlonso, civic);
        System.out.println(carService.get(civic.getId()));
        carService.addDriverToCar(mikaHakkinen, civic);
        System.out.println(carService.get(civic.getId()));

        audiModel.setModel("Q8");
        carService.update(audiModel);
        System.out.println(carService.get(audiModel.getId()));

        carService.delete(civic.getId());
        carService.getAll().forEach(System.out::println);
    }
}
