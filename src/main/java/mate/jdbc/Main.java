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
        ManufacturerService manufacturerService = (ManufacturerService) injector
                .getInstance(ManufacturerService.class);
        Manufacturer mercedes = new Manufacturer("Mercedes", "Germany");
        Manufacturer lada = new Manufacturer("Lada", "Ukraine");
        Manufacturer mazda = new Manufacturer("Mazda", "Japan");
        manufacturerService.create(mercedes);
        manufacturerService.create(lada);
        manufacturerService.create(mazda);

        DriverService driverService = (DriverService) injector
                .getInstance(DriverService.class);
        Driver yaroslav = new Driver("Yaroslav", "01234");
        Driver eugene = new Driver("Eugene", "02344");
        Driver volodimir = new Driver("Volodimir", "03454");
        driverService.create(yaroslav);
        driverService.create(eugene);
        driverService.create(volodimir);

        List<Driver> comfortDrivers = new ArrayList<>();
        List<Driver> premiumDrivers = new ArrayList<>();
        comfortDrivers.add(yaroslav);
        comfortDrivers.add(volodimir);
        premiumDrivers.add(eugene);
        CarService carService = (CarService) injector.getInstance(CarService.class);
        System.out.println("Create 3 cars!");
        Car mercedesSprinter = new Car("Sprinter", mercedes, comfortDrivers);
        carService.create(mercedesSprinter);
        System.out.println(carService.get(mercedesSprinter.getId()));
        Car mazdaRx = new Car("Rx", mazda, comfortDrivers);
        carService.create(mazdaRx);
        System.out.println(carService.get(mazdaRx.getId()));
        Car ladaKopeck = new Car("Kopeck", lada, premiumDrivers);
        carService.create(ladaKopeck);
        System.out.println(carService.get(ladaKopeck.getId()));
        System.out.println("--------");
        System.out.println("Delete lada car!");
        carService.delete(ladaKopeck.getId());
        System.out.println(carService.getAll());
        System.out.println("--------");
        System.out.println("Remove Yaroslav driver from mercedesSprinter!");
        carService.removeDriverFromCar(yaroslav, mercedesSprinter);
        System.out.println(carService.get(mercedesSprinter.getId()));
        System.out.println("-----------");
        System.out.println("Add Eugene driver to mazdaRx!");
        carService.addDriverToCar(eugene, mazdaRx);
        System.out.println(carService.get(mazdaRx.getId()));
        System.out.println("----------");
        System.out.println("Get all cars by driver Volodimir");
        System.out.println(carService.getAllByDriver(volodimir.getId()));
    }
}
