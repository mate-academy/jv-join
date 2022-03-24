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
    private static Injector injector = Injector.getInstance("mate.jdbc");

    public static void main(String[] args) {
        Injector injector = Injector.getInstance("mate.jdbc");
        Driver nazarii = new Driver("Nazarii Yatsiuk", "0001DC");
        Driver igor = new Driver("Igor Kharko", "0002DC");
        Driver mykola = new Driver("Mykola Yatsiuk", "4332FD");
        DriverService driverService = (DriverService) injector.getInstance(DriverService.class);
        driverService.create(nazarii);
        driverService.create(igor);
        driverService.create(mykola);
        List<Driver> firstList = new ArrayList<>();
        List<Driver> secondList = new ArrayList<>();
        firstList.add(mykola);
        firstList.add(igor);
        secondList.add(nazarii);
        secondList.add(igor);
        Manufacturer bmw = new Manufacturer("BMW", "Germany");
        Manufacturer volvo = new Manufacturer("Volvo", "Sweden");
        ManufacturerService manufacturerService =
                (ManufacturerService) injector.getInstance(ManufacturerService.class);
        manufacturerService.create(bmw);
        manufacturerService.create(volvo);
        Car bmwX5 = new Car("X5", bmw, firstList);
        Car volvoCX900 = new Car("CX900", volvo, secondList);
        CarService carService = (CarService) injector.getInstance(CarService.class);
        carService.create(bmwX5);
        carService.create(volvoCX900);
        System.out.println(carService.get(bmw.getId()));
        carService.getAll().forEach(System.out::println);
        bmwX5.setModel("X3");
        carService.update(bmwX5);
        System.out.println("Model of X5 after updating");
        System.out.println(carService.get(bmwX5.getId()));
        carService.addDriverToCar(mykola, volvoCX900);
        System.out.println("Drivers of volvo after adding");
        volvoCX900.getDrivers().forEach(System.out::println);
        carService.removeDriverFromCar(nazarii, volvoCX900);
        System.out.println("Drivers of volvo after remove");
        volvoCX900.getDrivers().forEach(System.out::println);
        carService.getAllByDriver(mykola.getId());
        System.out.println("All cars, that drives Mykola");
        carService.delete(bmw.getId());
        System.out.println("All cars after deleting");
        carService.getAll().forEach(System.out::println);
    }
}
