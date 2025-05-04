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
        ManufacturerService manufacturerService = (ManufacturerService) injector
                .getInstance(ManufacturerService.class);
        Manufacturer firstManufacturer = new Manufacturer("Fiat", "Italy");
        Manufacturer secondManufacturer = new Manufacturer("Rolls-Royce", "GB");
        firstManufacturer = manufacturerService.create(firstManufacturer);
        secondManufacturer = manufacturerService.create(secondManufacturer);
        Driver firstDriver = new Driver("Statham", "9900");
        Driver secondDriver = new Driver("Bourne ", "0088");
        Driver thirdDriver = new Driver("Reeves ", "5555");
        firstDriver = driverService.create(firstDriver);
        secondDriver = driverService.create(secondDriver);
        thirdDriver = driverService.create(thirdDriver);
        Driver fourthDriver = new Driver("Samuel L. Jackson ", "4444");
        fourthDriver = driverService.create(fourthDriver);
        List<Driver> firstDriverList = new ArrayList<>();
        CarService carService = (CarService) injector.getInstance(CarService.class);
        Car firstCar = new Car("Phantom", firstManufacturer, firstDriverList);
        firstCar = carService.create(firstCar);
        carService.addDriverToCar(firstDriver, firstCar);
        carService.addDriverToCar(secondDriver, firstCar);
        List<Driver> secondDriverList = new ArrayList<>();
        Car secondCar = new Car("Challenger", secondManufacturer, secondDriverList);
        carService.create(secondCar);
        carService.addDriverToCar(thirdDriver, secondCar);
        carService.addDriverToCar(fourthDriver, secondCar);
        Car carToTestGet = carService.get(secondCar.getId());
        System.out.println(carToTestGet);
        carService.removeDriverFromCar(thirdDriver, secondCar);
        carToTestGet = carService.get(secondCar.getId());
        System.out.println(carToTestGet);
        List<Driver> driverListToUpdate = new ArrayList<>();
        driverListToUpdate.add(thirdDriver);
        driverListToUpdate.add(secondDriver);
        Car carToUpdate = new Car("Continental", secondManufacturer, driverListToUpdate);
        carToUpdate.setId(firstCar.getId());
        carService.update(carToUpdate);
        carService.delete(secondCar.getId());
        List<Car> cars = carService.getAll();
        cars.forEach(System.out::println);
    }
}
