package mate.jdbc;

import java.util.ArrayList;
import java.util.List;
import mate.jdbc.lib.Injector;
import mate.jdbc.model.Car;
import mate.jdbc.model.Driver;
import mate.jdbc.model.Manufacturer;
import mate.jdbc.service.CarService;

public class Main {
    private static final Injector injector = Injector.getInstance("mate.jdbc");
    private static final CarService carService =
            (CarService) injector.getInstance(CarService.class);

    public static void main(String[] args) {
        Driver driver1 = new Driver(1L, "Bob", "123");
        Driver driver2 = new Driver(2L, "Andrew", "456");
        Driver driver3 = new Driver(3L, "John", "098");
        List<Driver> drivers1 = new ArrayList<>();
        drivers1.add(driver1);
        drivers1.add(driver2);
        List<Driver> drivers2 = new ArrayList<>();
        drivers1.add(driver2);
        drivers1.add(driver3);
        Manufacturer manufacturer1 = new Manufacturer(1L, "Audi", "Germany");
        Manufacturer manufacturer2 = new Manufacturer(2L, "Nissan", "Japan");
        Car audi = new Car(2L, "RS6", manufacturer1, drivers1);
        Car nissan = new Car(1L, "Rogue", manufacturer2, drivers2);
        List<Car> cars = List.of(audi, nissan);
        for (Car car : cars) {
            carService.create(car);
        } // success

        carService.get(audi.getId());
        carService.getAll();
        audi.setModel("S4");
        carService.update(audi);
        carService.removeDriverFromCar(driver1, audi);
        carService.getAllByDriver(2L);
    }
}
