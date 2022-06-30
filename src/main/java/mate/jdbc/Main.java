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
        ManufacturerService manufacturerService
                = (ManufacturerService) injector.getInstance(ManufacturerService.class);
        Manufacturer teslaManufacturer = new Manufacturer("Tesla","USA");
        manufacturerService.create(teslaManufacturer);
        Manufacturer rangeRoverManufacturer = new Manufacturer("Range Rover", "Great Britain");
        manufacturerService.create(rangeRoverManufacturer);
        Manufacturer bmwManufacturer = new Manufacturer("BMW","Germany");
        manufacturerService.create(bmwManufacturer);
        DriverService driverService
                = (DriverService) injector.getInstance(DriverService.class);
        Driver driverJack = new Driver("Jack","EP2578");
        driverService.create(driverJack);
        Driver driverOrland = new Driver("Orland","SG7962");
        driverService.create(driverOrland);
        Driver driverHugo = new Driver("Hugo","GB1425");
        driverService.create(driverHugo);
        CarService carService
                = (CarService) injector.getInstance(CarService.class);
        Car carModelX = new Car("Model X",teslaManufacturer);
        List<Driver> driversForTesla = new ArrayList<>();
        driversForTesla.add(driverJack);
        carModelX.setDrivers(driversForTesla);
        carService.create(carModelX);
        Car carSportSE = new Car("Sport SE",rangeRoverManufacturer);
        List<Driver> driversForRangeRover = new ArrayList<>();
        driversForRangeRover.add(driverJack);
        driversForRangeRover.add(driverOrland);
        carSportSE.setDrivers(driversForRangeRover);
        carService.create(carSportSE);
        Car carX5 = new Car("X5",bmwManufacturer);
        List<Driver> driversForBmw = new ArrayList<>();
        driversForBmw.add(driverHugo);
        carX5.setDrivers(driversForBmw);
        carService.create(carX5);
        carService.removeDriverFromCar(driverJack,carModelX);
        carService.addDriverToCar(driverHugo,carModelX);
        Manufacturer volvoManufacturer = new Manufacturer("Volvo","Sweden");
        manufacturerService.create(volvoManufacturer);
        Car volvoCar = new Car("V60",volvoManufacturer);
        List<Driver> driversForVolvo = new ArrayList<>();
        driversForVolvo.add(driverOrland);
        volvoCar.setDrivers(driversForVolvo);
        carService.create(volvoCar);
        carService.update(volvoCar);
        carService.get(volvoCar.getId());
        carService.getAll().forEach(System.out::println);
        carService.getAllByDriver(driverOrland.getId()).forEach(System.out::println);
    }
}
