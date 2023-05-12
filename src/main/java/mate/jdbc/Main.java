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
    private static Injector injector = Injector.getInstance("mate.jdbc");

    public static void main(String[] args) {
        //create manufacturers
        System.out.println("Create manufacturers...");
        ManufacturerService manufacturerService =
                (ManufacturerService) injector.getInstance(ManufacturerService.class);
        Manufacturer skoda = new Manufacturer();
        skoda.setName("Skoda");
        skoda.setCountry("Czech");
        manufacturerService.create(skoda);
        Manufacturer ford = new Manufacturer();
        ford.setName("Ford");
        ford.setCountry("USA");
        manufacturerService.create(ford);
        Manufacturer mitsubishi = new Manufacturer();
        mitsubishi.setName("Mitsubishi");
        mitsubishi.setCountry("Japan");
        manufacturerService.create(mitsubishi);
        manufacturerService.getAll().forEach(System.out::println);
        System.out.println("Manufacturers were created" + System.lineSeparator());
        //create cars
        System.out.println("Create cars...");
        Car octavia = new Car();
        octavia.setModel("Octavia");
        octavia.setManufacturer(skoda);
        octavia.setDrivers(new ArrayList<>());
        CarService carService =
                (CarService) injector.getInstance(CarService.class);
        carService.create(octavia);
        Car fabia = new Car();
        fabia.setModel("Fabia");
        fabia.setManufacturer(skoda);
        fabia.setDrivers(new ArrayList<>());
        carService.create(fabia);
        Car focus = new Car();
        focus.setModel("Focus");
        focus.setManufacturer(ford);
        focus.setDrivers(new ArrayList<>());
        carService.create(focus);
        Car mandeo = new Car();
        mandeo.setModel("Mandeo");
        mandeo.setManufacturer(ford);
        mandeo.setDrivers(new ArrayList<>());
        carService.create(mandeo);
        Car outlander = new Car();
        outlander.setModel("Outlander");
        outlander.setManufacturer(mitsubishi);
        outlander.setDrivers(new ArrayList<>());
        carService.create(outlander);
        Car pajero = new Car();
        pajero.setModel("Pajero");
        pajero.setManufacturer(mitsubishi);
        pajero.setDrivers(new ArrayList<>());
        carService.create(pajero);
        Car l200 = new Car();
        l200.setModel("L200");
        l200.setManufacturer(mitsubishi);
        l200.setDrivers(new ArrayList<>());
        carService.create(l200);
        carService.getAll().forEach(System.out::println);
        System.out.println("Cars were created" + System.lineSeparator());
        //create drivers
        System.out.println("Create drivers...");
        DriverService driverService =
                (DriverService) injector.getInstance(DriverService.class);
        Driver andrey = new Driver();
        andrey.setName("Andrey");
        andrey.setLicenseNumber("123456");
        driverService.create(andrey);
        Driver bogdan = new Driver();
        bogdan.setName("Bogdan");
        bogdan.setLicenseNumber("234567");
        driverService.create(bogdan);
        Driver sergey = new Driver();
        sergey.setName("Sergey");
        sergey.setLicenseNumber("345678");
        driverService.create(sergey);
        Driver zumba = new Driver();
        zumba.setName("Zumba");
        zumba.setLicenseNumber("456789");
        driverService.create(zumba);
        driverService.getAll().forEach(System.out::println);
        System.out.println("Drivers were created" + System.lineSeparator());
        //add drivers to cars
        System.out.println("Add drivers to cars...");
        carService.addDriverToCar(andrey, octavia);
        carService.addDriverToCar(andrey, fabia);
        carService.addDriverToCar(andrey, focus);
        carService.addDriverToCar(andrey, mandeo);
        carService.addDriverToCar(andrey, outlander);
        carService.addDriverToCar(andrey, pajero);
        carService.addDriverToCar(andrey, l200);
        carService.addDriverToCar(bogdan, octavia);
        carService.addDriverToCar(bogdan, fabia);
        carService.addDriverToCar(bogdan, focus);
        carService.addDriverToCar(sergey, focus);
        carService.addDriverToCar(sergey, mandeo);
        carService.addDriverToCar(sergey, outlander);
        carService.addDriverToCar(sergey, pajero);
        carService.addDriverToCar(zumba, outlander);
        carService.addDriverToCar(zumba, pajero);
        carService.addDriverToCar(zumba, l200);
        carService.getAll().forEach(System.out::println);
        System.out.println("Drivers were added to cars" + System.lineSeparator());
        //update car Octavia, id = 1
        System.out.println("Car \"Octavia\" update...");
        System.out.println(carService.get(1L));
        Car rapid = new Car();
        rapid.setId(1L);
        rapid.setModel("Rapid");
        rapid.setManufacturer(skoda);
        rapid.setDrivers(new ArrayList<>());
        carService.update(rapid);
        System.out.println(carService.get(1L));
        System.out.println("Car \"Octavia\" was updated to \"Rapid\"!");
        System.out.println("Add new drivers to \"Rapid\"");
        carService.addDriverToCar(andrey, rapid);
        carService.addDriverToCar(zumba, rapid);
        System.out.println(carService.get(1L));
        System.out.println("Drivers were added to \"Rapid\"" + System.lineSeparator());
        //remove driver from car
        System.out.println("Remove driver \"Zumba\" from \"Rapid\"");
        carService.removeDriverFromCar(zumba, rapid);
        System.out.println(carService.get(1L));
        System.out.println("Driver \"Zumba\" was removed from \"Rapid\"" + System.lineSeparator());
        //show all car by driver id
        System.out.println("Show all car by driver id = 3");
        carService.getAllByDriver(3L).forEach(System.out::println);
    }
}
