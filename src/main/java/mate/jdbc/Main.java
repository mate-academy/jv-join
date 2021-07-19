package mate.jdbc;

import java.util.ArrayList;
import java.util.List;
import mate.jdbc.lib.Injector;
import mate.jdbc.model.Car;
import mate.jdbc.model.Driver;
import mate.jdbc.model.Manufacturer;
import mate.jdbc.service.CarService;
import mate.jdbc.service.DriverService;
import mate.jdbc.service.ManufacturerService;

public class Main {
    private static Injector injector = Injector.getInstance("mate.jdbc");

    public static void main(String[] args) {
        ManufacturerService manufacturerService =
                (ManufacturerService) injector.getInstance(ManufacturerService.class);
        Manufacturer tesla = new Manufacturer("Tesla", "USA");
        manufacturerService.create(tesla);

        final CarService carService =
                (CarService) injector.getInstance(CarService.class);

        final DriverService driverService =
                (DriverService) injector.getInstance(DriverService.class);
        final Driver igor = new Driver("Igor", "1112222");
        final Driver sergey = new Driver("Sergey", "1234567");
        final Driver eugen = new Driver("Eugene", "7654321");
        final Driver dasha = new Driver("Daria", "6899320");
        final Driver artyom = new Driver("Artyom", "9714032");
        final Driver andrey = new Driver("Andrey", "5246436");
        final Driver cactus = new Driver("Cactus", "3213443");
        List<Driver> modelSDrivers = new ArrayList<>();
        modelSDrivers.add(eugen);
        modelSDrivers.add(igor);
        List<Driver> modelXDrivers = new ArrayList<>();
        modelXDrivers.add(sergey);
        modelXDrivers.add(eugen);
        List<Driver> modelYDrivers = new ArrayList<>();
        modelYDrivers.add(dasha);
        modelYDrivers.add(artyom);
        List<Driver> model3Drivers = new ArrayList<>();
        model3Drivers.add(eugen);
        model3Drivers.add(cactus);
        driverService.create(igor);
        driverService.create(sergey);
        driverService.create(eugen);
        driverService.create(dasha);
        driverService.create(artyom);
        driverService.create(andrey);
        driverService.create(cactus);
        final Car modelS = new Car("Model S", tesla);
        modelS.setDrivers(modelSDrivers);
        final Car modelX = new Car("Model X", tesla);
        modelX.setDrivers(modelXDrivers);
        final Car modelY = new Car("Model Y", tesla);
        modelY.setDrivers(modelYDrivers);
        Car model3 = new Car("Model 3", tesla);
        model3.setDrivers(model3Drivers);
        carService.create(modelS);
        carService.create(modelX);
        carService.create(modelY);
        carService.create(model3);

        System.out.println("MODEL S");
        System.out.println(carService.get(modelS.getId()));
        System.out.println();
        System.out.println("ALL MODELS AND DRIVERS");
        carService.getAll().forEach(System.out::println);
        System.out.println();
        model3 = carService.update(modelS);
        System.out.println("CHANGING MODEL 3 TO MODEL S");
        System.out.println(carService.get(model3.getId()));
        System.out.println();
        System.out.println("DELETING MODEL S");
        carService.delete(modelS.getId());
        System.out.println("check the DB, it is deleted, very softly)");
        System.out.println();
        System.out.println("Eugen's cars:");
        System.out.println(carService.getAllByDriver(eugen.getId()));
        System.out.println();
        Driver arsen = new Driver("Arsen", "2281337");
        driverService.create(arsen);
        carService.addDriverToCar(arsen, modelX);
        System.out.println("ADDING NEW DRIVER ARSEN TO TESLA MODEL X");
        System.out.println(carService.get(modelX.getId()));
        System.out.println("DELETING ARSEN FROM TESLA MODEL X");
        carService.removeDriverFromCar(arsen, modelX);
        System.out.println(carService.get(modelX.getId()));
    }
}
