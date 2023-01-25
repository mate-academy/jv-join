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
        DriverService driverService = (DriverService) injector.getInstance(DriverService.class);
        Driver petrenkoPP = new Driver("Petrenko P.P.", "KIA000001");
        Driver ivanenkoII = new Driver("Ivanenko I.I.", "KIB000001");
        Driver boykoOO = new Driver("Boyko O.O.", "KIC000001");
        Driver sydorenkoBB = new Driver("Sydorenko B.B.", "KIE000001");
        driverService.create(petrenkoPP);
        driverService.create(ivanenkoII);
        driverService.create(boykoOO);
        driverService.create(sydorenkoBB);
        ManufacturerService manufacturerService
                = (ManufacturerService) injector.getInstance(ManufacturerService.class);
        Manufacturer lanosManufacturer = new Manufacturer("Lanos", "Ukraine");
        Manufacturer volvoManufacturer = new Manufacturer("Volvo", "China");
        manufacturerService.create(lanosManufacturer);
        manufacturerService.create(volvoManufacturer);
        CarService carService = (CarService) injector.getInstance(CarService.class);
        Car lanos = new Car("1.6", lanosManufacturer, List.of(petrenkoPP, ivanenkoII));
        Car volvo = new Car("XC90", volvoManufacturer, List.of(boykoOO, sydorenkoBB));
        carService.create(lanos);
        carService.create(volvo);
        carService.getAll().forEach(System.out::println);
        carService.removeDriverFromCar(petrenkoPP, lanos);
        carService.removeDriverFromCar(sydorenkoBB, volvo);
        carService.addDriverToCar(sydorenkoBB, lanos);
        carService.addDriverToCar(petrenkoPP, volvo);
        System.out.println(carService.get(lanos.getId()));
        System.out.println(carService.get(volvo.getId()));
        carService.getAllByDriver(petrenkoPP.getId()).forEach(System.out::println);
    }
}
