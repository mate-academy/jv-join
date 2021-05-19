package mate.jdbc;

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
    private static final CarService carService =
            (CarService) injector.getInstance(CarService.class);
    private static final ManufacturerService manufacturerService = (ManufacturerService) injector
            .getInstance(ManufacturerService.class);

    public static void main(String[] args) {
        Manufacturer batmanCar = new Manufacturer("batmanCar", "Gotham");
        manufacturerService.create(batmanCar);

        Driver stig = new Driver("Stig", "1111");
        Driver tom = new Driver("Tom", "2222");
        driverService.create(stig);
        driverService.create(tom);
        List<Driver> driverList = List.of(stig, tom);

        Car batmanCarNewModel = new Car(batmanCar, driverList);
        batmanCarNewModel.setModel("Tractor");
        carService.create(batmanCarNewModel);

        System.out.println(carService.getAll());
        System.out.println(carService.getAllByDriver(stig.getId()));

        System.out.println(carService.get(batmanCarNewModel.getId()));
        System.out.println(carService.delete(batmanCarNewModel.getId()));
    }
}
