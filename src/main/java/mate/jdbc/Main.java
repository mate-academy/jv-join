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

        Manufacturer zaz = new Manufacturer("ZAZ", "Ukraine");
        Manufacturer chery = new Manufacturer("Chery", "China");
        Manufacturer vaz = new Manufacturer("VAZ", "Russia");
        System.out.println("Created manufacturers: " + zaz + chery + vaz);

        Driver ivanov = new Driver("Ivanov Ivan", "AM151AM");
        Driver petrov = new Driver("Petrov Petr", "MA515MA");
        Driver olegov = new Driver("Olegov Oleg", "AN211NA");
        System.out.println("Created drivers: " + ivanov + petrov + olegov);

        List<Driver> drivers = new ArrayList<>();
        drivers.add(ivanov);
        drivers.add(petrov);
        drivers.add(olegov);

        Car niva = new Car("Niva", drivers, vaz);
        Car vida = new Car("Vida", drivers, zaz);
        Car amulet = new Car("Amulet", drivers, chery);
        System.out.println("Created cars: " + niva + vida + amulet);

        ManufacturerService manufacturerService = (ManufacturerService)
                injector.getInstance(ManufacturerService.class);
        DriverService driverService = (DriverService) injector.getInstance(DriverService.class);
        CarService carService = (CarService) injector.getInstance(CarService.class);

        Manufacturer zazManufacturer = manufacturerService.create(zaz);
        Manufacturer cheryManufacturer = manufacturerService.create(chery);
        Manufacturer vazManufacturer = manufacturerService.create(vaz);
        System.out.println("Added manufacturers to DB: "
                + zazManufacturer + cheryManufacturer + vazManufacturer);

        Driver ivanovDriver = driverService.create(ivanov);
        Driver petrovDriver = driverService.create(petrov);
        Driver olegovDriver = driverService.create(olegov);
        System.out.println("Added drivers to DB: " + ivanovDriver + petrovDriver + olegovDriver);

        Car nivaCar = carService.create(niva);
        Car vidaCar = carService.create(vida);
        Car amuletCar = carService.create(amulet);
        System.out.println("Added cars to DB: " + nivaCar + vidaCar + amuletCar);

        carService.addDriverToCar(ivanov, niva);
        carService.addDriverToCar(petrov, vida);
        carService.addDriverToCar(olegov, amulet);

        carService.removeDriverFromCar(ivanov, niva);

        List<Driver> driverList = driverService.getAll();
        System.out.println("Got drivers list from DB: " + driverList);

        Driver getPetrov = driverService.get(petrov.getId());
        System.out.println("Got driver petrov from DB: " + getPetrov);

        List<Manufacturer> manufacturerList = manufacturerService.getAll();
        System.out.println("Got manufacturers list from DB: " + manufacturerList);

        Manufacturer getZaz = manufacturerService.get(zaz.getId());
        System.out.println("Got ZAZ manufacturer from DB: " + getZaz);

        List<Car> carList = carService.getAll();
        System.out.println("Got cars list from DB: " + carList);
        Car getAmulet = carService.get(amulet.getId());
        System.out.println("Got amulet from DB: " + getAmulet);

        Long olegovId = olegov.getId();
        List<Car> carsByOlegov = carService.getAllByDriver(olegovId);
        System.out.println("Got cars list by driver id from DB: " + carsByOlegov);

        chery.setCountry("Japan");
        Manufacturer cheryUpdate = manufacturerService.update(chery);
        System.out.println("Updated manufacturer info in DB: " + cheryUpdate);
        petrov.setLicenseNumber("456887");
        Driver petrovUpdate = driverService.update(petrov);
        System.out.println("Updated driver info in DB: " + petrovUpdate);
        niva.setModel("SuperCar");
        Car nivaUpdate = carService.update(niva);
        System.out.println("Updated niva info in DB: " + nivaUpdate);

        boolean deleteManufacturer = manufacturerService.delete(chery.getId());
        System.out.println("Deleted chery manufacturer: " + deleteManufacturer);
        boolean deleteDriver = driverService.delete(petrov.getId());
        System.out.println("Deleted driver Petrov: " + deleteDriver);
        boolean deleteCar = carService.delete(niva.getId());
        System.out.println("Deleted car Niva: " + deleteCar);
    }
}
