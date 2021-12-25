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

        Driver alice = new Driver("Alice","S43215678");
        Driver bob = new Driver("Bob","S12348765");
        Driver jon = new Driver("Jon","S45671287");
        DriverService driverService = (DriverService)
                injector.getInstance(DriverService.class);
        //driverService.create(alice);
        //driverService.create(bob);
        //driverService.create(jon);

        //Driver bob = new Driver(5L,"alice","S99887766");
        //driverService.update(bob);

        //System.out.println(driverService.get(3L));

        //System.out.println(driverService.delete(5L));
        Car cadillac = new Car("Cadillac Escalade");
        Car volkswagen = new Car("Volkswagen");
        Car toyotaYaris = new Car("ToyotaYarisCross");
        Car koenigsegg = new Car("Koenigsegg");

        CarService carService = (CarService)
                injector.getInstance(CarService.class);
        //carService.create(cadillac);
        //carService.create(volkswagen);
        //carService.create(toyotaYaris);
        //carService.create(koenigsegg);

        System.out.println(carService.get(3L));

        //Car volkswagenTiguan = new Car(5L,"Volkswagen Tiguan 2021");
        //carService.update(volkswagenTiguan);

        //carService.delete(2L);
        //carService.addDriverToCar(3L,1L);
        //carService.removeDriverFromCar(3L, 1L);

        carService.getAll().forEach(System.out::println);

        driverService.getAll().forEach(System.out::println);

        ManufacturerService manufacturerService = (ManufacturerService)
                injector.getInstance(ManufacturerService.class);
        manufacturerService.getAll().forEach(System.out::println);

        carService.getAllByDriver(2L).forEach(System.out::println);
    }
}
