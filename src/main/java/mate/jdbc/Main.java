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
    private static final CarService carService =
            (CarService) injector.getInstance(CarService.class);
    private static final DriverService driverService =
            (DriverService) injector.getInstance(DriverService.class);
    private static final ManufacturerService manufacturerService =
            (ManufacturerService) injector.getInstance(ManufacturerService.class);

    public static void main(String[] args) {
        Manufacturer bmw = new Manufacturer("BMW", "Germany");
        Manufacturer audi = new Manufacturer("AUDI", "Germany");
        Manufacturer ford = new Manufacturer("Ford", "USA");
        Manufacturer mercedes = new Manufacturer("Mercedes", "Germany");

        System.out.println("----SaveManufacturer----");
        Manufacturer savedBmw = manufacturerService.create(bmw);
        Manufacturer savedAudi = manufacturerService.create(audi);
        Manufacturer savedFord = manufacturerService.create(ford);
        Manufacturer savedMercedes = manufacturerService.create(mercedes);
        System.out.println(savedFord);
        System.out.println(savedBmw);
        System.out.println(savedAudi);
        System.out.println(savedMercedes);
        System.out.println();

        System.out.println("----Update----");
        System.out.println("Manufacturer before updating: " + savedMercedes);
        savedMercedes.setCountry("Ukraine");
        Manufacturer updatedMercedes = manufacturerService.update(savedMercedes);
        System.out.println(updatedMercedes);
        System.out.println();

        System.out.println("---Delete----");
        System.out.println("DB before deleting manufacturer");
        System.out.println(manufacturerService.getAll());
        manufacturerService.delete(updatedMercedes.getId());
        System.out.println("DB after deleting manufacturer BMW");
        System.out.println(manufacturerService.getAll());
        System.out.println();

        System.out.println("----Get----");
        System.out.println(manufacturerService.get(savedFord.getId()));

        System.out.println();
        System.out.println("----Drivers----");
        System.out.println();
        Driver nick = new Driver("Nick", "12345");
        Driver john = new Driver("John", "54321");
        Driver ira = new Driver("Ira", "653200");

        System.out.println("----Save Drivers----");
        Driver savedNick = driverService.create(nick);
        Driver savedJohn = driverService.create(john);
        Driver savedIra = driverService.create(ira);
        System.out.println(savedJohn);
        System.out.println(savedNick);
        System.out.println(savedIra);
        System.out.println();

        System.out.println("----Update----");
        System.out.println("Driver John before updating - " + savedJohn);
        savedJohn.setLicenseNumber("98765");
        Driver updatedJohn = driverService.update(savedJohn);
        System.out.println(updatedJohn);
        System.out.println();

        System.out.println("---Delete----");
        System.out.println("Drivers table before deleting John");
        System.out.println(driverService.getAll());
        driverService.delete(updatedJohn.getId());
        System.out.println("Drivers table after deleting John");
        System.out.println(driverService.getAll());
        System.out.println();

        System.out.println("----Get----");
        System.out.println(driverService.get(savedNick.getId()));

        Car x5 = new Car("X5", "Black", savedBmw);
        Car q7 = new Car("Q7", "White", savedAudi);
        Car mustang = new Car("Mustang", "yellow", savedFord);
        Car explorer = new Car("Explorer", "Black", savedFord);

        System.out.println("----Cars----");
        System.out.println();
        System.out.println("----SaveCar----");
        Car savedX5 = carService.create(x5);
        Car savedQ7 = carService.create(q7);
        Car savedMustang = carService.create(mustang);
        Car savedExplorer = carService.create(explorer);
        System.out.println(savedX5);
        System.out.println(savedQ7);
        System.out.println(savedMustang);
        System.out.println(savedExplorer);
        System.out.println();

        System.out.println("----Update----");
        System.out.println("Car ford mustang before updating: ");
        System.out.println(savedMustang);
        savedMustang.setColor("Purple");
        Car updatedMustang = carService.update(savedMustang);
        System.out.println("Car ford mustang after updating: ");
        System.out.println(updatedMustang);
        System.out.println();

        System.out.println("----Delete----");
        System.out.println("DB before deleting ford mustang");
        System.out.println(carService.getAll());
        carService.delete(updatedMustang.getId());
        System.out.println("DB after deleting for mustang");
        System.out.println(carService.getAll());
        System.out.println();

        System.out.println("----Setting drivers for car----");

        System.out.println("Cars of each driver before setting drivers to cars");
        System.out.println("Driver Ira: " + carService.getAllByDriver(savedIra.getId()));
        System.out.println("Driver Nick: " + carService.getAllByDriver(savedNick.getId()));

        System.out.println("Cars of each driver after setting drivers to cars");
        x5.setDrivers(new ArrayList<>(List.of(savedIra)));
        q7.setDrivers(new ArrayList<>(List.of(savedIra, savedNick)));
        explorer.setDrivers(new ArrayList<>(List.of(savedNick)));
        carService.update(x5);
        carService.update(q7);
        carService.update(explorer);
        System.out.println("Driver Ira: ");
        carService.getAllByDriver(savedIra.getId()).forEach(System.out::println);
        System.out.println("Driver Nick: ");
        carService.getAllByDriver(savedNick.getId()).forEach(System.out::println);

        System.out.println("All Ira's cars");
        carService.getAllByDriver(savedIra.getId()).forEach(System.out::println);
        System.out.println("Remove driver Ira from BMW X5");
        carService.removeDriverFromCar(savedIra, x5);
        System.out.println("All Ira's cars");
        carService.getAllByDriver(savedIra.getId()).forEach(System.out::println);
        System.out.println("Adding driver Nick to BMW X5");
        carService.addDriverToCar(savedNick, x5);
        System.out.println("All Nick's cars");
        carService.getAllByDriver(savedNick.getId()).forEach(System.out::println);
    }
}

