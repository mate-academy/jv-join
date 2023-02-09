package mate.jdbc;

import mate.jdbc.lib.Injector;
import mate.jdbc.model.Car;
import mate.jdbc.model.Driver;
import mate.jdbc.model.Manufacturer;
import mate.jdbc.service.CarService;

public class Main {

    private static final Injector injector = Injector.getInstance("mate.jdbc");

    public static void main(String[] args) {
        Manufacturer manufacturer = new Manufacturer(2L, "Peugeot", "Hungary");
        CarService carService = (CarService) injector.getInstance(CarService.class);
        carService.create(new Car("106", manufacturer));
        carService.get(6L);
        Car car = carService.update(new Car(6L,"206", manufacturer));
        carService.delete(9L);
        carService.removeDriverFromCar(new Driver(2L),new Car(2L));
        Driver driver = new Driver(1L, "Denis", "123456789");
        carService.addDriverToCar(driver,car);
        System.out.println(carService.getAll());
        System.out.println(carService.getAllByDriver(1L));
    }
}
