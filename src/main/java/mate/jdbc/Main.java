package mate.jdbc;

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
        Manufacturer bmw = new Manufacturer();
        bmw.setName("BMW");
        bmw.setCountry("Germany");
        
        Manufacturer audi = new Manufacturer();
        audi.setName("Audi");
        audi.setCountry("Germany");
        
        Manufacturer toyota = new Manufacturer();
        toyota.setName("Toyota");
        toyota.setCountry("Japan");
    
        ManufacturerService manufacturerService =
                (ManufacturerService) injector.getInstance(ManufacturerService.class);
        manufacturerService.create(bmw);
        manufacturerService.create(audi);
        manufacturerService.create(toyota);
    
        Driver bob = new Driver();
        bob.setName("Bob");
        bob.setLicenseNumber("123456");
        
        Driver john = new Driver();
        john.setName("John");
        john.setLicenseNumber("987654");
        
        Driver mike = new Driver();
        mike.setName("Mike");
        mike.setLicenseNumber("456321");
        
        DriverService driverService = (DriverService) injector.getInstance(DriverService.class);
        driverService.create(bob);
        driverService.create(john);
        driverService.create(mike);
    
        CarService carService = (CarService) injector.getInstance(CarService.class);
        Car bmwM5 = new Car();
        bmwM5.setModel("BMW-M5");
        bmwM5.setManufacturer(bmw);
        carService.addDriverToCar(bob, bmwM5);
        
        Car audiQ6 = new Car();
        audiQ6.setModel("Audi-Q6");
        audiQ6.setManufacturer(audi);
        carService.addDriverToCar(mike, audiQ6);
        carService.addDriverToCar(john, audiQ6);
        
        Car prius = new Car();
        prius.setModel("Toyota-Prius");
        prius.setManufacturer(toyota);
        carService.addDriverToCar(bob, prius);
        carService.addDriverToCar(mike, prius);
        
        carService.create(bmwM5);
        carService.create(audiQ6);
        carService.create(prius);
    
        System.out.println(carService.get(2L));
        carService.delete(1L);
        System.out.println(carService.getAll());
        
        Car bmwm3 = new Car();
        bmwm3.setId(1L);
        bmwm3.setModel("BMW-M8");
        bmwm3.setManufacturer(manufacturerService.get(1L));
        carService.addDriverToCar(driverService.get(2L), bmwm3);
        carService.addDriverToCar(driverService.get(3L), bmwm3);
        
        carService.update(bmwm3);
    
        System.out.println(carService.getAllByDriver(2L));
    }
}
