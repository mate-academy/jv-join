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
    private static final Injector injector = Injector.getInstance("mate.jdbc");

    public static void main(String[] args) {
        ManufacturerService manufacturerService =
                (ManufacturerService) injector.getInstance(ManufacturerService.class);

        Manufacturer manufacturerAudi = createRecordManufacturer(
                manufacturerService, "Audi", "Germany");
        Manufacturer manufacturerVW = createRecordManufacturer(
                manufacturerService, "Volkswagen", "Germany");

        DriverService driverService =
                (DriverService) injector.getInstance(DriverService.class);

        Driver driverBob = createRecordDriver(
                driverService, "Bob", "123456");
        Driver driverAlice = createRecordDriver(
                driverService, "Alice", "765432");
        Driver driverNick = createRecordDriver(
                driverService, "Nick", "356894");

        CarService carService =
                (CarService) injector.getInstance(CarService.class);

        Car carAudiA4 = createRecordCar(carService, "A4",
                manufacturerAudi, List.of(driverBob));
        System.out.println(carAudiA4);
        Car carVwGolf8 = createRecordCar(carService, "Golf8",
                manufacturerVW, List.of(driverAlice, driverNick));
        System.out.println(carVwGolf8);

        Manufacturer manufacturerChrysler = createRecordManufacturer(
                manufacturerService, "Chrysler", "USA");
        carAudiA4.setModel("C200");
        carAudiA4.setManufacturer(manufacturerChrysler);
        Driver driverNancy = createRecordDriver(
                driverService, "Nancy", "987562");
        carAudiA4.setDrivers(List.of(driverNick, driverNancy));
        Car carChryslerC200 = carService.update(carAudiA4);
        System.out.println(carChryslerC200);

        Car copyCarVwGolf8 = carService.get(carVwGolf8.getId());
        System.out.println(copyCarVwGolf8);

        Car carAudiA6 = createRecordCar(carService, "A6",
                manufacturerAudi, List.of(driverBob, driverNancy));
        carService.delete(carAudiA6.getId());

        List<Car> carList = carService.getAll();
        System.out.println(carList);

        Driver driverVadim = createRecordDriver(
                driverService, "Vadim", "568723");

        carService.addDriverToCar(driverVadim, carChryslerC200);
        System.out.println(carService.get(carChryslerC200.getId()));

        carService.removeDriverFromCar(driverVadim, carChryslerC200);
        System.out.println(carService.get(carChryslerC200.getId()));

        List<Car> newCarList = carService.getAllByDriver(driverNancy.getId());
        System.out.println(newCarList);
    }

    private static Manufacturer createRecordManufacturer(
            ManufacturerService manufacturerService, String name, String country) {
        Manufacturer manufacturer = new Manufacturer(name, country);
        return manufacturerService.create(manufacturer);
    }

    private static Driver createRecordDriver(
            DriverService driverService, String name, String licenseNumber) {
        Driver driver = new Driver(name, licenseNumber);
        return driverService.create(driver);
    }

    private static Car createRecordCar(
            CarService carService, String model, Manufacturer manufacturer,
            List<Driver> driverList) {
        Car car = new Car(model, manufacturer, driverList);
        return carService.create(car);
    }
}
