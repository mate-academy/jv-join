package mate.jdbc;

import static mate.jdbc.util.Tools.numberLicenseGenerator;

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
        System.out.println("App.start");
        System.out.println("<-----------TEST DRIVER SERVICE----------------->");
        DriverService driverService = (DriverService) injector.getInstance(DriverService.class);
        driverService.getAll().forEach(System.out::println);
        Driver firstCreatedDriver = new Driver("Іванов Іван Іванович", numberLicenseGenerator());
        System.out.print(firstCreatedDriver + " -> ");
        driverService.create(firstCreatedDriver);
        System.out.println(firstCreatedDriver);
        System.out.println("Get by id = " + firstCreatedDriver.getId() + " from DB : "
                + driverService.get(firstCreatedDriver.getId()));
        Driver secondCreatedDriver = new Driver("Мазепа Т.П.", numberLicenseGenerator());
        driverService.create(secondCreatedDriver);
        System.out.print(secondCreatedDriver + " -> ");
        System.out.println(driverService.get(secondCreatedDriver.getId()));
        System.out.print("Update Driver to -> ");
        secondCreatedDriver.setName("П.Т. Мазепа");
        driverService.update(secondCreatedDriver);
        System.out.println(driverService.get(secondCreatedDriver.getId()));
        System.out.println("Delete Driver id = "
                + firstCreatedDriver.getId() + "  from DB ");
        if (driverService.delete(firstCreatedDriver.getId())) {
            System.out.println("Driver id = " + firstCreatedDriver.getId()
                    + " deleted successfully");
        }
        driverService.getAll().forEach(System.out::println);
        System.out.println("<----------TEST MANUFACTURER SERVICE------------------>");
        ManufacturerService manufacturerService =
                (ManufacturerService) injector.getInstance(ManufacturerService.class);
        manufacturerService.getAll().forEach(System.out::println);
        Manufacturer firstCreatedManufacturer = new Manufacturer("IBM2", "USA");
        System.out.print(firstCreatedManufacturer + " -> ");
        System.out.println(manufacturerService.create(firstCreatedManufacturer));
        System.out.println("Get by id = " + firstCreatedManufacturer.getId()
                + " from DB : " + manufacturerService.get(firstCreatedManufacturer.getId()));
        Manufacturer secondCreatedManufacturer = new Manufacturer("BMV2", "Germany");
        System.out.print(secondCreatedManufacturer + " -> ");
        manufacturerService.create(secondCreatedManufacturer);
        System.out.println(manufacturerService.get(secondCreatedManufacturer.getId()));
        System.out.print("Update Manufacturer to -> ");
        secondCreatedManufacturer.setName("VW");
        manufacturerService.update(secondCreatedManufacturer);
        System.out.println(manufacturerService.get(secondCreatedManufacturer.getId()));
        System.out.println("Delete Manufacturer id = "
                + firstCreatedManufacturer.getId() + "  from DB ");
        if (manufacturerService.delete(firstCreatedManufacturer.getId())) {
            System.out.println("Manufacturer id = " + firstCreatedManufacturer.getId()
                    + " deleted successfully");
        }
        manufacturerService.getAll().forEach(System.out::println);
        System.out.println("<--------TEST CAR SERVICE-------------------->");
        CarService carService = (CarService) injector.getInstance(CarService.class);
        carService.getAll().forEach(System.out::println);
        List<Driver> firstDriverList = new ArrayList<>(
                List.of(secondCreatedDriver,
                        firstCreatedDriver));
        Car firstCreatedCar = new Car("RENO", secondCreatedManufacturer, firstDriverList);
        System.out.println(firstCreatedCar);
        System.out.println(" ---> ");
        System.out.println(carService.create(firstCreatedCar));
        System.out.println("Get by id = " + firstCreatedCar.getId() + " from carsDB: \n"
                + carService.get(firstCreatedCar.getId()));
        firstCreatedCar.setModel("New Model");
        firstCreatedCar.setManufacturer(manufacturerService.create(
                new Manufacturer("OOOO", "FRG")));
        carService.addDriverToCar(driverService.create(
                new Driver("Б.Хмельницький", numberLicenseGenerator())), firstCreatedCar);
        carService.update(firstCreatedCar);
        System.out.println("Update it to -> ");
        System.out.println(carService.get(firstCreatedCar.getId()));
        System.out.println("Add Driver Id = " + secondCreatedDriver.getId() + " to car ");
        carService.addDriverToCar(secondCreatedDriver, firstCreatedCar);
        carService.update(firstCreatedCar);
        System.out.println("Add random driver to car :");
        carService.addDriverToCar(driverService.create(
                new Driver("Л. Кучма", numberLicenseGenerator())), firstCreatedCar);
        carService.update(firstCreatedCar);
        carService.getAllByDriver(secondCreatedDriver.getId()).forEach(System.out::println);
        carService.removeDriverFromCar(secondCreatedDriver, firstCreatedCar);
        carService.update(firstCreatedCar);
        System.out.println("Remove driver id = " + secondCreatedDriver.getId() + " from car");
        System.out.println(carService.get(firstCreatedCar.getId()));
        System.out.println("Delete Car id = " + firstCreatedCar.getId() + "  from DB ");
        if (carService.delete(firstCreatedCar.getId())) {
            System.out.println("Car id = " + firstCreatedCar.getId()
                    + " deleted successfully");
        }
        carService.getAll().forEach(System.out::println);
        System.out.println("All by driver id = 1");
        System.out.println(carService.getAllByDriver(1L));
        System.out.println("App.finish");
    }
}
