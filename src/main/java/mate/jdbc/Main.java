package mate.jdbc;

import java.util.List;
import mate.jdbc.lib.Injector;
import mate.jdbc.model.Car;
import mate.jdbc.model.Driver;
import mate.jdbc.model.Manufacturer;
import mate.jdbc.service.CarService;
import mate.jdbc.service.DriverService;
import mate.jdbc.service.ManufacturerService;
import mate.jdbc.service.impl.CarServiceImpl;
import mate.jdbc.service.impl.DriverServiceImpl;

public class Main {
    private static Injector injector = Injector.getInstance("mate.jdbc");
    private static CarService carService = (CarServiceImpl) injector.getInstance(CarService.class);
    private static DriverService driverService
            = (DriverServiceImpl) injector.getInstance(DriverService.class);
    private static ManufacturerService manufacturerService
            = (ManufacturerService) injector.getInstance(ManufacturerService.class);
    private static final String LICENSE_NUMBER_IVAN = "123456";
    private static final String LICENSE_NUMBER_WRONG = "671241";
    private static final String DEFAULT_NAME_IVAN = "Ivan";
    private static final String COUNTRY_GERMANY = "Germany";
    private static final String COUNTRY_JAPAN = "Japan";
    private static final String NAME_OF_CAR = "BMW";
    private static final String MODEL_X6 = "X6";
    private static final Long DEFAULT_ID_6 = 6L;
    private static final Long DEFAULT_ID_2 = 2L;
    private static final Long DEFAULT_ID_7 = 7L;
    private static final Long DEFAULT_ID_4 = 4L;

    public static void main(String[] args) {
        //test the manufacturerService
        manufacturerService.create(new Manufacturer(null, NAME_OF_CAR, COUNTRY_JAPAN));
        Manufacturer manufacturer = manufacturerService.get(DEFAULT_ID_6);
        manufacturer.setCountry(COUNTRY_GERMANY);
        manufacturerService.update(manufacturer);
        manufacturerService.delete(DEFAULT_ID_6);
        System.out.println(manufacturerService.getAll());
        //test the driverService
        driverService.create(new Driver(null,DEFAULT_NAME_IVAN, LICENSE_NUMBER_WRONG));
        Driver driverIvan = driverService.get(DEFAULT_ID_4);
        driverIvan.setLicenseNumber(LICENSE_NUMBER_IVAN);
        driverService.update(driverIvan);
        driverService.delete(DEFAULT_ID_4);
        System.out.println(driverService.getAll());
        //test the carService
        carService.create(new Car(null, MODEL_X6, manufacturer, List.of(driverIvan)));
        Car car = carService.get(DEFAULT_ID_7);
        carService.addDriverToCar(driverService.get(DEFAULT_ID_2), car);
        System.out.println(carService.get(DEFAULT_ID_7));
        carService.removeDriverFromCar(driverService.get(DEFAULT_ID_2), car);
        carService.update(car);
        carService.delete(DEFAULT_ID_4);
        System.out.println(carService.getAll());
    }
}
