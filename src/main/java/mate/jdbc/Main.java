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
        List<Driver> toyotaSupraDriversList = new ArrayList<>();
        Driver bob = driverService.create(new Driver("Bob", "12345678"));
        Driver john = driverService.create(new Driver("John", "88005353535"));
        toyotaSupraDriversList.add(bob);
        toyotaSupraDriversList.add(john);
        List<Driver> nissanGtrDriversList = new ArrayList<>();
        Driver ann = driverService.create(new Driver("Ann", "cheburek"));
        Driver alex = driverService.create(new Driver("Alex", "fritz"));
        nissanGtrDriversList.add(john);
        nissanGtrDriversList.add(ann);
        List<Driver> audiEtronDriversList = new ArrayList<>();
        Driver serhiy = driverService.create(new Driver("Serhiy", "plkoijhu"));
        audiEtronDriversList.add(alex);
        audiEtronDriversList.add(serhiy);
        ManufacturerService manufacturerService =
                (ManufacturerService) injector.getInstance(ManufacturerService.class);
        CarService carService = (CarService) injector.getInstance(CarService.class);
        Manufacturer toyota = manufacturerService.create(new Manufacturer("Toyota", "Japan"));
        Manufacturer nissan = manufacturerService.create(new Manufacturer("Nissan", "Japan"));
        Manufacturer audi = manufacturerService.create(new Manufacturer("Audi", "Germany"));
        Car audiEtronGt = new Car("e-tron GT", audi, audiEtronDriversList);
        Car toyotaSupra = new Car("Supra", toyota, toyotaSupraDriversList);
        Car nissanGtr = new Car("GTR", nissan, nissanGtrDriversList);
        audiEtronGt = carService.create(audiEtronGt);
        toyotaSupra = carService.create(toyotaSupra);
        nissanGtr = carService.create(nissanGtr);
        System.out.println("All cars: " + carService.getAll() + "\n");
        carService.delete(toyotaSupra.getId());
        System.out.println("Deleting Toyota Supra: " + carService.getAll() + "\n");
        carService.addDriverToCar(bob, audiEtronGt);
        System.out.println("Adding Bob to Audi e-tron GT and getting all cars by driver Bob: "
                + carService.getAllByDriver(bob.getId()) + "\n");
        carService.removeDriverFromCar(bob, audiEtronGt);
        System.out.println("Removing Bob from Audi e-tron GT and getting all cars by driver Bob: "
                + carService.getAllByDriver(bob.getId()) + "\n");
        System.out.println("carService.get() test for Nissan GTR: "
                + carService.get(nissanGtr.getId()) + "\n");
        nissanGtr.setModel("Leaf");
        System.out.println("Update test by changing car model from GTR to Leaf: "
                + carService.update(nissanGtr));
    }
}
