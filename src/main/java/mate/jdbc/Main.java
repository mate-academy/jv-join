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
    private static Injector injector = Injector.getInstance("mate.jdbc");

    public static void main(String[] args) {
        CarService carService
                = (CarService) injector.getInstance(CarService.class);
        DriverService driverService
                = (DriverService) injector.getInstance(DriverService.class);
        ManufacturerService manufacturerService
                = (ManufacturerService) injector.getInstance(ManufacturerService.class);
        Driver olaf = new Driver("Olaf", "*GER*");
        Driver gans = new Driver("Gans", "*KELLEN*");
        List<Driver> driversFromGermany = List.of(olaf, gans);
        driversFromGermany.forEach(driverService::create);
        Manufacturer manufacturerNato = new Manufacturer("NATO", "Germany");
        manufacturerService.create(manufacturerNato);
        Car carOne = carService.create(new Car(
                        "IRIS-T",
                        manufacturerNato,
                        driversFromGermany));
        System.out.println(carService.get(carOne.getId()));
        System.out.println(carService.getAll());
        carOne.setModel("Pajero");
        Manufacturer manufacturerFromJapan
                = new Manufacturer("Mitsubishi", "Japan");
        manufacturerService.create(manufacturerFromJapan);
        carOne.setManufacturer(manufacturerFromJapan);
        Driver lii = new Driver("Lii", "*JAPAN*");
        driverService.create(lii);
        carOne.setDrivers(List.of(lii));
        System.out.println(carService.update(carOne));
        System.out.println(carService.get(carOne.getId()));
        System.out.println(carService.delete(carOne.getId()));
        System.out.println(carService.getAll());
        System.out.println(carService.getAllByDriver(2L));
        Driver zaluzhniy = new Driver("Zaluzhniy", "*UKR*");
        driverService.create(zaluzhniy);
        Car carTwo = carService.create(new Car(
                "Leopard",
                manufacturerNato,
                driversFromGermany));
        carService.addDriverToCar(zaluzhniy, carTwo);
        System.out.println(carService.get(carTwo.getId()));
        System.out.println(carService.update(carTwo));
        carService.removeDriverFromCar(olaf, carTwo);
        System.out.println(carService.update(carTwo));
        System.out.println(carService.get(carTwo.getId()));
    }
}
