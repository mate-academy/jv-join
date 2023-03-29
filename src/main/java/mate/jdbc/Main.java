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
    private static final CarService carService = (CarService)
            injector.getInstance(CarService.class);
    private static final ManufacturerService manufacturerService = (ManufacturerService)
            injector.getInstance(ManufacturerService.class);
    private static final DriverService driverService = (DriverService)
            injector.getInstance(DriverService.class);

    public static void main(String[] args) {
        Manufacturer bmwManufacturer = new Manufacturer("BMW", "Germany");
        Manufacturer toyotaManufacturer = new Manufacturer("Toyota", "Japan");
        Manufacturer volvoManufacturer = new Manufacturer("Volvo","Sweden");
        Manufacturer fordManufacturer = new Manufacturer("Ford","USA");
        List<Manufacturer> manufacturerList =
                List.of(bmwManufacturer, toyotaManufacturer, volvoManufacturer, fordManufacturer);
        for (Manufacturer manufacturer : manufacturerList) {
            System.out.println(manufacturerService.create(manufacturer));
        }

        Driver taras = new Driver("Taras", "11111111");
        Driver petro = new Driver("petro", "22222222");
        Driver ivan = new Driver("ivan", "33333333");
        List<Driver> driversList = List.of(taras, petro, ivan);
        for (Driver driver : driversList) {
            System.out.println(driverService.create(driver));
        }

        System.out.println("\nTest creating new car");
        Car lexus = new Car("Lexus", toyotaManufacturer);
        Car fiesta = new Car("Ford", fordManufacturer);
        lexus.setDrivers(driversList);
        System.out.println(carService.create(lexus));
        System.out.println(carService.create(fiesta));

        Long carId = lexus.getId();
        System.out.println("Test getting car from database");
        System.out.println(carService.get(carId));

        System.out.println("Test getting all cars from database");
        System.out.println(carService.getAll());

        fiesta.setManufacturer(volvoManufacturer);
        fiesta.setModel("restyle");
        System.out.println("Test updating car");
        System.out.println("return = " + carService.update(fiesta));

        System.out.println("Test deleting car");
        carService.delete(carId);
        System.out.println(carService.getAll());

        Long driverId = petro.getId();
        System.out.println("Test adding driver to car");
        carService.addDriverToCar(driverService.get(driverId), carService.get(carId));
        System.out.println(carService.get(carId));

        System.out.println("Test deleting driver from car");
        carService.removeDriverFromCar(driverService.get(driverId), carService.get(carId));
        System.out.println(carService.get(carId));
    }
}
