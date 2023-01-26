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
    private static Injector injector = Injector.getInstance("mate.jdbc");

    public static void main(String[] args) {
        ManufacturerService manufacturerService =
                (ManufacturerService) injector.getInstance(ManufacturerService.class);
        Manufacturer audi = new Manufacturer("AUDI", "Germany");
        Manufacturer bmw = new Manufacturer("BMW", "Germany");
        DriverService driverService = (DriverService) injector.getInstance(DriverService.class);
        manufacturerService.create(audi);
        manufacturerService.create(bmw);
        Driver driverOne = new Driver("driver1", "KA0001");
        Driver driverTwo = new Driver("driver2", "KD0002");
        Driver driverThree = new Driver("driver3", "KC0003");
        driverService.create(driverOne);
        driverService.create(driverTwo);
        driverService.create(driverThree);
        Driver driverFour = new Driver("driver4", "KH0004");
        driverService.create(driverFour);
        System.out.println("Added new driver and manufacturer");
        CarService carService = (CarService) injector.getInstance(CarService.class);
        Car audiA3 = new Car("A3", audi, List.of(driverTwo, driverThree));
        Car bmwM5 = new Car("M5", bmw, List.of(driverOne, driverTwo));
        Car audiQ7 = new Car("Q7", audi, List.of(driverOne, driverThree));
        carService.create(audiQ7);
        carService.create(bmwM5);
        carService.create(audiA3);
        System.out.println("Added new car to DB");
        System.out.println("Get car by id: " + carService.get(audiQ7.getId()));
        System.out.println("Get all element: " + carService.getAll());
        System.out.println("Get all by drivers: " + carService.getAllByDriver(driverOne.getId()));
        audiQ7.setModel("upgradeQ7");
        audiQ7.setDrivers(List.of(driverTwo));
        System.out.println(carService.update(audiQ7));
        System.out.println(carService.delete(audiA3.getId()));
        carService.addDriverToCar(driverFour, bmwM5);
        System.out.println(bmwM5);
        carService.removeDriverFromCar(driverTwo, bmwM5);
        System.out.println(bmwM5);
    }
}
