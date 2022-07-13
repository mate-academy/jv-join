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
        // test your code here
        ManufacturerService manufacturerService =
                (ManufacturerService) injector.getInstance(ManufacturerService.class);
        List<Manufacturer> manufacturers = new ArrayList<Manufacturer>();
        manufacturers.add(new Manufacturer("Mazda","Japan"));
        manufacturers.add(new Manufacturer("Honda","Japan"));
        manufacturers.add(new Manufacturer("Ford","USA"));
        for (Manufacturer manufacturer: manufacturers) {
            manufacturer = manufacturerService.create(manufacturer);
        }

        List<Manufacturer> manufacturerList = manufacturerService.getAll();
        for (Manufacturer manufacturerItem : manufacturerList) {
            System.out.println(manufacturerItem.toString());
        }

        DriverService driverService = (DriverService) injector.getInstance(DriverService.class);
        List<Driver> drivers = new ArrayList<Driver>();
        drivers.add(new Driver("Taras","00001"));
        drivers.add(new Driver("Vika","00002"));
        drivers.add(new Driver("John","00003"));
        drivers.add(new Driver("Olga","00004"));
        drivers.add(new Driver("Kyrylo","00005"));

        for (Driver driver : drivers) {
            driver = driverService.create(driver);
        }

        List<Driver> driverList = driverService.getAll();
        for (Driver driver : driverList) {
            System.out.println(driver.toString());
        }

        CarService carService = (CarService) injector.getInstance(CarService.class);
        List<Car> cars = new ArrayList<Car>();
        cars.add(new Car(manufacturers.get(0).getId(),"CX-9"));
        cars.add(new Car(manufacturers.get(1).getId(),"Accord"));
        cars.add(new Car(manufacturers.get(2).getId(),"Focus"));

        for (Car car : cars) {
            car = carService.create(car);
        }

        List<Car> carList = carService.getAll();
        for (Car carItem : carList) {
            System.out.println(carItem.toString());
        }

        for (int i = 0; i < driverList.size(); i++) {
            carService.addDriverToCar(driverList.get(i),carList.get(i % carList.size()));
        }

        for (Driver driver : driverList) {
            System.out.println("for " + driver.toString() + "assigned following cars");
            for (Car car : carService.getAllByDriver(driver.getId())) {
                System.out.println(car.toString());
            }

        }

    }
}
