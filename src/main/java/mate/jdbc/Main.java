package mate.jdbc;

import java.util.List;
import java.util.stream.Collectors;
import mate.jdbc.lib.Injector;
import mate.jdbc.model.Car;
import mate.jdbc.model.Driver;
import mate.jdbc.model.Manufacturer;
import mate.jdbc.service.CarService;
import mate.jdbc.service.DriverService;
import mate.jdbc.service.ManufacturerService;

public class Main {
    private static final Injector injector = Injector.getInstance("mate.jdbc");
    private static final CarService carService =
            (CarService) injector.getInstance(CarService.class);
    private static final DriverService driverService =
            (DriverService) injector.getInstance(DriverService.class);
    private static final ManufacturerService manufacturerService =
            (ManufacturerService) injector.getInstance(ManufacturerService.class);

    public static void main(String[] args) {
        Manufacturer honda = new Manufacturer();
        honda.setCountry("Japan");
        honda.setName("Honda");
        manufacturerService.create(honda);

        Manufacturer audi = new Manufacturer();
        audi.setName("Audi");
        audi.setCountry("Germany");
        manufacturerService.create(audi);

        Manufacturer lamborghini = new Manufacturer();
        lamborghini.setCountry("Italy");
        lamborghini.setName("Lamborghini");
        manufacturerService.create(lamborghini);

        Driver vinDiesel = new Driver();
        vinDiesel.setName("Vin Diesel");
        vinDiesel.setLicenseNumber("89AD");
        driverService.create(vinDiesel);

        Driver bob = new Driver();
        bob.setName("Bob");
        bob.setLicenseNumber("00001234");
        driverService.create(bob);

        Driver jhon = new Driver();
        jhon.setName("Jhon");
        jhon.setLicenseNumber("00005679");
        driverService.create(jhon);

        Car carAudi = new Car();
        carAudi.setManufacturer(audi);
        carAudi.setModel("AudiQ7");
        carAudi.setDrivers(List.of(jhon, vinDiesel));
        carService.create(carAudi);

        Car carHonda = new Car();
        carHonda.setManufacturer(honda);
        carHonda.setModel("HondaCRV");
        carHonda.setDrivers(List.of(jhon));
        carService.create(carHonda);

        Car carLamborghini = new Car();
        carLamborghini.setManufacturer(lamborghini);
        carLamborghini.setModel("Lamborghini");
        carLamborghini.setDrivers(List.of(bob, vinDiesel));
        carService.create(carLamborghini);

        System.out.println("HondaCRV drivers: ");
        carHonda.getDrivers().forEach(System.out::println);
        System.out.println();

        System.out.println("Audi drivers: ");
        carAudi.getDrivers().forEach(System.out::println);
        System.out.println();

        System.out.println("Lamborghini drivers: ");
        carLamborghini.getDrivers().forEach(System.out::println);
        System.out.println();

        System.out.println("Get all cars: ");
        carService.getAll().stream().map(Car::getModel).forEach(System.out::println);
        System.out.println();

        System.out.println("Get by id: ");
        System.out.println(carService.get(carAudi.getId()).getModel());
        System.out.println();

        System.out.println(carService.getAllByDriver(vinDiesel.getId())
                .stream()
                .map(Car::getModel)
                .collect(Collectors.toList()));

        //The method addDriverToCar catches an exception...
        // I can't to add any driver to List<Driver> drivers...
        carService.addDriverToCar(jhon,carHonda);
    }
}
