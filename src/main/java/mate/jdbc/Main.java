package mate.jdbc;

import java.util.List;
import mate.jdbc.dao.ManufacturerDao;
import mate.jdbc.lib.Injector;
import mate.jdbc.model.Car;
import mate.jdbc.model.Driver;
import mate.jdbc.model.Manufacturer;
import mate.jdbc.service.CarService;
import mate.jdbc.service.DriverService;

public class Main {
    public static void main(String[] args) {
        Injector injector = Injector.getInstance("mate.jdbc");

        System.out.println("*** Step 1. Manufacturers ");
        ManufacturerDao manufacturerDao
                = (ManufacturerDao) injector.getInstance(ManufacturerDao.class);
        Manufacturer opel = new Manufacturer(null, "Opel", "Germany");
        opel = manufacturerDao.create(opel);
        System.out.println(opel);
        Manufacturer mazda = new Manufacturer(null, "Mazda", "Japan");
        mazda = manufacturerDao.create(mazda);
        System.out.println(mazda);
        Manufacturer jac = new Manufacturer(null, "Jac", "China");
        jac = manufacturerDao.create(jac);
        System.out.println(jac);

        System.out.println("*** Step 2. Drivers ");
        DriverService driverService = (DriverService) injector.getInstance(DriverService.class);
        Driver martin = new Driver(null, "Frank Martin", "BD2 MGF 06");
        martin = driverService.create(martin);
        System.out.println(martin);
        Driver morales = new Driver(null, "Daniel Morales", "724 LNB 13");
        morales = driverService.create(morales);
        System.out.println(morales);
        Driver torreto = new Driver(null, "Dominic Toretto", "JSM 586");
        torreto = driverService.create(torreto);
        System.out.println(torreto);

        System.out.println("*** Step 3. Cars ");
        CarService carsService = (CarService) injector.getInstance(CarService.class);
        Car carFirst = new Car(null, opel, "Opel Vectra", null);
        carFirst = carsService.create(carFirst);
        System.out.println(carFirst);
        Car carSecond = new Car(null, mazda, "Mazda V6", null);
        carSecond = carsService.create(carSecond);
        System.out.println(carSecond);
        Car carThird = new Car(null, jac, "Jac J7", null);
        carThird = carsService.create(carThird);
        System.out.println(carThird);

        System.out.println("*** Step 4. Add links car - driver");
        carsService.addDriverToCar(martin, carFirst);
        carsService.addDriverToCar(martin, carSecond);
        carsService.addDriverToCar(martin, carThird);
        List<Car> allByMartin = carsService.getAllByDriver(martin.getId());
        for (Car car: allByMartin) {
            System.out.println(car);
        }

        System.out.println("*** Step 5. Remove one link");
        carsService.removeDriverFromCar(martin, carThird);
        allByMartin = carsService.getAllByDriver(martin.getId());
        for (Car car: allByMartin) {
            System.out.println(car);
        }
    }
}
