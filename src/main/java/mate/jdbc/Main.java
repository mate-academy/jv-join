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
        DriverService driverService = (DriverService) injector.getInstance(DriverService.class);

        Driver driverBob = new Driver();
        driverBob.setName("Bob");
        driverBob.setLicenseNumber("12345");
        driverService.create(driverBob);
        System.out.println(driverService.get(driverBob.getId()));

        Driver driverBil = new Driver();
        driverBil.setName("Bil");
        driverBil.setLicenseNumber("12346");
        driverService.create(driverBil);

        Driver driverAlice = new Driver();
        driverAlice.setId(driverBil.getId());
        driverAlice.setName("Alice");
        driverAlice.setLicenseNumber("12347");
        driverService.create(driverAlice);
        System.out.println(driverService.update(driverAlice));

        driverService.getAll().forEach(System.out::println);

        ManufacturerService manufacturerService
                = (ManufacturerService) injector.getInstance(ManufacturerService.class);
        Manufacturer manufacturerOpel = new Manufacturer();

        manufacturerOpel.setName("Opel");
        manufacturerOpel.setCountry("Germany");
        manufacturerService.create(manufacturerOpel);
        System.out.println(manufacturerService.get(manufacturerOpel.getId()));

        Manufacturer manufacturerReno = new Manufacturer();
        manufacturerReno.setName("Reno");
        manufacturerReno.setCountry("France");
        manufacturerService.create(manufacturerReno);

        Manufacturer manufacturerMazda = new Manufacturer();
        manufacturerMazda.setName("Mazda");
        manufacturerMazda.setCountry("Japan");
        manufacturerService.create(manufacturerMazda);
        System.out.println(manufacturerService.delete(manufacturerMazda.getId()));

        Manufacturer manufacturerNissan = new Manufacturer();
        manufacturerNissan.setId(manufacturerMazda.getId());
        manufacturerNissan.setName("Nissan");
        manufacturerNissan.setCountry("Japan");
        manufacturerService.create(manufacturerNissan);
        System.out.println(manufacturerService.update(manufacturerNissan));

        manufacturerService.getAll().forEach(System.out::println);

        Car car1 = new Car();
        car1.setModel("Opel Insignia");
        car1.setManufacturer(manufacturerOpel);
        car1.setDrivers(List.of(new Driver[]{driverService.get(driverBil.getId()),
                driverService.get(driverBob.getId())}));
        CarService carService = (CarService) injector.getInstance(CarService.class);
        carService.create(car1);
        System.out.println(carService.get(car1.getId()));

        Car car2 = new Car();
        car2.setModel("Reno Megan");
        car2.setManufacturer(manufacturerReno);
        carService.create(car2);

        Car car3 = new Car();
        car3.setModel("Mazda rx7");
        car3.setManufacturer(manufacturerMazda);
        carService.create(car3);
        System.out.println(carService.delete(car3.getId()));

        Car car4 = new Car();
        car4.setId(car1.getId());
        car4.setModel("Nisan Leaf");
        car4.setManufacturer(manufacturerNissan);
        carService.create(car4);
        System.out.println(carService.update(car4));

        carService.getAll().forEach(System.out::println);
        carService.getAllByDriver(driverBil.getId()).forEach(System.out::println);

        carService.addDriverToCar(driverAlice, car1);
        carService.removeDriverFromCar(driverAlice, car1);
    }
}
