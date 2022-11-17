package mate.jdbc;

import java.util.List;
import mate.jdbc.lib.Injector;
import mate.jdbc.model.Car;
import mate.jdbc.model.Driver;
import mate.jdbc.model.Manufacturer;
import mate.jdbc.service.CarService;

public class Main {
    private static final Injector injector = Injector.getInstance("mate.jdbc");

    public static void main(String[] args) {
        CarService carService = (CarService) injector.getInstance(CarService.class);

        Driver driverMaksim = new Driver("Maksim", "SDD 232231");
        Driver driverOleh = new Driver("Oleh", "RES 759745");
        Driver driverOlha = new Driver("Olha", "GGH 368823");
        Manufacturer audi = new Manufacturer("Audi", "Germany");
        Manufacturer mitsubishi = new Manufacturer("Mitsubishi", "Japan");
        Manufacturer mercedes = new Manufacturer("Mercedes", "Germany");
        List<Car> cars = List.of(
                new Car("Q7", audi, List.of(driverMaksim)),
                new Car("Sport", mitsubishi, List.of(driverOleh)),
                new Car("X5", mercedes, List.of(driverMaksim, driverOlha))
        );

        for (Car car : cars) {
            carService.create(car);
        }
        cars = carService.getAll();

        System.out.println("carService.getAll() = " + cars);

        System.out.println("carService.get(cars.get(0).getId()) = "
                + carService.get(cars.get(0).getId()));

        cars.get(0).setModel("Brand new model");
        System.out.println("carService.update(cars.get(0)) = "
                + carService.update(cars.get(0)));

        carService.delete(cars.get(0).getId());
        System.out.println("carService.delete(cars.get(0).getId()) = "
                + carService.getAll());

        carService.addDriverToCar(driverMaksim, cars.get(1));
        System.out.println("carService.addDriverToCar = "
                + carService.getAll());

        carService.removeDriverFromCar(cars.get(2).getDrivers().get(0), cars.get(2));
        System.out.println("carService.removeDriverFromCar = "
                + carService.getAll());

        System.out.println("carService.getAllByDriver(cars.get(1).getId()) = "
                + carService.getAllByDriver(cars.get(1).getId()));
    }
}
