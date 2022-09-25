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

        //Create a manufacturers...
        ManufacturerService manufacturerService = (ManufacturerService)
                injector.getInstance(ManufacturerService.class);
        Manufacturer manufacturerToyota = new Manufacturer("TOYOTA", "JAPAN");
        Manufacturer manufacturerFord = new Manufacturer("FORD", "USA");
        Manufacturer manufacturerBmw = new Manufacturer("BMW", "GERMANY");
        manufacturerService.create(manufacturerToyota);
        manufacturerService.create(manufacturerFord);
        manufacturerService.create(manufacturerBmw);
        manufacturerService.delete(7L);

        //Create, delete and update a cars...
        CarService carService = (CarService) injector.getInstance(CarService.class);
        Car carFocus = new Car("Focus", manufacturerService.get(9L));
        Car carCorolla = new Car("Corolla", manufacturerService.get(5L));
        Car carMustang = new Car("Mustang", manufacturerService.get(9L));
        carService.create(carFocus);
        carService.create(carCorolla);
        carService.create(carMustang);
        Car updateCarCorolla = new Car(2L,"Corolla", manufacturerService.get(5L));
        carService.update(updateCarCorolla);
        carService.delete(4L);

        //Create a drivers...
        DriverService driverService = (DriverService) injector.getInstance(DriverService.class);
        Driver driverBob = new Driver("Bob", "000666");
        Driver driverJack = new Driver("Jack", "000365");
        Driver driverBill = new Driver("Bill", "000824");
        driverService.create(driverBob);
        driverService.create(driverJack);
        driverService.create(driverBill);

        //Add drivers in to car...
        carService.addDriverToCar(driverService.get(7L), carService.get(2L));
        carService.addDriverToCar(driverService.get(5L), carService.get(2L));
        carService.addDriverToCar(driverService.get(7L), carService.get(1L));
        carService.addDriverToCar(driverService.get(8L), carService.get(1L));
        //Remove drivers in to car...
        carService.removeDriverFromCar(driverService.get(7L), carService.get(2L));

        carService.getAllByDriver(7L).forEach(System.out::println);
    }
}
