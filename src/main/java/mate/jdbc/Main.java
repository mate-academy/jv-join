package mate.jdbc;

import java.util.List;
import mate.jdbc.lib.Injector;
import mate.jdbc.model.Car;
import mate.jdbc.model.Driver;
import mate.jdbc.model.Manufacturer;
import mate.jdbc.service.CarService;

public class Main {
    private static Injector injector = Injector.getInstance("mate.jdbc");

    public static void main(String[] args) {
        CarService carService = (CarService) injector.getInstance(CarService.class);
        Car oldBmw = new Car("X5", new Manufacturer(1L, "BMW", "Germany"),
                List.of(new Driver(1L, "Bob", "123"),
                        new Driver(2L,"Dick", "234")));
        Car newBmw = new Car(1L, "X7", new Manufacturer(4L, "ZAZ", "Ukraine"),
                List.of(new Driver(3L, "Rob", "456"),
                        new Driver(4L,"Tj", "567")));
        Car porsche = new Car("Cayenne", new Manufacturer(2L, "Porsche", "Italy"),
                List.of(new Driver(1L, "Bob", "123"),
                        new Driver(3L, "Rob", "456")));
        Car dacia = new Car(7L,"Duster", new Manufacturer(3L, "Dacia", "Romania"),
                List.of(new Driver(2L,"Dick", "234"),
                        new Driver(4L,"Tj", "567")));
        Car bmwX5 = carService.create(oldBmw);
        Car porscheCayenne = carService.create(porsche);
        Car daciaDuster = carService.create(dacia);
        System.out.println("Get porsche: " + carService.get(porscheCayenne.getId()));
        System.out.println("Update bmw: " + carService.update(newBmw));
        System.out.println("Delete dacia: " + carService.delete(daciaDuster.getId()));
        System.out.println("Cars by driver id: "
                + carService.getAllByDriver(newBmw.getDrivers().get(1).getId()));
        System.out.println("All cars: " + carService.getAll());
    }
}
