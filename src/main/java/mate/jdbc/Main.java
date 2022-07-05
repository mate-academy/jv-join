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

        DriverService driverService = (DriverService) injector.getInstance(DriverService.class);
        Driver lens = new Driver("Lens", "12345");
        driverService.create(lens);
        Driver margie = new Driver("Margie", "23456");
        driverService.create(margie);
        Driver umesh = new Driver("Umesh", "34567");
        driverService.create(umesh);
        Driver rajesh = new Driver("rajesh", "45678");
        driverService.create(rajesh);

        ManufacturerService manufacturerService =
                (ManufacturerService) injector.getInstance(ManufacturerService.class);
        Manufacturer lincoln = new Manufacturer("Lincoln", "USA");
        manufacturerService.create(lincoln);
        Manufacturer ford = new Manufacturer("Ford", "USA");
        manufacturerService.create(ford);
        Manufacturer audi = new Manufacturer("Audi", "Germany");
        manufacturerService.create(audi);
        Manufacturer mitsubishi = new Manufacturer("Mitsubishi", "Japan");
        manufacturerService.create(mitsubishi);
        //check create() method
        CarService carService = (CarService) injector.getInstance(CarService.class);
        Car nautilus = new Car("Nautilus", lincoln, List.of(lens, rajesh));
        carService.create(nautilus);
        Car edge = new Car("Edge", ford, List.of(margie));
        carService.create(edge);
        Car q8 = new Car("Q8", audi, List.of(umesh, lens));
        carService.create(q8);
        Car lancer = new Car("Lancer", mitsubishi, List.of(margie, rajesh));
        carService.create(lancer);
        //check get() method
        System.out.println(carService.get(lincoln.getId()));
        System.out.println(carService.get(ford.getId()));
        //check update() method
        List<Driver> driversListToUpdate = new ArrayList<>();
        driversListToUpdate.add(lens);
        driversListToUpdate.add(rajesh);
        Car utilityCar =
                new Car(audi.getId(),"Pajero", mitsubishi, driversListToUpdate);
        System.out.println(carService.update(utilityCar));
        //check delete() method
        carService.delete(mitsubishi.getId());
        //check getAll() method
        carService.getAll().forEach(System.out::println);
        //check addDriverToCar() method && getAllByDriver() method
        carService.addDriverToCar(margie, utilityCar);
        System.out.println(carService.getAllByDriver(margie.getId()));
        //check removeDriverFromCar() method && getAllByDriver() method
        carService.removeDriverFromCar(margie, utilityCar);
        System.out.println(carService.getAllByDriver(margie.getId()));
    }
}
