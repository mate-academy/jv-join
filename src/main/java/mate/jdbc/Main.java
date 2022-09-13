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
        ManufacturerService manufacturerService =
                (ManufacturerService) injector.getInstance(ManufacturerService.class);
        Manufacturer zaz = new Manufacturer("ZAZ", "Ukraine");
        Manufacturer audi = new Manufacturer("AUDI", "Germany");
        Manufacturer nissan = new Manufacturer("NISSAN", "Japan");
        manufacturerService.create(zaz);
        manufacturerService.create(audi);
        manufacturerService.create(nissan);
        System.out.println("Created and added manufacturers!");

        DriverService driverService =
                (DriverService) injector.getInstance(DriverService.class);
        Driver ivan = new Driver("Ivan", "11111111");
        Driver petro = new Driver("Petro", "22222222");
        Driver mukola = new Driver("Mukola", "33333333");
        driverService.create(ivan);
        driverService.create(petro);
        driverService.create(mukola);
        System.out.println("Created and added drivers!");

        CarService carService =
                (CarService) injector.getInstance(CarService.class);
        Car slavuta = new Car("Slavuta", zaz, List.of(ivan));
        Car lanos = new Car("Lanos", zaz, List.of(petro, mukola));
        Car qashqai = new Car("Qashqai", nissan, List.of(ivan, mukola));
        Car touareg = new Car("Touareg", nissan, List.of(ivan, petro, mukola));
        Car q8 = new Car("Q8", audi, List.of(mukola));
        carService.create(slavuta);
        carService.create(lanos);
        carService.create(qashqai);
        carService.create(touareg);
        carService.create(q8);
        System.out.println("Created and added cars!");

        System.out.println(carService.get(lanos.getId()));
        System.out.println("Car by id was gotten!");

        System.out.println(carService.delete(lanos.getId()));
        System.out.println("Car by id was deleted!");

        carService.getAll().forEach(System.out::println);
        System.out.println("All cars was gotten!");

        carService.getAllByDriver(ivan.getId()).forEach(System.out::println);
        System.out.println("All cars for some driver was gotten!");

        System.out.println(slavuta);
        carService.addDriverToCar(petro, slavuta);
        System.out.println(slavuta);
        System.out.println("New driver was successful added!");

        carService.removeDriverFromCar(petro, slavuta);
        System.out.println(slavuta);
        System.out.println("Some driver was successful deleted!");
    }
}
