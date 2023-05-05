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
        CarService carService =
                (CarService) injector.getInstance(CarService.class);
        DriverService driverService =
                (DriverService) injector.getInstance(DriverService.class);
        ManufacturerService manufacturerService =
                (ManufacturerService) injector.getInstance(ManufacturerService.class);

        List<Driver> allDrivers = driverService.getAll();
        List<Manufacturer> allManufacturers = manufacturerService.getAll();

        List<Driver> driversForFirstCar = new ArrayList<>();
        driversForFirstCar.add(allDrivers.get(0));
        Car car1 = carService.create(new Car(1L, "MRS1",
                allManufacturers.get(0), driversForFirstCar));
        System.out.println("First car created: "
                + System.lineSeparator() + car1);


        List<Driver> driversForSecondCar = new ArrayList<>();
        driversForSecondCar.add(allDrivers.get(0));
        driversForSecondCar.add(allDrivers.get(2));
        Car car2 = carService.create(new Car(2L, "BMW1",
                allManufacturers.get(3), driversForSecondCar));
        System.out.println("Second car created: "
                + System.lineSeparator() + car2);

        List<Driver> driversForThirdCar = new ArrayList<>();
        driversForThirdCar.add(allDrivers.get(0));
        Car car3 = carService.create(new Car(3L, "MTS1",
                allManufacturers.get(1), driversForThirdCar));
        System.out.println("Third car created: "
                + System.lineSeparator() + car3);

        System.out.println("Get second car from DB: "
                + System.lineSeparator() + carService.get(2L));

        List<Car> cars = carService.getAll();
        System.out.println("Get all cars from DB: ");
        cars.forEach(System.out::println);

        car1.setModel("MRS2");
        System.out.println("Update first car: "
                + System.lineSeparator() + carService.update(car1));

        System.out.println("Delete third car: "
                + System.lineSeparator() + carService.delete(car3.getId()));

        carService.addDriverToCar(allDrivers.get(2), car1);
        System.out.println("Add driver to first car: "
                + System.lineSeparator() + car1);

        carService.removeDriverFromCar(allDrivers.get(1), car1);
        System.out.println("Remove driver from first car: "
                + System.lineSeparator() + car1);

        List<Car> allCarsByDriver = carService.getAllByDriver(3L);
        System.out.println("Get all cars by driver: " + allDrivers.get(2));
        allCarsByDriver.forEach(System.out::println);
    }
}
