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
        ManufacturerService mnfService = (ManufacturerService)
                injector.getInstance(ManufacturerService.class);
        DriverService drvService = (DriverService)
                injector.getInstance(DriverService.class);

        Manufacturer manufacturer1 = mnfService.get(1L);
        Driver driver1 = drvService.get(1L);
        Driver driver2 = drvService.get(2L);
        List<Driver> drivers1 = new ArrayList<>();
        drivers1.add(driver1);
        drivers1.add(driver2);
        Car car1 = new Car();
        car1.setManufacturer(manufacturer1);
        car1.setModel("80");
        car1.setDrivers(drivers1);

        Manufacturer manufacturer2 = mnfService.get(2L);
        Car car2 = new Car();
        car2.setManufacturer(manufacturer2);
        car2.setModel("1844");
        Driver driver3 = drvService.get(3L);
        List<Driver> drivers2 = new ArrayList<>();
        drivers2.add(driver2);
        drivers2.add(driver3);
        car2.setDrivers(drivers2);

        CarService carService = (CarService) injector.getInstance(CarService.class);

        car2 = carService.create(car2);
        System.out.println(carService.get(car2.getId()));

        Manufacturer manufacturer3 = mnfService.get(3L);
        car2.setManufacturer(manufacturer3);
        car2.setDrivers(drivers1);
        carService.update(car2);
        System.out.println(carService.get(car2.getId()));

        Driver driver4 = drvService.get(4L);
        carService.addDriverToCar(driver4, car2);
        System.out.println(carService.get(car2.getId()));

        carService.removeDriverFromCar(driver1, car2);
        System.out.println(carService.get(car2.getId()));

        carService.delete(car2.getId());

        System.out.println(carService.getAllByDriver(3L));
    }
}
