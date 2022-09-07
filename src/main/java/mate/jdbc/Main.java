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
        CarService carService = (CarService)
                injector.getInstance(CarService.class);
        ManufacturerService manufacturerService = (ManufacturerService)
                injector.getInstance(ManufacturerService.class);
        DriverService driverService = (DriverService)
                injector.getInstance(DriverService.class);
        Driver bob = driverService.create(new Driver("Bob", "12345"));
        Driver alice = driverService.create(new Driver("Alice", "67890"));
        List<Driver> aliceList = new ArrayList<>();
        List<Driver> bobList = new ArrayList<>();
        aliceList.add(alice);
        bobList.add(bob);
        Manufacturer audi = manufacturerService.create(
                new Manufacturer("Audi", "Germany"));
        Manufacturer volkswagen = manufacturerService.create(
                new Manufacturer("Volkswagen", "Germany"));
        Car audi1 = new Car("Q5", audi, aliceList);
        Car volkswagen1 = new Car("Polo", volkswagen, bobList);
        audi1 = carService.create(audi1);
        volkswagen1 = carService.create(volkswagen1);
        audi1.setModel("RSQ7");
        volkswagen1.setModel("Touareg");
        audi1 = carService.update(audi1);
        volkswagen1 = carService.update(volkswagen1);
        carService.delete(volkswagen1.getId());
        carService.addDriverToCar(bob, audi1);
        audi1 = carService.update(audi1);
        carService.removeDriverFromCar(alice, audi1);
        carService.update(audi1);
        Car audi2 = carService.create(new Car("Q5", audi, List.of(bob)));
        List<Car> carsByBob = carService.getAllByDriver(bob.getId());
        System.out.println(carsByBob.toString());
        List<Car> cars = carService.getAll();
        System.out.println(cars.toString());
    }
}
