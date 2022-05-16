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
    private static Injector injector = Injector.getInstance("mate.jdbc");

    public static void main(String[] args) {
        ManufacturerService manufacturerService = (ManufacturerService)
                injector.getInstance(ManufacturerService.class);
        Manufacturer audiManufacturer = new Manufacturer("Audi", "Germany");
        manufacturerService.create(audiManufacturer);
        Manufacturer volkswagenManufacturer = new Manufacturer("Volkswagen", "Germany");
        manufacturerService.create(volkswagenManufacturer);

        DriverService driverService = (DriverService) injector.getInstance(DriverService.class);
        Driver driverBob = new Driver("Bob", "12345");
        driverBob = driverService.create(driverBob);
        Driver driverAlice = new Driver("Alice", "123456");
        driverAlice = driverService.create(driverAlice);
        Driver driverRyan = new Driver("Ryan Gosling", "4433");
        driverRyan = driverService.create(driverRyan);
        Driver driverJessey = new Driver("Jessey", "13531");
        driverJessey = driverService.create(driverJessey);

        List<Driver> audiDrivers = new ArrayList<>();
        audiDrivers.add(driverBob);
        audiDrivers.add(driverAlice);
        List<Driver> volkswagenDrivers = new ArrayList<>();
        volkswagenDrivers.add(driverRyan);

        CarService carService = (CarService) injector.getInstance(CarService.class);
        Car audiCar = new Car("TT", audiManufacturer, audiDrivers);
        Car volkswagenCar = new Car("Golf", volkswagenManufacturer, volkswagenDrivers);
        audiCar = carService.create(audiCar);
        volkswagenCar = carService.create(volkswagenCar);
        List<Car> cars = carService.getAll();
        cars.forEach(System.out::println);

        driverService.delete(driverAlice.getId());
        carService.getAll().forEach(System.out::println);

        System.out.println(carService.get(volkswagenCar.getId()));

        driverBob.setLicenseNumber("155532");

        System.out.println(driverService.update(driverBob));

        carService.addDriverToCar(driverJessey, audiCar);
        carService.getAll().forEach(System.out::println);

        carService.removeDriverFromCar(driverBob, audiCar);
        carService.getAll().forEach(System.out::println);

        carService.getAllByDriver(driverRyan.getId()).forEach(System.out::println);

    }
}
