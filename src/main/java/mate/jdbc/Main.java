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
    private static final String SEPARATOR = System.lineSeparator();

    public static void main(String[] args) {
        Driver bohdan = new Driver("Bohdan", "Chupika228");
        Driver bob = new Driver("Bob", "Alice123");
        DriverService manager = (DriverService) injector.getInstance(DriverService.class);
        manager.create(bohdan);
        manager.create(bob);
        System.out.println("List of all drivers ->");
        manager.getAll().forEach(System.out::println);
        bohdan.setName("Bodya");
        bohdan.setLicenseNumber("Boroda4");
        manager.update(bohdan);
        System.out.println(SEPARATOR + "Find Bohdan " + manager.get(bohdan.getId()));
        System.out.println(SEPARATOR + "Fire Bohdan: " + manager.delete(bohdan.getId()));
        System.out.println(SEPARATOR + "Info in table drivers ->");
        manager.getAll().forEach(System.out::println);
        System.out.println(SEPARATOR);

        Manufacturer bmw = new Manufacturer("BMW", "Germany");
        Manufacturer ford = new Manufacturer("Ford", "USA");
        ManufacturerService businessman = (ManufacturerService)
                injector.getInstance(ManufacturerService.class);
        businessman.create(bmw);
        businessman.create(ford);
        System.out.println("List of all manufacturers ->");
        businessman.getAll().forEach(System.out::println);
        bmw.setCountry("Ukraine");
        bmw.setName("Mercedes");
        businessman.update(bmw);
        System.out.println(SEPARATOR);
        System.out.println("Find bmw factory that now mercedes: " + businessman.get(bmw.getId())
                + SEPARATOR);
        System.out.println("It`s a crap! We must destroy it! " + businessman.delete(bmw.getId()));
        System.out.println(SEPARATOR + "Info in table manufacturers ->");
        businessman.getAll().forEach(System.out::println);
        System.out.println(SEPARATOR);

        CarService factory = (CarService) injector.getInstance(CarService.class);
        List<Driver> driverList = new ArrayList<>();
        driverList.add(bohdan);
        Car racingCar = new Car("Ford", driverList, ford);
        factory.create(racingCar);
        System.out.println("List of all cars: ");
        factory.getAll().forEach(System.out::println);

        System.out.println(SEPARATOR + "Find car by id " + factory.get(racingCar.getId()));

        ford.setId(7L);
        racingCar.setCarModel("Bugatti");
        driverList.add(bob);
        racingCar.setManufacturer(ford);
        System.out.println(SEPARATOR + "Updating car " + factory.update(racingCar) + SEPARATOR);

        System.out.println("Get all info by driver: ");
        factory.getAllByDriver(bob.getId()).forEach(System.out::println);

        System.out.println(SEPARATOR + "Adding driver to car");
        Driver michael = new Driver("Michael", "Champion№1");
        manager.create(michael);
        factory.addDriverToCar(michael, racingCar);
        System.out.println(SEPARATOR + "Get car after adding driver michael: "
                + SEPARATOR + racingCar);
        factory.removeDriverFromCar(bohdan, racingCar);
        System.out.println(SEPARATOR + "Get car after deleting driver bohdan: "
                + SEPARATOR + racingCar + SEPARATOR);
        System.out.println("Deleting car " + racingCar.getCarModel() + " "
                + factory.delete(racingCar.getId()));
        System.out.println(SEPARATOR + "List of all cars:");
        factory.getAll().forEach(System.out::println);

        System.out.println(SEPARATOR + "Deleting all info...");
        manager.getAll().forEach(d -> manager.delete(d.getId()));
        businessman.getAll().forEach(f -> businessman.delete(f.getId()));
        factory.getAll().forEach(c -> factory.delete(c.getId()));

    }
}
