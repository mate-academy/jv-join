package mate.jdbc;

import java.util.ArrayList;
import mate.jdbc.lib.Injector;
import mate.jdbc.model.Car;
import mate.jdbc.model.Driver;
import mate.jdbc.model.Manufacturer;
import mate.jdbc.service.CarService;
import mate.jdbc.service.ManufacturerService;

public class Main {
    private static final Injector injector = Injector.getInstance("mate.jdbc");

    public static void main(String[] args) {
        //create drivers

        //create manufacture
        ManufacturerService manufacturerService =
                (ManufacturerService) injector.getInstance(ManufacturerService.class);
        Manufacturer manufacturerPorsche =
                manufacturerService.create(
                        new Manufacturer(1L, "Porsche", "Germany"));

        Car firstCar = new Car();
        Car secondCar = new Car();

        setCarParameters(manufacturerPorsche, firstCar, secondCar);

        CarService carService = (CarService) injector.getInstance(CarService.class);
        createCarTest(firstCar, secondCar, carService);

        getAllCarsTest(carService);

        Driver driverLuka = new Driver(1L, "Luca", "999");
        Driver driverValera = new Driver(2L, "Valera", "none");
        addDriverToCarTest(driverLuka, driverValera, firstCar, secondCar, carService);

        updateCarTest(firstCar, carService);

        getCarsByIdTest(firstCar, secondCar, carService);

        getAllByDriverTest(driverLuka, carService);

        removeCarTest(driverLuka, firstCar, carService);

        deleteCarTest(firstCar, carService);
    }

    private static void setCarParameters(
            Manufacturer manufacturerPorsche, Car firstCar, Car secondCar) {
        firstCar.setModel("911");
        firstCar.setManufacturer(manufacturerPorsche);
        firstCar.setDrivers(new ArrayList<>());
        secondCar.setModel("Macan");
        secondCar.setManufacturer(manufacturerPorsche);
        secondCar.setDrivers(new ArrayList<>());
    }

    private static void createCarTest(Car firstCar, Car secondCar, CarService carService) {
        System.out.println("Create car");
        carService.create(firstCar);
        carService.create(secondCar);
        System.out.println("--------------------------------------------");
    }

    private static void getAllCarsTest(CarService carService) {
        System.out.println("All cars");
        System.out.println(carService.getAll());
        System.out.println("--------------------------------------------");
    }

    private static void addDriverToCarTest(
            Driver driverLuka, Driver driverValera,
            Car firstCar, Car secondCar, CarService carService) {
        System.out.println("Add driver to car");
        carService.addDriverToCar(driverLuka, firstCar);
        carService.addDriverToCar(driverValera, secondCar);
        System.out.println("--------------------------------------------");
    }

    private static void updateCarTest(Car firstCar, CarService carService) {
        System.out.println("Update car");
        firstCar.setModel("TestModel");
        System.out.println(carService.update(firstCar));
        System.out.println("--------------------------------------------");
    }

    private static void getCarsByIdTest(Car firstCar, Car secondCar, CarService carService) {
        System.out.println(carService.get(firstCar.getId()));
        System.out.println(carService.get(secondCar.getId()));
        System.out.println("--------------------------------------------");
    }

    private static void getAllByDriverTest(Driver driverLuka, CarService carService) {
        System.out.println("Get all driver");
        System.out.println(carService.getAllByDriver(driverLuka.getId()));
        System.out.println("--------------------------------------------");
    }

    private static void removeCarTest(Driver driverLuka, Car firstCar, CarService carService) {
        System.out.println("remove car");
        carService.removeDriverFromCar(driverLuka, firstCar);
        System.out.println("--------------------------------------------");
    }

    private static void deleteCarTest(Car firstCar, CarService carService) {
        System.out.println("delete car");
        System.out.println(carService.delete(firstCar.getId()));
        System.out.println("--------------------------------------------");
    }
}
