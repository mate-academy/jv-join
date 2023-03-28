package mate.jdbc;

import mate.jdbc.lib.Injector;
import mate.jdbc.model.Car;
import mate.jdbc.model.Driver;
import mate.jdbc.model.Manufacturer;
import mate.jdbc.service.CarService;
import mate.jdbc.service.DriverService;
import mate.jdbc.service.ManufacturerService;
import mate.jdbc.service.impl.CarServiceImpl;
import mate.jdbc.service.impl.DriverServiceImpl;
import mate.jdbc.service.impl.ManufacturerServiceImpl;

public class Main {
    private static final Injector injector = Injector.getInstance("mate.jdbc");
    private static final ManufacturerService manufacturerService
            = (ManufacturerServiceImpl) injector.getInstance(ManufacturerService.class);
    private static final DriverService driverService
            = (DriverServiceImpl) injector.getInstance(DriverService.class);
    private static final CarService carService
            = (CarServiceImpl) injector.getInstance(CarService.class);

    public static void main(String[] args) {
        Manufacturer manufacturerPorsche = manufacturerService
                .create(new Manufacturer(1L,"Porsche","Germany"));
        Manufacturer manufacturerBmw = manufacturerService
                .create(new Manufacturer(2L,"BMW","Germany"));
        Manufacturer manufacturerHyundai = manufacturerService
                .create(new Manufacturer(3L,"Hyundai", "South Korea"));

        Driver driverViktoria = driverService.create(new Driver(1L,"Viktoria","KG890"));
        Driver driverBob = driverService.create(new Driver(2L,"Bob","OL104"));
        Driver driverLee = driverService.create(new Driver(3L,"Lee","IK823"));
        Driver driverOlivia = driverService.create(new Driver(4L,"Olivia","IJ321"));

        Car porsche = new Car(1L,"GTX",manufacturerPorsche);
        porsche.getDrivers().add(driverViktoria);
        porsche.getDrivers().add(driverBob);

        Car bmw = new Car(2L,"I5",manufacturerBmw);
        bmw.getDrivers().add(driverViktoria);
        bmw.getDrivers().add(driverOlivia);

        Car hyundai = new Car(3L,"XL",manufacturerHyundai);
        hyundai.getDrivers().add(driverLee);
        hyundai.getDrivers().add(driverBob);

        Car peugeot = new Car(4L,"107",manufacturerPorsche);
        peugeot.getDrivers().add(driverBob);

        carService.create(porsche);
        carService.create(bmw);
        carService.create(hyundai);
        carService.create(peugeot);
        porsche.setModel("GTX+");
        carService.update(porsche);
        hyundai.getDrivers().remove(driverBob);
        carService.update(hyundai);
        carService.removeDriverFromCar(driverViktoria, bmw);
        carService.addDriverToCar(driverViktoria,hyundai);
        carService.delete(peugeot.getId());
        carService.getAll().forEach(System.out::println);
        carService.getAllByDriver(hyundai.getId()).forEach(System.out::println);
    }
}
