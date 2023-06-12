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
    public static void main(String[] args) {
        Injector injector = Injector.getInstance("mate.jdbc");
        DriverService driverService = (DriverService) injector.getInstance(DriverService.class);
        ManufacturerService manufacturerService =
                (ManufacturerService) injector.getInstance(ManufacturerService.class);

        Driver taras = new Driver("Taras", "AA0000AA");
        Driver borys = new Driver("Borys", "AA0001AA");
        Manufacturer toyota = new Manufacturer("Toyota", "Japan");

        taras = driverService.create(taras);
        borys = driverService.create(borys);
        toyota = manufacturerService.create(toyota);

        CarService carService = (CarService) injector.getInstance(CarService.class);

        List<Driver> drivers = new ArrayList<>();
        drivers.add(taras);
        Car toyotaCamry = new Car("Toyota Camry", toyota, drivers);
        Car createdToyotaCamry = carService.create(toyotaCamry);
        carService.addDriverToCar(borys, toyotaCamry);
        System.out.println(createdToyotaCamry);
        System.out.println(System.lineSeparator());

        Car returnedToyotaCamry = carService.get(createdToyotaCamry.getId());
        System.out.println(returnedToyotaCamry);
        System.out.println(System.lineSeparator());

        List<Car> cars = carService.getAll();
        cars.forEach(System.out::println);
        System.out.println(System.lineSeparator());

        Driver biba = new Driver("Biba", "AA0002AA");
        Driver boba = new Driver("Boba", "AA0003AA");
        biba = driverService.create(biba);
        boba = driverService.create(boba);

        List<Driver> drivers1 = new ArrayList<>();
        drivers1.add(biba);
        drivers1.add(boba);
        drivers1.add(taras);
        createdToyotaCamry.setDrivers(drivers1);
        Car updatedToyotaCamry = carService.update(createdToyotaCamry);
        System.out.println(updatedToyotaCamry);
        System.out.println(System.lineSeparator());

        List<Driver> drivers2 = new ArrayList<>();
        drivers2.add(taras);
        drivers2.add(borys);
        Car dummy = new Car("Dummy model", toyota, drivers2);
        Car createdDummy = carService.create(dummy);
        carService.removeDriverFromCar(borys, createdDummy);
        boolean delete = carService.delete(createdDummy.getId());
        System.out.println(delete);
        System.out.println(System.lineSeparator());

        List<Car> allByTaras = carService.getAllByDriver(taras.getId());
        allByTaras.forEach(System.out::println);
    }
}
