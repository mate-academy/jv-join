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
    private static Injector injector;
    private static ManufacturerService manufacturerService;
    private static DriverService driverService;
    private static CarService carService;

    public static void main(String[] args) {
        injector = Injector.getInstance("mate.jdbc");
        manufacturerService = (ManufacturerService) injector
                .getInstance(ManufacturerService.class);
        driverService = (DriverService) injector.getInstance(DriverService.class);
        carService = (CarService) injector.getInstance(CarService.class);

        Manufacturer manufacturer01 = new Manufacturer("BMW", "Germany");
        manufacturer01 = manufacturerService.create(manufacturer01);
        Manufacturer manufacturer02 = new Manufacturer("Mitsubishi", "Japan");
        manufacturer02 = manufacturerService.create(manufacturer02);
        Manufacturer manufacturer03 = new Manufacturer("Honda", "Japan");
        manufacturer03 = manufacturerService.create(manufacturer03);
        Manufacturer manufacturer04 = new Manufacturer("Citroen", "France");
        manufacturer04 = manufacturerService.create(manufacturer04);

        Driver driver01 = new Driver("Vladyslav","BB-1243");
        driver01 = driverService.create(driver01);
        Driver driver02 = new Driver("Pasha","SS-2442");
        driver02 = driverService.create(driver02);
        Driver driver03 = new Driver("Gosha","SA-3334");
        driver03 = driverService.create(driver03);
        Driver driver04 = new Driver("Max","AA-1243");
        driver04 = driverService.create(driver04);

        Car car01 = new Car("Pajero001", manufacturer02);
        Car car02 = new Car("Pajero002", manufacturer02);
        Car car03 = new Car("Cactus001", manufacturer04);
        car01 = carService.create(car01);
        car02 = carService.create(car02);
        car03 = carService.create(car03);
        car01.getDrivers().clear();
        car02.getDrivers().clear();
        car03.getDrivers().clear();
        carService.update(car01);
        carService.update(car02);
        carService.update(car03);
        System.out.println("Cars without Drivers");
        System.out.println(car01);
        System.out.println(car02);
        System.out.println(car03);
        System.out.println();

        carService.addDriverToCar(driver01, car01);
        carService.addDriverToCar(driver01, car02);
        carService.addDriverToCar(driver02, car01);

        car01 = carService.get(car01.getId());
        car02 = carService.get(car02.getId());
        System.out.println("Cars with Drivers");
        System.out.println(car01);
        System.out.println(car02);
        System.out.println(car03);
        System.out.println();

        List<Car> carsWithDriver = carService.getAllByDriver(driver01.getId());
        System.out.println("All cars with driver " + driver01);
        for (Car car : carsWithDriver) {
            System.out.println(car);
        }
        System.out.println();

        carService.removeDriverFromCar(driver01, car01);
        car01 = carService.get(car01.getId());
        System.out.println(car01 + System.lineSeparator()
                + "without driver " + driver01);
    }
}
