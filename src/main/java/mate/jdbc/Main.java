package mate.jdbc;

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
        DriverService driverService = (DriverService) injector.getInstance(DriverService.class);
        CarService carService = (CarService) injector.getInstance(CarService.class);
        ManufacturerService manufacturerService =
                (ManufacturerService) injector.getInstance(ManufacturerService.class);

        Manufacturer mercedes = new Manufacturer(4L, "mercedes", "Germany");
        Manufacturer peugeot = new Manufacturer(5L, "peugeot", "France");
        manufacturerService.create(peugeot);
        carService.create(new Car(5L, "Class B", mercedes));
        carService.update(new Car(3L, "Focus", carService.get(3l).getManufacturer()));
        carService.getAllByDriver(3L).forEach(System.out::println);
        carService.delete(4L);
        Car classB = carService.get(7L);
        Driver misha = new Driver(1L, "Misha", "778899");
        Driver petya = new Driver(2L, "Petya", "558833");
        driverService.create(misha);
        driverService.create(petya);
        carService.addDriverToCar(misha, classB);
        carService.create(new Car("407", peugeot));
        Driver kolya = driverService.get(3L);
        Car m5 = carService.get(2L);
        carService.removeDriverFromCar(kolya, m5);
        carService.getAll().forEach(System.out::println);
    }
}
