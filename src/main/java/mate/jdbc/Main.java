package mate.jdbc;

import mate.jdbc.lib.Injector;
import mate.jdbc.model.Car;
import mate.jdbc.model.Driver;
import mate.jdbc.service.CarService;
import mate.jdbc.service.DriverService;
import mate.jdbc.service.ManufacturerService;
import java.math.BigDecimal;


public class Main {
    private static final Injector injector = Injector.getInstance("mate.jdbc");

    public static void main(String[] args) {
        CarService carService = (CarService) injector.getInstance(CarService.class);
        ManufacturerService manufacturerService
                = (ManufacturerService) injector.getInstance(ManufacturerService.class);
        DriverService driverService
                = (DriverService) injector.getInstance(DriverService.class);
       /* Driver driver = new Driver("Goga", "656565");
        driverService.create(driver);
        Car myCar = new Car("Honda","black");
        myCar.setPrice(BigDecimal.valueOf(100000));
        myCar.setDriver(driver);
        carService.update(myCar);*/
        carService.delete(2L);
        System.out.println(carService.getAll());
    }
}
