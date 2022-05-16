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
        ManufacturerService manufacturerService = (ManufacturerService)
                injector.getInstance(ManufacturerService.class);
        Manufacturer toyotaManufacturer = new Manufacturer("Toyota", "Japan");
        manufacturerService.create(toyotaManufacturer);
        Manufacturer fordManufacturer = new Manufacturer("Ford", "USA");
        manufacturerService.create(fordManufacturer);

        DriverService driverService = (DriverService)
                injector.getInstance(DriverService.class);
        Driver driverStepan = new Driver("Stepan", "DIE657086");
        driverStepan = driverService.create(driverStepan);
        Driver driverIvan = new Driver("Ivan", "YDU965147");
        driverIvan = driverService.create(driverIvan);
        Driver driverBohdan = new Driver("Bohdan", "DFW953475");
        driverBohdan = driverService.create(driverBohdan);
        Driver driverPetro = new Driver("Petro", "TYU584136");
        driverBohdan = driverService.create(driverPetro);

        List<Driver> toyotaDrivers = new ArrayList<>();
        toyotaDrivers.add(driverStepan);
        toyotaDrivers.add(driverIvan);
        List<Driver> fordDrivers = new ArrayList<>();
        fordDrivers.add(driverBohdan);
        fordDrivers.add(driverPetro);

        CarService carService = (CarService)
                injector.getInstance(CarService.class);
        Car toyotaCar = new Car("Camry", toyotaManufacturer, toyotaDrivers);
        Car fordCar = new Car("Focus", fordManufacturer, fordDrivers);
        toyotaCar = carService.create(toyotaCar);
        fordCar = carService.create(fordCar);
        carService.getAll().forEach(System.out::println);
        driverService.delete(driverIvan.getId());
        carService.getAll().forEach(System.out::println);

        System.out.println(carService.get(fordCar.getId()));

        driverStepan.setLicenseNumber("VBN112233");

        System.out.println(driverService.update(driverStepan));

        Driver driverJessey = new Driver("Jessey", "LKJ135317");
        driverJessey = driverService.create(driverJessey);
        carService.addDriverToCar(driverJessey, toyotaCar);
        carService.getAll().forEach(System.out::println);

        carService.removeDriverFromCar(driverStepan, toyotaCar);
        carService.getAll().forEach(System.out::println);

        carService.getAllByDriver(driverBohdan.getId()).forEach(System.out::println);
    }
}
