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

    public static void main(String[] args) {
        ManufacturerService manufacturerService = (ManufacturerService) injector
                .getInstance(ManufacturerService.class);
        Manufacturer manufacturer1 = new Manufacturer("BMW", "Germany");
        manufacturerService.create(manufacturer1);
        Manufacturer manufacturer2 = new Manufacturer("Ford", "USA");
        manufacturerService.create(manufacturer2);

        DriverService driverService = (DriverService) injector
                .getInstance(DriverService.class);
        Driver driver1 = new Driver("Bottas", "5");
        driverService.create(driver1);
        Driver driver2 = new Driver("Fettel", "7");
        driverService.create(driver2);
        Driver driver3 = new Driver("Verstapen", "9");
        driverService.create(driver3);
        Driver driver4 = new Driver("Sainz", "3");
        driverService.create(driver4);

        List<Driver> driverList1 = new ArrayList<>();
        driverList1.add(driver1);
        driverList1.add(driver2);
        List<Driver> driverList2 = new ArrayList<>();
        driverList2.add(driver3);
        CarService carService = (CarService) injector
                .getInstance(CarService.class);
        Car car1 = new Car("M5", manufacturer1, driverList1);
        Car car2 = new Car("Kuga", manufacturer2, driverList1);
        Car car3 = new Car("X5", manufacturer1, driverList2);
        Car car4 = new Car("Ka", manufacturer2, driverList2);
        carService.create(car1);
        carService.create(car2);
        carService.create(car3);
        carService.create(car4);
        carService.get(car1.getId());
        System.out.println(carService.getAll().size());
        car1.setModel("M3");
        car1.setDrivers(driverList2);
        carService.update(car1);
        System.out.println(carService.get(car1.getId()));
        carService.addDriverToCar(driver4, car1);
        carService.addDriverToCar(driver4, car2);
        System.out.println(carService.getAllByDriver(driver4.getId()));
        carService.removeDriverFromCar(driver1, car2);
        System.out.println(carService.get(car2.getId()));
        carService.delete(car1.getId());
        System.out.println(carService.getAll().size());
    }
}
