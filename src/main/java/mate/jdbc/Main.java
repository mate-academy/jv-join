package mate.jdbc;

import mate.jdbc.lib.Injector;
import mate.jdbc.service.CarService;
import mate.jdbc.service.DriverService;
import mate.jdbc.service.ManufacturerService;

public class Main {
    private static Injector injector = Injector.getInstance("mate.jdbc");

    public static void main(String[] args) {
        CarService carService = (CarService) injector.getInstance(CarService.class);
        ManufacturerService manufacturerService =
                (ManufacturerService) injector.getInstance(ManufacturerService.class);
        DriverService driverService = (DriverService) injector.getInstance(DriverService.class);

        /*
        System.out.println(carService.get(30L));
        carService.delete(2L);

        List<Car> all = carService.getAll();
        for (Car car : all) {
            System.out.println(car);
        }
        Driver driverEmily = new Driver();
        driverEmily.setId(6L);
        driverEmily.setName("Emily");
        driverEmily.setLicenseNumber("1225521");
        System.out.println(driverService.create(driverEmily));

        Driver driverLili = new Driver();
        driverLili.setId(7L);
        driverLili.setName("Lili");
        driverLili.setLicenseNumber("8452367");
        System.out.println(driverService.create(driverLili));

        List<Driver> drivers = new ArrayList<>();
        drivers.add(driverService.get(6L));
        drivers.add(driverService.get(7L));

        Manufacturer manufacturer = manufacturerService.get(14L);
        System.out.println(manufacturer);

        Car car = new Car();
        car.setId(2L);
        car.setModel("A3");
        car.setManufacturer(manufacturer);
        car.setDrivers(drivers);
        Car update = carService.update(car);
        System.out.println(update);

        List<Car> allByDriver = carService.getAllByDriver(5L);
        for (Car car : allByDriver) {
            System.out.println(car);
        }

        Car car = carService.get(2L);
        Driver driver = driverService.get(1L);
        carService.addDriverToCar(driver, car);

        Car car = carService.get(2L);
        Driver driver = driverService.get(1L);
        carService.removeDriverFromCar(driver, car);
        */
    }
}
