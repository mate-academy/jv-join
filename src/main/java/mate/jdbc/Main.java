package mate.jdbc;

import java.util.List;
import java.util.Random;
import mate.jdbc.lib.Injector;
import mate.jdbc.model.Car;
import mate.jdbc.model.Driver;
import mate.jdbc.model.Manufacturer;
import mate.jdbc.service.CarService;
import mate.jdbc.service.DriverService;
import mate.jdbc.service.ManufacturerService;

public class Main {
    private static final Injector INJECTOR = Injector.getInstance("mate.jdbc");

    public static void main(String[] args) {
        ManufacturerService manufacturerService = (ManufacturerService)
                INJECTOR.getInstance(ManufacturerService.class);
        DriverService driverService = (DriverService) INJECTOR.getInstance(DriverService.class);

        Driver petro = new Driver("Petro", generateRandomLicensePlate());
        Driver ivan = new Driver("Ivan", generateRandomLicensePlate());
        System.out.println(driverService.create(petro));
        System.out.println(driverService.create(ivan));

        Manufacturer manufacturerFerrari = new Manufacturer("Ferrari", "Italy");
        Manufacturer manufacturer = manufacturerService.create(manufacturerFerrari);

        List<Driver> drivers = driverService.getAll();
        drivers.add(ivan);
        drivers.add(petro);

        CarService carService = (CarService) INJECTOR.getInstance(CarService.class);
        Car liftBack = new Car("liftBack", manufacturer, drivers);
        Car sedan = new Car("sedan", manufacturer, drivers);

        carService.create(liftBack);
        carService.create(sedan);

        System.out.println(carService.get(liftBack.getId()));

        System.out.println(carService.update(new Car(liftBack.getId(),
                "liftBack2", manufacturer, drivers)));

        carService.removeDriverFromCar(ivan, liftBack);

        System.out.println("all driver: ");
        carService.getAll().forEach(System.out::println);

        List<Car> allDrivers = carService.getAllByDriver(petro.getId());
        allDrivers.forEach(System.out::println);

        System.out.println(carService.delete(liftBack.getId()));
    }

    public static String generateRandomLicensePlate() {
        StringBuilder sb = new StringBuilder();
        Random random = new Random();
        String letters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        String numbers = "0123456789";
        sb.append(letters.charAt(random.nextInt(letters.length())));
        sb.append(letters.charAt(random.nextInt(letters.length())));
        for (int i = 0; i < 4; i++) {
            sb.append(numbers.charAt(random.nextInt(numbers.length())));
        }
        sb.append(letters.charAt(random.nextInt(letters.length())));
        sb.append(letters.charAt(random.nextInt(letters.length())));
        return sb.toString();
        // this method was made for easier and more convenient testing
    }
}
