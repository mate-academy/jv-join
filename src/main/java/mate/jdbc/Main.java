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
    private static final DriverService driverService =
            (DriverService) injector.getInstance(DriverService.class);
    private static final CarService carService =
            (CarService) injector.getInstance(CarService.class);
    private static final ManufacturerService manufacturerService =
            (ManufacturerService) injector.getInstance(ManufacturerService.class);

    public static void main(String[] args) {
        Driver johnDriver = new Driver("John Green", "12345");
        johnDriver = driverService.create(johnDriver);
        Driver ericDriver = new Driver("Eric Brown", "65478");
        ericDriver = driverService.create(ericDriver);
        Driver christopherDriver = new Driver("Christopher McGill", "98765");
        christopherDriver = driverService.create(christopherDriver);
        Driver smithDriver = new Driver("mr. Smith", "55577");
        smithDriver = driverService.create(smithDriver);
        Driver jamesDriver = new Driver("James Bond", "007");
        jamesDriver = driverService.create(jamesDriver);

        Manufacturer bmwManufacturer = new Manufacturer("BMW", "Germany");
        bmwManufacturer = manufacturerService.create(bmwManufacturer);
        Manufacturer toyotaManufacturer = new Manufacturer("Toyota", "Japan");
        toyotaManufacturer = manufacturerService.create(toyotaManufacturer);
        Manufacturer renaultManufacturer = new Manufacturer("Renault", "France");
        renaultManufacturer = manufacturerService.create(renaultManufacturer);
        Manufacturer fordManufacturer = new Manufacturer("Ford", "USA");
        fordManufacturer = manufacturerService.create(fordManufacturer);
        Manufacturer hondaManufacturer = new Manufacturer("Honda", "Japan");
        hondaManufacturer = manufacturerService.create(hondaManufacturer);

        Car x5 = new Car();
        x5.setModel("X5");
        x5.setManufacturer(bmwManufacturer);
        x5.setDrivers(new ArrayList<>(List.of(johnDriver, jamesDriver)));
        x5 = carService.create(x5);

        Car corollaCar = new Car();
        corollaCar.setModel("Corolla");
        corollaCar.setManufacturer(toyotaManufacturer);
        corollaCar.setDrivers(new ArrayList<>(List.of(johnDriver, ericDriver)));
        corollaCar = carService.create(corollaCar);

        Car mondeoCar = new Car();
        mondeoCar.setModel("Mondeo");
        mondeoCar.setManufacturer(fordManufacturer);
        mondeoCar.setDrivers(new ArrayList<>(List.of(christopherDriver, jamesDriver)));
        mondeoCar = carService.create(mondeoCar);

        Car dusterCar = new Car();
        dusterCar.setModel("Duster");
        dusterCar.setManufacturer(renaultManufacturer);
        dusterCar.setDrivers(new ArrayList<>(List.of(smithDriver, ericDriver)));
        dusterCar = carService.create(dusterCar);

        Car jazzCar = new Car();
        jazzCar.setModel("Jazz");
        jazzCar.setManufacturer(hondaManufacturer);
        jazzCar.setDrivers(new ArrayList<>(List.of(christopherDriver, johnDriver)));
        jazzCar = carService.create(jazzCar);

        x5.setModel("X7");
        carService.update(x5);

        carService.delete(mondeoCar.getId());

        carService.addDriverToCar(ericDriver, x5);

        carService.removeDriverFromCar(ericDriver, dusterCar);

        System.out.println("=====");
        carService.getAllByDriver(johnDriver.getId()).forEach(System.out::println);
        System.out.println("=====");
        carService.getAll().forEach(System.out::println);
    }
}
