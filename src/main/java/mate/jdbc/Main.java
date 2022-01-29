package mate.jdbc;

import mate.jdbc.dao.CarDao;
import mate.jdbc.lib.Injector;
import mate.jdbc.service.CarService;
import mate.jdbc.service.DriverService;

public class Main {
    private static final Injector injector = Injector.getInstance("mate.jdbc");

    public static void main(String[] args) {
        // test your code here
        DriverService driverService = (DriverService) injector.getInstance(DriverService.class);
        CarDao carDao = (CarDao) injector.getInstance(CarDao.class);
        CarService carService = (CarService) injector.getInstance(CarService.class);
        /*Manufacturer manufacturer = new Manufacturer();
        manufacturer.setName("Honda");
        manufacturer.setCountry("Japan");
        manufacturer.setId(1L);
        Car car = new Car();
        car.setModel("Odyssey");
        car.setId(5L);
        car.setManufacturer(manufacturer);
        List<Driver> drivers = new ArrayList<>();
        Driver driver = new Driver();
        driver.setName("Rocket Jet");
        driver.setLicenseNumber("00000005");
        driver.setId(5L);
        drivers.add(driver);
        car.setDrivers(drivers);
        carDao.update(car);
        driverService.create(driver);
        carDao.create(car);
        carDao.delete(5L);
        System.out.println(carDao.get(4L).getDrivers());
        System.out.println(carDao.get(2L).getModel());
        System.out.println(carDao.get(2L)
                .getDrivers().stream()
                .map(Driver::getName)
                .collect(Collectors.toList()));
        System.out.println(carDao.getAll()
                .stream()
                .map(Car::getManufacturer)
                .map(Manufacturer::getName)
                .collect(Collectors.toList()));
        System.out.println(carDao.get(5L).getDrivers());
        System.out.println(carDao.getAllByDriver(1L)
                .stream()
                .map(Car::getManufacturer)
                .map(Manufacturer::getName)
                .collect(Collectors.toList()));
        carService.addDriverToCar(driver,car);
        carService.removeDriverFromCar(driver,car);*/
    }
}
