package mate.jdbc;

import java.util.ArrayList;
import java.util.List;
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
        ManufacturerService manufacturerService = (ManufacturerService)
                injector.getInstance(ManufacturerService.class);
        manufacturerService.getAll().forEach(System.out::println);

        Driver alice = new Driver("Alice","S43215678");
        Driver bob = new Driver("Bob","S12348765");
        Driver jon = new Driver("Jon","S45671287");
        //driverService.create(alice);
        //driverService.create(bob);
        //driverService.create(jon);

        List<Driver> driversCadillac = new ArrayList();
        driversCadillac.add(driverService.get(1L));
        Car cadillac = new Car("Cadillac Escalade",19L, driversCadillac);
        Car volkswagen = new Car("Volkswagen",22L, driversCadillac);
        Car toyotaYaris = new Car("ToyotaYarisCross",20L, driversCadillac);
        Car koenigseggS = new Car("Koenigsegg Supra",21L, driversCadillac);
        //carService.create(cadillac);
        //carService.create(volkswagen);
        //carService.create(toyotaYaris);
        //carService.create(koenigseggS);

        CarService carService = (CarService)
                injector.getInstance(CarService.class);
        Car volkswagenTiguan = new Car(5L,"Volkswagen Tiguan 2022",22L);
        List<Driver> driversVolkswagen = new ArrayList();
        driversVolkswagen.add(driverService.get(3L));
        volkswagenTiguan.setDrivers(driversVolkswagen);
        carService.update(volkswagenTiguan);

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
