package mate.jdbc;

import mate.jdbc.lib.Injector;
import mate.jdbc.model.Car;
import mate.jdbc.model.Driver;
import mate.jdbc.service.CarService;
import mate.jdbc.service.DriverService;
import mate.jdbc.service.ManufacturerService;

public class Main {
    private static final Injector injector = Injector.getInstance("mate.jdbc");

    public static void main(String[] args) {

        DriverService driverService = (DriverService)
                injector.getInstance(DriverService.class);
        CarService carService = (CarService)
                injector.getInstance(CarService.class);
        ManufacturerService manufacturerService = (ManufacturerService)
                injector.getInstance(ManufacturerService.class);
        manufacturerService.getAll().forEach(System.out::println);

        Driver alice = new Driver("Alice","S43215678");
        Driver bob = new Driver("Bob","S12348765");
        Driver jon = new Driver("Jon","S45671287");
        //driverService.create(alice);
        //driverService.create(bob);
        //driverService.create(jon);

        Car cadillac = new Car("Cadillac Escalade");
        Car volkswagen = new Car("Volkswagen");
        Car toyotaYaris = new Car("ToyotaYarisCross");
        Car koenigseggS = new Car("Koenigsegg Supra");
        //carService.create(cadillac);
        //carService.create(volkswagen);
        //carService.create(toyotaYaris);
        // carService.create(koenigseggS);

        //Car volkswagenTiguan = new Car(5L,"Volkswagen Tiguan 2022",22L);
        //carService.update(volkswagenTiguan);

        //carService.delete(7L);
        carService.addDriverToCar(driverService.get(3L),
                carService.get(5L));
        carService.removeDriverFromCar(driverService.get(3L),
                carService.get(5L));

        carService.getAll().forEach(System.out::println);
        driverService.getAll().forEach(System.out::println);
        carService.getAllByDriver(1L).forEach(System.out::println);
    }
}
