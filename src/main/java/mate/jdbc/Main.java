package mate.jdbc;

import mate.jdbc.lib.Injector;
import mate.jdbc.model.Car;
import mate.jdbc.model.Driver;
import mate.jdbc.service.CarService;
import mate.jdbc.service.DriverService;

public class Main {
    private static final Injector INJECTOR = Injector.getInstance("mate.jdbc");

    public static void main(String[] args) {
        CarService carService = (CarService) INJECTOR.getInstance(CarService.class);
        DriverService driverService = (DriverService) INJECTOR.getInstance(DriverService.class);
//        Driver driver = new Driver();
//        driver.setName("Mykola");
//        driver.setLicenseNumber("777777");
//        driverService.create(driver);

//        Driver driver = driverService.get(1L);
        Car car = carService.get(1L);
        Driver driver = driverService.get(3L);
        carService.addDriverToCar(driver, car);
        System.out.println(carService.getAllByDriver(3L));
//        carService.removeDriverFromCar(driver, car);
//        System.out.println(car);
//        System.out.println(carService.getAll());
    }
}
