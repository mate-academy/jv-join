package mate.jdbc;

import java.util.ArrayList;
import java.util.List;
import mate.jdbc.lib.Injector;
import mate.jdbc.model.Car;
import mate.jdbc.model.Driver;
import mate.jdbc.model.Manufacturer;
import mate.jdbc.service.CarService;

public class Main {
    private static final Injector injector = Injector.getInstance("mate.jdbc");

    public static void main(String[] args) {
        CarService carService = (CarService) injector.getInstance(CarService.class);

        //----create/
//        Driver vitalic = new Driver(9L, "Vitalic", "01228");
//        Driver oles = new Driver(10L, "Oles", "01229");
//        List<Driver> listDriversPeugeot = new ArrayList<>();
//        listDriversPeugeot.add(vitalic);
//        listDriversPeugeot.add(oles);
//        Manufacturer peugeotGroup = new Manufacturer(6L, "peugeot group", "France");
//
//        Car peugeot = new Car();
//        peugeot.setModel("peugeot");
//        peugeot.setDrivers(listDriversPeugeot);
//        peugeot.setManufacturer(peugeotGroup);
//        carService.create(peugeot);
        //----create/

        //----get;
//        Car car = carService.get(15L);
//        System.out.println(car);
        //----get;

        //----getAll;
//        System.out.println(carService.getAll());
        //----getAll;



        //-----TEST!!!
        Driver Bob001 = new Driver(881L, "Bob001", "00001");
        Driver Bob002 = new Driver(882L, "Bob002", "00002");
        List<Driver> listDriversTestCar = new ArrayList<>();
        listDriversTestCar.add(Bob001);
        listDriversTestCar.add(Bob002);
        Manufacturer manufacturerTest = new Manufacturer(999L, "manufacturerTest", "countryTest");

        Car carTest = new Car();
        carTest.setId(18L);
        carTest.setModel("modelCarTest");
        carTest.setDrivers(listDriversTestCar);
        carTest.setManufacturer(manufacturerTest);
        carService.update(carTest);

//        carService.create(carTest);
        //-----TEST!!!




    }
}
