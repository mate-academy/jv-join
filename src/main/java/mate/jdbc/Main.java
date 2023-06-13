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
    private static DriverService driverService;
    private static ManufacturerService manufacturerService;
    private static CarService carService;

    public static void main(String[] args) {
        driverService = (DriverService) injector.getInstance(DriverService.class);
        manufacturerService = (ManufacturerService) injector.getInstance(ManufacturerService.class);
        carService = (CarService) injector.getInstance(CarService.class);

        Manufacturer audi = new Manufacturer("Audi", "Germany");
        manufacturerService.create(audi);
        Manufacturer bmw = new Manufacturer("BMW", "Germany");
        manufacturerService.create(bmw);

        Driver dominicToretto1 = new Driver("Dominic","family");
        driverService.create(dominicToretto1);
        Driver ivanych1 = new Driver("Ivanych", "ekarnuyBabay");
        driverService.create(ivanych1);
        Driver taxiDriver1 = new Driver("Daniel", "Marselle");
        driverService.create(taxiDriver1);

        List<Driver> listForFirsCar = new ArrayList<>();
        listForFirsCar.add(dominicToretto1);
        listForFirsCar.add(ivanych1);
        List<Driver> listForSecondCar = new ArrayList<>();
        listForSecondCar.add(taxiDriver1);

        Car firstCar = new Car("Q7", audi,listForFirsCar);
        Car secondCar = new Car("X5", bmw, listForSecondCar);
        carService.create(firstCar);
        carService.create(secondCar);

        System.out.println(carService.get(firstCar.getId()));

        Car carForUpdate = new Car(firstCar.getId(),"Q5", audi, listForFirsCar);
        carService.update(carForUpdate);

        carService.addDriverToCar(taxiDriver1,carService.get(firstCar.getId()));
        System.out.println(carService.get(firstCar.getId()));
        carService.removeDriverFromCar(taxiDriver1, carService.get(firstCar.getId()));
        System.out.println(carService.get(firstCar.getId()));
        System.out.println(carService.getAllByDriver(dominicToretto1.getId()));
    }
}
