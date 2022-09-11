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

    public static void main(String[] args) {
        final DriverService driverService = (DriverService)
                injector.getInstance(DriverService.class);
        final ManufacturerService manufacturerService = (ManufacturerService)
                injector.getInstance(ManufacturerService.class);
        final CarService carService = (CarService) injector.getInstance(CarService.class);

        Driver nazar = driverService.create(new Driver(null, "Nazar", "9009"));
        Driver dima = driverService.create(new Driver(null, "Dima", "4044"));
        Car geelyFC = new Car();
        geelyFC.setModel("Geely-FC");
        geelyFC.setManufacturer(manufacturerService.create(
                new Manufacturer(null, "Geely", "China")));
        geelyFC.setDrivers(List.of(nazar, dima));
        System.out.println("------------------====TESTS STARTED====---------------------");

        System.out.println("");
        System.out.println("-------------------------------------------------------------");
        System.out.println("method create() test");
        System.out.println("The result of method carService.create(geelyFC) should be " + geelyFC);
        System.out.println("AND ACTUAL is --> " + carService.create(geelyFC));
        System.out.println("-------------------------------------------------------------");

        System.out.println("-------------------------------------------------------------");
        System.out.println("method get() test");
        System.out.println("The result of method carService.get(geelyFC.getId()) should be "
                + geelyFC);
        System.out.println("AND ACTUAL is --> " + carService.get(geelyFC.getId()));
        System.out.println("-------------------------------------------------------------");

        System.out.println("-------------------------------------------------------------");
        System.out.println("method getAll() test");
        System.out.println("The result-list of method carService.getAll() should contain "
                + geelyFC);
        System.out.println("AND ACTUAL is --> " + carService.getAll());
        System.out.println("-------------------------------------------------------------");

        System.out.println("-------------------------------------------------------------");
        System.out.println("method update() test");
        geelyFC.setModel("HAVAL-H9");
        geelyFC.setManufacturer(manufacturerService.get(8L));
        geelyFC.setDrivers(List.of(driverService.get(1L), driverService.get(2L), nazar));
        System.out.println("Upgraded geelyFC to " + geelyFC);
        System.out.println("The result of method carService.update(geelyFC) should be " + geelyFC);
        System.out.println("AND ACTUAL is --> " + carService.update(geelyFC));
        System.out.println("--------");
        System.out.println("AND ALSO : The result of method carService.get(geelyFC.getId()) "
                + "after calling method update");
        System.out.println("should be " + geelyFC);
        System.out.println("AND ACTUAL is --> " + carService.get(geelyFC.getId()));
        System.out.println("-------------------------------------------------------------");

        System.out.println("-------------------------------------------------------------");
        System.out.println("method getAllByDriver() test");
        System.out.println("The result-list of method carService.getAllByDriver(nazar.getId())");
        System.out.println("should contain only " + geelyFC);
        System.out.println("AND ACTUAL is --> " + carService.getAllByDriver(nazar.getId()));
        System.out.println("-------------------------------------------------------------");

        System.out.println("-------------------------------------------------------------");
        System.out.println("method addDriverToCar() test");
        carService.addDriverToCar(dima, geelyFC);
        System.out.println("Called method carService.addDriverToCar(dima, geelyFC)");
        System.out.println("THEN");
        System.out.println("The result-list of method getAllByDriver(dima.getId()) "
                + "after calling addDriverToCar method");
        System.out.println("should contain " + geelyFC);
        System.out.println("AND ACTUAL is --> " + carService.getAllByDriver(dima.getId()));
        System.out.println("-------------------------------------------------------------");

        System.out.println("-------------------------------------------------------------");
        System.out.println("method removeDriverFromCar() test");
        carService.removeDriverFromCar(dima, geelyFC);
        System.out.println("Called carService.removeDriverFromCar(dima, geelyFC)");
        System.out.println("THEN");
        System.out.println("The result-list of method getAllByDriver(dima.getId())");
        System.out.println("after calling removeDriverFromCar method should be EMPTY");
        System.out.println("AND ACTUAL is --> " + carService.getAllByDriver(dima.getId()));
        System.out.println("-------------------------------------------------------------");

        System.out.println("-------------------------------------------------------------");
        System.out.println("method delete() test");
        System.out.println("The result of method carService.delete(geelyFC.getId()) "
                + "should be : TRUE");
        System.out.println("AND ACTUAL is --> " + carService.delete(geelyFC.getId()));
        System.out.println("--------");
        System.out.println("AND ALSO : The result-list of method carService.getAll() "
                + "after calling method delete()");
        System.out.println("should not contain " + geelyFC);
        System.out.println("AND ACTUAL is --> " + carService.getAll());
        System.out.println("-------------------------------------------------------------");
        System.out.println("");

        System.out.println("------------------====TESTS FINISHED====---------------------");
    }
}
