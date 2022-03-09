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
    private static final DriverService driverService =
            (DriverService) injector.getInstance(DriverService.class);
    private static final ManufacturerService manufacturerService =
            (ManufacturerService) injector.getInstance(ManufacturerService.class);
    private static final CarService carService =
            (CarService) injector.getInstance(CarService.class);

    public static void main(String[] args) {
        initDriversToDB();
        initManufacturersToDB();
        initCarsToDB();
        System.out.println(carService.get(2L));
        carService.getAll().forEach(System.out::println);
        System.out.println(carService.update(getCarByIndex(2)));
        System.out.println(carService.delete(1L));
        carService.getAllByDriver(5L).forEach(System.out::println);
        carService.addDriverToCar(getDriverByIndex(0), getCarByIndex(2));
        carService.removeDriverFromCar(getDriverByIndex(3), getCarByIndex(2));
    }

    private static void initCarsToDB() {
        carService.create(getCarByIndex(0));
        carService.create(getCarByIndex(1));
    }

    private static void initManufacturersToDB() {
        manufacturerService.create(getManufacturerByIndex(0));
        manufacturerService.create(getManufacturerByIndex(1));
        manufacturerService.create(getManufacturerByIndex(2));
        manufacturerService.create(getManufacturerByIndex(3));
        manufacturerService.create(getManufacturerByIndex(4));
    }

    private static void initDriversToDB() {
        driverService.create(getDriverByIndex(0));
        driverService.create(getDriverByIndex(1));
        driverService.create(getDriverByIndex(2));
        driverService.create(getDriverByIndex(3));
        driverService.create(getDriverByIndex(4));
    }

    private static Driver getDriverByIndex(int index) {
        List<Driver> drivers = new ArrayList<>();
        drivers.add(Driver.builder().id(1L).name("Bob").licenseNumber("8471-3461").build());
        drivers.add(Driver.builder().id(2L).name("Alice").licenseNumber("5745-6541").build());
        drivers.add(Driver.builder().id(3L).name("John").licenseNumber("7653-0814").build());
        drivers.add(Driver.builder().id(4L).name("Eric").licenseNumber("0961-1463").build());
        drivers.add(Driver.builder().id(5L).name("James").licenseNumber("0127-8122").build());
        return drivers.get(index);
    }

    private static Manufacturer getManufacturerByIndex(int index) {
        List<Manufacturer> manufacturers = new ArrayList<>();
        manufacturers.add(Manufacturer.builder().id(1L).name("ZAZ").country("Ukraine").build());
        manufacturers.add(Manufacturer.builder().id(2L).name("Toyota").country("Japan").build());
        manufacturers.add(Manufacturer.builder().id(3L).name("Skoda")
                .country("Czech Republic").build());
        manufacturers.add(Manufacturer.builder().id(4L).name("Lancia").country("Italy").build());
        manufacturers.add(Manufacturer.builder().id(5L).name("Lotus")
                .country("United Kingdom").build());
        return manufacturers.get(index);
    }

    private static Car getCarByIndex(int index) {
        List<Car> cars = new ArrayList<>();
        cars.add(Car.builder().id(1L).model("Zaporozhets")
                .manufacturer(getManufacturerByIndex(0))
                .drivers(List.of(getDriverByIndex(0))).build());
        cars.add(Car.builder().id(2L).model("Tacoma")
                .manufacturer(getManufacturerByIndex(1))
                .drivers(List.of(getDriverByIndex(0), getDriverByIndex(1))).build());
        cars.add(Car.builder().id(3L).model("Octavia")
                .manufacturer(getManufacturerByIndex(2))
                .drivers(List.of(getDriverByIndex(2), getDriverByIndex(3), getDriverByIndex(4)))
                .build());
        return cars.get(index);
    }
}
