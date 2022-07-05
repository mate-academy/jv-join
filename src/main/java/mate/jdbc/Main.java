package mate.jdbc;

import mate.jdbc.lib.Injector;
import mate.jdbc.model.Car;
import mate.jdbc.model.Driver;
import mate.jdbc.model.Manufacturer;
import mate.jdbc.service.CarService;
import mate.jdbc.service.DriverService;
import mate.jdbc.service.ManufacturerService;

public class Main {
    public static final Injector injector = Injector.getInstance("mate.jdbc");

    public static void main(String[] args) {
        ManufacturerService manufacturerService =
                (ManufacturerService) injector.getInstance(ManufacturerService.class);
        //        Manufacturer mazda = manufacturerService
        //        .create(new Manufacturer("mazda", "japan"));
        //        System.out.println(mazda.getId());

        Manufacturer mazda = manufacturerService.get(2L);
        //        System.out.println(manufacturerService.get(mazda.getId()));
        //        manufacturerService.delete(3L);
        //        manufacturerService.getAll().forEach(System.out::println);

        DriverService driverService = (DriverService) injector.getInstance(DriverService.class);
        //        Driver driverAlex = driverService.get(1L);
        //                = driverService.create(new Driver("alex", "123"));
        //        System.out.println(driverService.get(driverAlex.getId()));
        //        driverService.getAll().forEach(System.out::println);

        //                = new Car("mazda 3", mazda);
        //        mazda3.addDriver(driverAlex);
        CarService carService = (CarService) injector.getInstance(CarService.class);
        Car mazda3 = carService.get(3L);
        //        carService.create(mazda3);
        //        System.out.println(mazda3);
        //        carService.getAll().forEach(System.out::println);
        //        carService.getAll().forEach(System.out::println);
        //        carService.delete(6L);
        //        mazda3.setModel("cx5");
        //        carService.update(mazda3);
        //        mazda3.addDriver(driverService.get(1L));
        //        carService.update(mazda3);
        carService.getAll().forEach(System.out::println);
        //        Driver driver2 = new Driver("Serge", "87654");
        //        driverService.create(driver2);
        driverService.getAll().forEach(System.out::println);
        Driver current = driverService.get(5L);
        carService.addDriverToCar(current, mazda3);
        //        carService.getAll().forEach(System.out::println);
        //        carService.update()
        //        carService.getAll().forEach(System.out::println);
        System.out.println("get all by driver");
        carService.getAllByDriver(1L).forEach(System.out::println);
    }

}
