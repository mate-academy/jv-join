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
    private static final Injector injector = Injector.getInstance("mate.jdbc");

    public static void main(String[] args) {
        ManufacturerService manufacturerService =
                (ManufacturerService) injector.getInstance(ManufacturerService.class);
        Manufacturer bmw = new Manufacturer("BMW", "Germany");
        Manufacturer chryslerGroup = new Manufacturer("GM", "USA");
        manufacturerService.create(bmw);
        manufacturerService.create(chryslerGroup);
        DriverService driverService =
                (DriverService) injector.getInstance(DriverService.class);
        Driver schumacher = new Driver("Schumacher", "ln012345671");
        Driver hamilton = new Driver("Hamilton", "ln012345672");
        driverService.create(schumacher);
        driverService.create(hamilton);
        List<Driver> firstTeam = new ArrayList<>();
        firstTeam.add(schumacher);
        List<Driver> secondTeam = new ArrayList<>();
        secondTeam.add(hamilton);
        CarService carService = (CarService) injector.getInstance(CarService.class);
        Car bmw5 = new Car("530d", bmw, firstTeam);
        Car challenger = new Car("Dodge challenger", chryslerGroup, secondTeam);
        System.out.println("CREATE");
        System.out.println(carService.create(bmw5));
        System.out.println(carService.create(challenger));
        System.out.println("GET");
        System.out.println(carService.get(bmw5.getId()));
        System.out.println("GET ALL");
        System.out.println(carService.getAll());
        System.out.println("UPDATE");
        challenger.setModel("Charger");
        System.out.println(carService.update(challenger));
        System.out.println("ADD DRIVER TO CAR");
        carService.addDriverToCar(hamilton, bmw5);
        System.out.println("REMOVE DRIVER FROM CAR");
        carService.removeDriverFromCar(hamilton, bmw5);
        System.out.println("GET ALL BY DRIVER");
        System.out.println(carService.getAllByDriver(hamilton.getId()));
    }
}
