package mate.jdbc;

import java.util.ArrayList;
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

        Manufacturer audi = new Manufacturer("AUDI", "Germany");
        Manufacturer bmw = new Manufacturer("BMW", "Ukraine");
        Manufacturer theOne = new Manufacturer("shiguli", "USA");
        manufacturerService.create(audi);
        manufacturerService.create(bmw);
        manufacturerService.create(theOne);
        System.out.println(manufacturerService.getAll());
        System.out.println("Create manufacturers to DB");

        DriverService driverService =
                (DriverService) injector.getInstance(DriverService.class);

        Driver driverMila = new Driver("Mila","4567334");
        Driver driverIlya = new Driver("Ilya","9087645");
        Driver driverRoma = new Driver("Roma","9757575");
        Driver driverLola = new Driver("Lola","1009293");
        driverService.create(driverMila);
        driverService.create(driverIlya);
        driverService.create(driverLola);
        driverService.create(driverRoma);
        System.out.println("Create drivers to DB");

        CarService carService = (CarService) injector.getInstance(CarService.class);

        Car moskvich = new Car("moskvich412", bmw, new ArrayList<>());
        moskvich.getDrivers().add(driverLola);
        carService.create(moskvich);

        Car copeechka = new Car("111",theOne, new ArrayList<>());
        copeechka.getDrivers().add(driverRoma);
        System.out.println(carService.create(copeechka));
        System.out.println("Create cars to DB");
        System.out.println("GetAll " + carService.getAll());

        carService.addDriverToCar(driverMila, copeechka);
        System.out.println("getAllByDriver " + carService.getAllByDriver(88L));
        carService.removeDriverFromCar(driverLola, moskvich);

        System.out.println(carService.update(copeechka));
    }
}
