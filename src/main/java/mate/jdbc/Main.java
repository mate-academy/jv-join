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
    private static final Injector INJECTOR = Injector.getInstance("mate.jdbc");

    public static void main(String[] args) {
        final CarService carService = (CarService) INJECTOR.getInstance(
                CarService.class);
        final DriverService driverService = (DriverService) INJECTOR.getInstance(
                DriverService.class);
        final ManufacturerService manufacturerService = (ManufacturerService) INJECTOR.getInstance(
                ManufacturerService.class);

        Manufacturer manufacturerVolvo = new Manufacturer();
        manufacturerVolvo.setName("Volvo");
        manufacturerVolvo.setCountry("Sweden");
        manufacturerService.create(manufacturerVolvo);

        Manufacturer manufacturerSkoda = new Manufacturer();
        manufacturerSkoda.setName("Skoda");
        manufacturerSkoda.setCountry("Czechia");
        manufacturerService.create(manufacturerSkoda);

        Driver driverOleg = new Driver();
        driverOleg.setName("Oleg");
        driverOleg.setLicenseNumber("45");
        driverService.create(driverOleg);

        Driver driverStepan = new Driver();
        driverStepan.setName("Stepan");
        driverStepan.setLicenseNumber("12");
        driverService.create(driverStepan);

        Driver driverOleksiy = new Driver();
        driverOleksiy.setName("Oleksiy");
        driverOleksiy.setLicenseNumber("17");
        driverService.create(driverOleksiy);

        driverService.getAll().forEach(System.out::println);

        List<Driver> driverList1 = new ArrayList<>();
        driverList1.add(driverOleg);
        driverList1.add(driverStepan);

        List<Driver> driverList2 = new ArrayList<>();
        driverList2.add(driverOleksiy);

        Car carVolvoC40 = new Car();
        carVolvoC40.setModel("C40");
        carVolvoC40.setManufacturer(manufacturerVolvo);
        carVolvoC40.setDrivers(driverList1);
        carService.create(carVolvoC40);

        Car carVolvoXC60 = new Car();
        carVolvoXC60.setModel("XC60");
        carVolvoXC60.setManufacturer(manufacturerVolvo);
        carVolvoXC60.setDrivers(driverList2);
        carService.create(carVolvoXC60);

        Car carSkodaOct = new Car();
        carSkodaOct.setModel("Octavia");
        carSkodaOct.setManufacturer(manufacturerSkoda);
        carSkodaOct.setDrivers(driverList2);
        carService.create(carSkodaOct);

        Car carSkodaScala = new Car();
        carSkodaScala.setModel("Scala");
        carSkodaScala.setManufacturer(manufacturerSkoda);
        carSkodaScala.setDrivers(driverList1);
        carService.create(carSkodaScala);

        carService.addDriverToCar(driverOleg, carVolvoXC60);
        carService.addDriverToCar(driverOleksiy, carSkodaOct);

        carService.removeDriverFromCar(driverStepan, carSkodaScala);

        carService.getAllByDriver(1L).forEach(System.out::println);
        System.out.println("=============================");
        carService.getAll().forEach(System.out::println);
    }
}
