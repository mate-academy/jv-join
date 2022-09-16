package mate.jdbc;

import mate.jdbc.lib.Injector;
import mate.jdbc.model.Car;
import mate.jdbc.model.Driver;
import mate.jdbc.model.Manufacturer;
import mate.jdbc.service.CarService;
import mate.jdbc.service.DriverService;
import mate.jdbc.service.ManufacturerService;
import mate.jdbc.service.ManufacturerServiceImpl;

import java.util.ArrayList;
import java.util.List;


public class Main {
    public static final Injector injector = Injector.getInstance("mate.jdbc");

    public static void main(String[] args) {
        CarService carService = (CarService) injector.getInstance(CarService.class);
        ManufacturerService manufacturerService
                = (ManufacturerService) injector.getInstance(ManufacturerService.class);
        DriverService driverService = (DriverService) injector.getInstance(DriverService.class);

/*        // create car
        Driver max = new Driver();
        max.setName("Max");
        max.setLicenseNumber("m444");
        driverService.create(max);

        Driver dan = new Driver();
        dan.setName("Dan");
        dan.setLicenseNumber("d555");
        driverService.create(dan);

        List<Driver> driversForLexusNx300 = new ArrayList<>();
        driversForLexusNx300.add(max);
        driversForLexusNx300.add(dan);

        Manufacturer lexusManufacturer = new Manufacturer();
        lexusManufacturer.setName("Lexus");
        lexusManufacturer.setCountry("Japan");
        manufacturerService.create(lexusManufacturer);

        Car lexusNx300 = new Car();
        lexusNx300.setManufacturer(lexusManufacturer);
        lexusNx300.setModel("nx300");
        lexusNx300.setDriverList(driversForLexusNx300);

        carService.create(lexusNx300);*/

/*        // get car
        System.out.println(carService.get(2L));*/

/*        // get all cars
        carService.getAll().forEach(System.out::println);*/

/*        // update car
        Car car = new Car();
        car.setId(2L);
        car.setModel("mini");

        Manufacturer manufacturer = new Manufacturer();
        manufacturer.setId(10L);
        car.setManufacturer(manufacturer);

        Driver von = new Driver();
        von.setId(12L);
        Driver tim = new Driver();
        tim.setId(11L);
        List<Driver> driverList = new ArrayList<>();
        driverList.add(von);
        driverList.add(tim);
        car.setDriverList(driverList);

        carService.update(car);*/

/*        // delete car
        System.out.println(carService.delete(2L));*/

        // get all cars by driver
        carService.getAllByDriver(11L).forEach(System.out::println);
    }
}
