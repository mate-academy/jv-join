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
    private static Injector injector = Injector.getInstance("mate.jdbc");

    public static void main(String[] args) {
        // test your code here
        DriverService driverService
                = (DriverService) injector.getInstance(DriverService.class);

        List<Driver> bmwDrivers = new ArrayList<>();
        Driver john = new Driver("John", "45698");
        Driver paul = new Driver("Paul", "77777");
        driverService.create(john);
        driverService.create(paul);
        bmwDrivers.add(john);
        bmwDrivers.add(paul);

        ManufacturerService manufacturerService
                = (ManufacturerService) injector.getInstance(ManufacturerService.class);
        Manufacturer bmwManufacturer = new Manufacturer();
        bmwManufacturer.setName("BMW");
        bmwManufacturer.setCountry("Germany");
        manufacturerService.create(bmwManufacturer);
        Manufacturer toyotaManufacturer = new Manufacturer();
        toyotaManufacturer.setName("Toyota");
        toyotaManufacturer.setCountry("Japan");
        manufacturerService.create(toyotaManufacturer);

        Car bmw = new Car("BMW Super", bmwManufacturer, bmwDrivers);

        CarService carService = (CarService) injector.getInstance(CarService.class);
        carService.create(bmw);

        List<Driver> toyotaDrivers = new ArrayList<>();
        toyotaDrivers.add(john);
        toyotaDrivers.add(paul);
        Car toyota = new Car("Toyota model one", toyotaManufacturer, toyotaDrivers);
        carService.create(toyota);

        List<Driver> lexusDrivers = new ArrayList<>();
        lexusDrivers.add(john);
        lexusDrivers.add(paul);
        Car lexus = new Car("Lexus", toyotaManufacturer, lexusDrivers);
        carService.create(lexus);

        System.out.println(carService.get(bmw.getId()));
        carService.delete(toyota.getId());

        List<Driver> newBmwDrivers = new ArrayList<>();
        Driver tony = new Driver("Tony", "879123");
        driverService.create(tony);
        newBmwDrivers.add(tony);
        bmw.setDrivers(newBmwDrivers);
        System.out.println(carService.update(bmw));

        System.out.println();
        System.out.println(carService.get(bmw.getId()));
        Driver vito = new Driver("Vito", "65486");
        driverService.create(vito);
        carService.addDriverToCar(vito, bmw);
        carService.removeDriverFromCar(paul, bmw);
        System.out.println(carService.get(bmw.getId()));

        System.out.println();
        System.out.println("Bobs car info: " + carService.getAllByDriver(1L));
        System.out.println("John car info: " + carService.getAllByDriver(john.getId()));
        System.out.println("Paul car info: " + carService.getAllByDriver(paul.getId()));
        System.out.println("Tony car info: " + carService.getAllByDriver(tony.getId()));
        System.out.println("Vito car info: " + carService.getAllByDriver(vito.getId()));
        System.out.println("All: " + carService.getAll());
    }
}
