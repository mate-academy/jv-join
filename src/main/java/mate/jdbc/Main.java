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
        Manufacturer chevrolet = new Manufacturer();
        chevrolet.setName("General Motors");
        chevrolet.setCountry("USA");
        ManufacturerService manufacturerService
                = (ManufacturerService) injector.getInstance(ManufacturerService.class);
        System.out.println("---Create New Manufacturer---");
        System.out.println(manufacturerService.create(chevrolet));

        Driver johnConnor = new Driver();
        johnConnor.setName("John Connor");
        johnConnor.setLicenseNumber("JC_111");
        Driver madMax = new Driver();
        madMax.setName("Mad Max");
        madMax.setLicenseNumber("MM_222");
        Driver sergeyShestaka = new Driver();
        sergeyShestaka.setName("Sergey Shestaka");
        sergeyShestaka.setLicenseNumber("SS_333");
        DriverService driverService
                = (DriverService) injector.getInstance(DriverService.class);
        System.out.println("---Create new Drivers---");
        System.out.println(driverService.create(johnConnor));
        System.out.println(driverService.create(madMax));
        System.out.println(driverService.create(sergeyShestaka));

        List<Driver> driversList = new ArrayList<>();
        driversList.add(johnConnor);
        driversList.add(madMax);
        driversList.add(sergeyShestaka);

        Car chevroletCorvette = new Car();
        chevroletCorvette.setModel("Chevrolet Corvette C1");
        chevroletCorvette.setManufacturer(chevrolet);
        chevroletCorvette.setDriverList(driversList);

        CarService carService = (CarService) injector.getInstance(CarService.class);
        System.out.println("---Create New Car---");
        Car addedCar = carService.create(chevroletCorvette);
        System.out.println(addedCar);
        System.out.println("---Get Car # " + addedCar.getId() + "---");
        System.out.println(carService.get(addedCar.getId()));
        System.out.println(System.lineSeparator());

        System.out.println("---Get All Cars---");
        carService.getAll().forEach(System.out::println);
        System.out.println(System.lineSeparator());

        System.out.println("---Get All Cars of Driver #4---");
        carService.getAllByDriver(4L).forEach(System.out::println);

        Manufacturer manufacturerVolvo = manufacturerService.get(20L);
        Car carForUpdate = new Car();
        carForUpdate.setId(1L);
        carForUpdate.setModel("XC90");
        carForUpdate.setManufacturer(manufacturerVolvo);
        carForUpdate.setDriverList(driversList);

        System.out.println("---Update Car #1---");
        System.out.println(carService.update(carForUpdate));

        System.out.println("---Get Car #1---");
        System.out.println(carService.get(1L));

        System.out.println("---Remove driver " + madMax.getId() + " from Car #1---");
        carService.removeDriverFromCar(madMax, carForUpdate);

        System.out.println("---Get Car #1---");
        System.out.println(carService.get(1L));

        System.out.println("---Add driver " + madMax.getId() + " to Car #1---");
        carService.addDriverToCar(madMax, carForUpdate);

        System.out.println("---Get Car #1---");
        System.out.println(carService.get(1L));
    }
}
