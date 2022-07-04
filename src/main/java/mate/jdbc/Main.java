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
        ManufacturerService manufacturerService =
                (ManufacturerService) injector.getInstance(ManufacturerService.class);
        Manufacturer manufacturerChallenger = new Manufacturer("Challender", "USA");
        Manufacturer manufacturerReatta = new Manufacturer("Reatta", "Germany");
        manufacturerService.create(manufacturerChallenger);
        manufacturerService.create(manufacturerReatta);
    
        Driver driverIvan = new Driver("Ivan", "AZ12345");
        Driver driverVova = new Driver("Vova", "BZ53623");
        Driver driverIra = new Driver("Ira", "ZK54637");
        Driver driverPavlo = new Driver("Pavlo", "KR83926");
    
        DriverService driverService =
                (DriverService) injector.getInstance(DriverService.class);
        driverService.create(driverIvan);
        driverService.create(driverVova);
        driverService.create(driverIra);
        driverService.create(driverPavlo);
    
        List<Driver> driversListDodge = new ArrayList<>();
        List<Driver> driversListBuick = new ArrayList<>();
        
        driversListDodge.add(driverIvan);
        driversListDodge.add(driverVova);
        driversListBuick.add(driverIra);
        driversListBuick.add(driverPavlo);
    
        CarService carService =
                (CarService) injector.getInstance(CarService.class);
        Car carModelDodge = new Car("Challenger", manufacturerChallenger, driversListDodge);
        Car carModelBuick = new Car("Reatta", manufacturerReatta, driversListBuick);
        carService.create(carModelDodge);
        carService.create(carModelBuick);
        
        carService.getAllByDriver(carModelDodge.getId());
        carModelDodge.setManufacturer(manufacturerService.get(manufacturerChallenger.getId()));
        carService.update(carModelDodge);
        carService.getAll().forEach(System.out::println);
        carService.removeDriverFromCar(driverVova, carModelDodge);
        carService.getAllByDriver(driverIvan.getId()).forEach(System.out::println);
        
        carService.delete(carModelBuick.getId());
    }
}
