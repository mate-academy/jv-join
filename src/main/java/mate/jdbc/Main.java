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
        CarService carService
                = (CarService) injector.getInstance(CarService.class);
        System.out.println("Removing cars from db ...");
        carService.getAll().forEach(car -> carService.delete(car.getId()));
        System.out.println("Creating drivers if they dont exist yet ...");
        Driver olaf = new Driver("Olaf", "*GER*");
        Driver gans = new Driver("Gans", "*KELLEN*");
        List<Driver> driversFromGermany = new ArrayList<>();
        driversFromGermany.add(olaf);
        driversFromGermany.add(gans);
        DriverService driverService
                = (DriverService) injector.getInstance(DriverService.class);
        driversFromGermany.forEach(driverService::create);
        System.out.println("Printing all drivers in db ...");
        driverService.getAll().forEach(driver -> System.out.println());
        System.out.println("Creating new manufacturers ...");
        Manufacturer manufacturerNato = new Manufacturer("NATO", "Germany");
        ManufacturerService manufacturerService
                = (ManufacturerService) injector.getInstance(ManufacturerService.class);
        manufacturerService.create(manufacturerNato);
        System.out.println("Creating car ...");
        Car carOne = carService.create(new Car(
                        "IRIS-T",
                        manufacturerNato,
                        driversFromGermany));
        System.out.println("Printing car data from db " + carOne.getModel());
        System.out.println(carService.get(carOne.getId()));
        System.out.println("Updating car " + carOne.getModel());
        carOne.setModel("Pajero");
        Manufacturer manufacturerFromJapan
                = new Manufacturer("Mitsubishi", "Japan");
        manufacturerService.create(manufacturerFromJapan);
        carOne.setManufacturer(manufacturerFromJapan);
        System.out.println(carService.update(carOne));
        System.out.println("Creating new driver ...");
        Driver lii = new Driver("Lii", "*JAPAN*");
        System.out.println(driverService.create(lii));
        System.out.println("Updating driver for car " + carOne.getModel());
        carOne.setDrivers(List.of(lii));
        System.out.println(carService.update(carOne));
        System.out.println("Printing car data from db ");
        System.out.println(carService.get(carOne.getId()));
        System.out.println("Deleting data from db car - " + carOne.getModel());
        System.out.println(carService.delete(carOne.getId()));
        System.out.println("Printing all cars by driver id ");
        System.out.println(carService.getAllByDriver(lii.getId()));
        System.out.println("Creating new driver if it doesnt exist in db");
        Driver zaluzhniy = new Driver("Zaluzhniy", "*UKR*");
        System.out.println(driverService.create(zaluzhniy));
        System.out.println("Creating car ...");
        Car carTwo = carService.create(new Car(
                "Leopard",
                manufacturerNato,
                driversFromGermany));
        System.out.println(carService.get(carTwo.getId()));
        System.out.println("Adding driver " + zaluzhniy.getName()
                + " the car " + carTwo.getModel());
        carService.addDriverToCar(zaluzhniy, carTwo);
        System.out.println(carService.update(carTwo));
        System.out.println("Removing driver " + olaf.getName()
                + " from car " + carTwo.getModel());
        carService.removeDriverFromCar(olaf, carTwo);
        System.out.println(carService.update(carTwo));
        System.out.println(carService.get(carTwo.getId()));
        System.out.println("Printing all cars from db");
        carService.getAll().forEach(car -> System.out.println());

    }
}
