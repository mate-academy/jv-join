package mate.jdbc;

import java.util.ArrayList;
import java.util.List;
import mate.jdbc.lib.Injector;
import mate.jdbc.model.Car;
import mate.jdbc.model.Driver;
import mate.jdbc.model.Manufacturer;
import mate.jdbc.service.CarService;
import mate.jdbc.service.impl.CarServiceImpl;

public class Main {
    private static Injector injector = Injector.getInstance("mate.jdbc");
    private static CarService carService = (CarServiceImpl) injector.getInstance(CarService.class);
    private static final String DEFAULT_NUMBER_IVAN = "123456";
    private static final String DEFAULT_NUMBER_MYKOLA = "671241";
    private static final String DEFAULT_NAME_MYKOLA = "Mykola";
    private static final String DEFAULT_NAME_IVAN = "Ivan";
    private static final String DEFAULT_COUNTRY = "Germany";
    private static final String DEFAULT_NAME_OF_CAR = "BMW";
    private static final String DEFAULT_MODEL_X6 = "X6";
    private static final String DEFAULT_MODEL_X7 = "X7";
    private static final Long DEFAULT_ID_1 = 1L;
    private static final Long DEFAULT_ID_2 = 2L;

    public static void main(String[] args) {
        List<Driver> drivers = new ArrayList<>();
        drivers.add(new Driver(DEFAULT_ID_1, DEFAULT_NAME_IVAN, DEFAULT_NUMBER_IVAN));
        Manufacturer manufacturer
                = new Manufacturer(DEFAULT_ID_1, DEFAULT_NAME_OF_CAR, DEFAULT_COUNTRY);
        Car car = new Car(DEFAULT_MODEL_X6, manufacturer, drivers);
        car.setId(DEFAULT_ID_2);
        carService.create(car);
        carService.addDriverToCar(new Driver(DEFAULT_ID_2,
                DEFAULT_NAME_MYKOLA, DEFAULT_NUMBER_MYKOLA), car);
        carService.removeDriverFromCar(new Driver(DEFAULT_ID_2,
                DEFAULT_NAME_MYKOLA, DEFAULT_NUMBER_MYKOLA), car);
        carService.delete(DEFAULT_ID_1);
        System.out.println(carService.get(DEFAULT_ID_2));
        Car carUpdated = new Car(DEFAULT_MODEL_X7, manufacturer, drivers);
        carService.update(carUpdated);
        System.out.println(carService.get(DEFAULT_ID_2));
        System.out.println(carService.getAll());
    }
}
