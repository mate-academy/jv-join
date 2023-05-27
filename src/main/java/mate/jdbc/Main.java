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

        Driver vitalic = new Driver(9L, "Vitalic", "01228");
        Driver oles = new Driver(10L, "Oles", "01229");
        List<Driver> listDriversPeugeot = new ArrayList<>();
        listDriversPeugeot.add(vitalic);
        listDriversPeugeot.add(oles);
        Manufacturer peugeotGroup = new Manufacturer(6L, "peugeot group", "France");
        Car peugeot = new Car();
        peugeot.setId(15L);
        peugeot.setModel("peugeot");
        peugeot.setDrivers(listDriversPeugeot);
        peugeot.setManufacturer(peugeotGroup);

        CarService carService = (CarService) injector.getInstance(CarService.class);

        carService.create(peugeot);

        carService.update(peugeot);

        carService.delete(1L);

        Car car = carService.get(15L);
        System.out.println(car);

        System.out.println(carService.getAll());

        System.out.println(carService.getAllByDriver(1L));
    }
}
