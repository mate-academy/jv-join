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
        Manufacturer volvo = new Manufacturer("Volvo", "Sweden");
        Manufacturer tesla = new Manufacturer("Tesla", "USA");
        ManufacturerService manufacturerService
                = (ManufacturerService) injector.getInstance(ManufacturerService.class);
        manufacturerService.create(volvo);
        manufacturerService.create(tesla);

        Driver nina = new Driver("Nina", "AB6532");
        Driver olya = new Driver("Olya", "UY777");
        Driver bogdan = new Driver("Bogdan", "CD685");
        List<Driver> drivers = new ArrayList<>();
        drivers.add(nina);
        drivers.add(olya);
        drivers.add(bogdan);
        DriverService driverService = (DriverService) injector.getInstance(DriverService.class);
        drivers.forEach(driverService::create);

        Car carVolvo = new Car();
        carVolvo.setModel("Volvo XC40");
        carVolvo.setManufacturer(volvo);
        carVolvo.setDrivers(drivers);

        Car carTesla = new Car();
        carTesla.setModel("Tesla Roadster");
        carTesla.setManufacturer(tesla);
        carTesla.setDrivers(drivers);

        CarService carService = (CarService) injector.getInstance(CarService.class);
        carService.create(carVolvo);
        carService.create(carTesla);
        System.out.println(carService.get(carTesla.getId()));
        carVolvo.setModel("Volvo C30");
        carService.update(carVolvo);
        carService.delete(carTesla.getId());
        carService.getAll().forEach(System.out::println);
        Driver ivan = new Driver("Ivan", "IKw3456");
        driverService.create(ivan);
        carService.addDriverToCar(ivan, carVolvo);
        carService.removeDriverFromCar(bogdan, carVolvo);
        System.out.println(System.lineSeparator());
        carService.getAllByDriverId(olya.getId()).forEach(System.out::println);
    }
}
