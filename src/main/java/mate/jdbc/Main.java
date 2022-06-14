package mate.jdbc;

import java.util.List;
import mate.jdbc.lib.Injector;
import mate.jdbc.model.Car;
import mate.jdbc.service.CarService;
import mate.jdbc.service.DriverService;
import mate.jdbc.service.ManufacturerService;

public class Main {
    private static final Injector injector = Injector.getInstance("mate.jdbc");
    private static CarService carService;
    private static ManufacturerService manufacturerService;
    private static DriverService driverService;

    public static void main(String[] args) {

        implementServices();
        showAllCars();
        System.out.println("- - - - - - - - - - - - - - - - - - - - -");

        System.out.println(carService.get(1L));
        System.out.println("- - - - - - - - - - - - - - - - - - - - -");

        createCar(2L, "500");
        System.out.println("Some car was added:" + System.lineSeparator());
        showAllCars();
        System.out.println("- - - - - - - - - - - - - - - - - - - - -");
        System.out.println("Car with id 5 before update: " + carService.get(4L));
        updateCar(5L, 3L, "X7");
        System.out.println("- - - - - - - - - - - - - - - - - - - - -");
        showAllCars();
        System.out.println("- - - - - - - - - - - - - - - - - - - - -");

        carService.delete(7L);
        carService.delete(8L);
        System.out.println("Some cars were deleted:" + System.lineSeparator());
        showAllCars();
        System.out.println("- - - - - - - - - - - - - - - - - - - - -");

        carService.addDriverToCar(driverService.get(1L), carService.get(4L));
        carService.removeDriverFromCar(driverService.get(3L), carService.get(2L));
        System.out.println("Some changes with drivers:" + System.lineSeparator());
        showAllCars();
        System.out.println("- - - - - - - - - - - - - - - - - - - - -");

        System.out.println("Cars of driver with id 2:" + System.lineSeparator());
        List<Car> cars = carService.getAllByDriver(2L);
        cars.forEach(System.out::println);
        System.out.println("- - - - - - - - - - - - - - - - - - - - -");
    }

    private static void implementServices() {
        carService = (CarService) injector.getInstance(CarService.class);
        manufacturerService = (ManufacturerService)
                injector.getInstance(ManufacturerService.class);
        driverService = (DriverService) injector.getInstance(DriverService.class);
    }

    private static void createCar(Long manufacturerId, String model) {
        Car car = new Car();
        car.setManufacturer(manufacturerService.get(manufacturerId));
        car.setModel(model);
        carService.create(car);
    }

    private static void updateCar(Long id, Long manufacturerId, String model) {
        Car car = new Car(manufacturerService.get(manufacturerId), model);
        car.setId(id);
        carService.update(car);
        System.out.println("Car with id " + id + " after update: " + carService.get(id));
    }

    private static void showAllCars() {
        List<Car> cars = carService.getAll();
        cars.forEach(System.out::println);
    }
}
