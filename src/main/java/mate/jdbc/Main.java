package mate.jdbc;

import java.util.ArrayList;
import java.util.List;
import mate.jdbc.dao.ManufacturerDao;
import mate.jdbc.lib.Injector;
import mate.jdbc.model.Car;
import mate.jdbc.model.Driver;
import mate.jdbc.model.Manufacturer;
import mate.jdbc.service.CarService;
import mate.jdbc.service.DriverService;

public class Main {
    private static final Injector injector = Injector.getInstance("mate.jdbc");

    public static void main(String[] args) {
        ManufacturerDao manufacturerDao =
                (ManufacturerDao) injector.getInstance(ManufacturerDao.class);
        Manufacturer nissan = new Manufacturer("Nissan","Japan");
        manufacturerDao.create(nissan);

        Manufacturer porsche = new Manufacturer("Porsche","Germany");
        manufacturerDao.create(porsche);

        Manufacturer audi = new Manufacturer("Audi","Germany");
        manufacturerDao.create(audi);

        manufacturerDao.getAll().forEach(System.out::println);
        System.out.println("---------------------------- Create cars" + System.lineSeparator());

        DriverService driverService = (DriverService) injector.getInstance(DriverService.class);

        List<Driver> nissanDriver = new ArrayList<>();
        Driver rostyslav = new Driver("Rostyslav","95A555999");
        driverService.create(rostyslav);
        nissanDriver.add(rostyslav);

        List<Driver> porscheDriver = new ArrayList<>();
        Driver vadym = new Driver("Vadym","85B255999");
        driverService.create(vadym);
        porscheDriver.add(vadym);

        List<Driver> audiDriver = new ArrayList<>();
        Driver valentyn = new Driver("Valentyn","87R455999");
        driverService.create(valentyn);
        audiDriver.add(valentyn);

        driverService.getAll().forEach(System.out::println);
        System.out.println("---------------------------- Create driver" + System.lineSeparator());

        CarService carService = (CarService) injector.getInstance(CarService.class);

        Car nissanCar = new Car("Nissan Z",nissan,nissanDriver);
        carService.create(nissanCar);
        Car porscheCar = new Car("Taycan",porsche,porscheDriver);
        carService.create(porscheCar);
        Car audiCar = new Car("TT RS",audi,audiDriver);
        carService.create(audiCar);

        carService.getAll().forEach(System.out::println);

        carService.addDriverToCar(vadym,nissanCar);

        System.out.println(carService.get(nissanCar.getId()));
    }
}
