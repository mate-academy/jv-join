package mate.jdbc;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
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
        //init dao
        ManufacturerService manufacturerService
                = (ManufacturerService) injector.getInstance(ManufacturerService.class);
        CarService carService = (CarService) injector.getInstance(CarService.class);
        DriverService driverService = (DriverService) injector.getInstance(DriverService.class);

        Manufacturer testManufacturer
                = new Manufacturer("Cockroach_Industries", "China");
        testManufacturer = manufacturerService.create(testManufacturer);
        List<Car> carList = new ArrayList<>();
        List<Driver> drivers = generateDrivers(3, "Li", driverService);
        //test create method
        for (int i = 1; i <= 3; i++) {
            Car car = new Car("modelName", testManufacturer, drivers);
            car = carService.create(car);
            assert car.getId() != null;
            carList.add(car);
        }
        //test get method
        Car receivedCar = carService.get(carList.get(0).getId());
        System.out.println("Previously added car:\n" + receivedCar);
        //test getAll method
        List<Car> allCars = carService.getAll();
        System.out.println("All cars:\n" + allCars);
        //test update method
        receivedCar.setModel("LolabayModel");
        Long carIdBeforeUpdate = receivedCar.getId();
        carService.update(receivedCar);
        Car updatedCar = carService.get(carIdBeforeUpdate);
        assert updatedCar.getModel().equals("LolabayModel");
        System.out.println("Updated car:\n" + updatedCar);
        //test delete method
        carService.delete(updatedCar.getId());
        System.out.println("Cars after delete method:\n" + carService.getAll());
        //test add driver method
        Driver spyDriver
                = driverService.create(new Driver("ThisIsASpy!", "0000"));
        Car spyCar = carService.getAll().get(0);
        carService.addDriverToCar(spyDriver, spyCar);
        System.out.println("Car state after add driver method:\n"
                + carService.get(spyCar.getId()));
        //test remove driver method
        carService.removeDriverFromCar(spyDriver, spyCar);
        System.out.println("Car state after remove driver method:\n"
                + carService.get(spyCar.getId()));
        //test getAllByDriver method
        assert carService.getAllByDriver(spyDriver.getId()).size() == 0;
        List<Car> sam1Cars = carService.getAllByDriver(drivers.get(0).getId());
        System.out.println("Cars of " + drivers.get(0).getName() + " driver:" + sam1Cars);
    }

    private static List<Driver> generateDrivers(int amount, String name, DriverService service) {
        ArrayList<Driver> drivers = new ArrayList<>();
        for (int i = 1; i <= amount; i++) {
            Driver driver = new Driver(name + i,
                    "" + Objects.hash(name + i));
            drivers.add(service.create(driver));
        }
        return drivers;
    }
}
