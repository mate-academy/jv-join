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
        DriverService driverService =
                (DriverService) injector.getInstance(DriverService.class);

        Driver vinDiesel = new Driver("VinDiesel", "BH2506AK");
        driverService.create(vinDiesel);
        Driver terminator = new Driver("Terminator", "AP0777BK");
        driverService.create(terminator);
        Driver fedor = new Driver("Fedor", "450777BK");
        driverService.create(fedor);
        Driver babak = new Driver("Babak", "777");
        driverService.create(babak);
        Driver bond = new Driver("Bond", "007");
        driverService.create(bond);
        System.out.println(driverService.getAll());

        ManufacturerService manufacturerService =
                (ManufacturerService) injector.getInstance(ManufacturerService.class);

        Manufacturer bmw = new Manufacturer("BMW", "Germany");
        manufacturerService.create(bmw);
        Manufacturer ford = new Manufacturer("Ford", "USA");
        manufacturerService.create(ford);

        System.out.println(manufacturerService.getAll());

        CarService carService =
                (CarService) injector.getInstance(CarService.class);

        List<Driver> firstDriversTeam = new ArrayList<>();
        firstDriversTeam.add(driverService.get(vinDiesel.getId()));
        firstDriversTeam.add(driverService.get(terminator.getId()));
        Car boomer = new Car("BMW X5",bmw, firstDriversTeam);
        carService.create(boomer);

        List<Driver> secondDriversTeam = new ArrayList<>();
        secondDriversTeam.add(driverService.get(fedor.getId()));
        secondDriversTeam.add(driverService.get(babak.getId()));
        secondDriversTeam.add(driverService.get(bond.getId()));
        Car american = new Car("Ford focus",ford, secondDriversTeam);
        carService.create(american);

        carService.addDriverToCar(bond, boomer);
        carService.removeDriverFromCar(vinDiesel, boomer);
        System.out.println(carService.get(boomer.getId()));
    }
}
