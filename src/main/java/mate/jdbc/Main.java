package mate.jdbc;

import mate.jdbc.lib.Injector;
import mate.jdbc.model.Car;
import mate.jdbc.model.Driver;
import mate.jdbc.model.Manufacturer;
import mate.jdbc.service.CarService;

public class Main {
    private static final Injector injector = Injector.getInstance("mate.jdbc");

    public static void main(String[] args) {

        Manufacturer peugeotGroup = new Manufacturer(25L, "peugeot group", "France");

        Car peugeot = new Car();
        peugeot.setModel("peugeot");
        peugeot.setManufacturer(peugeotGroup);

        CarService carService = (CarService) injector.getInstance(CarService.class);

        peugeot = carService.create(peugeot); // create Car.

        System.out.println(carService.get(peugeot.getId())); // print it

        Driver vitalic = new Driver(220L, "Vitalic", "01228");
        carService.addDriverToCar(vitalic, peugeot); // add 1 Drivers to it.
        peugeot = carService.get(peugeot.getId());

        Driver oles = new Driver(221L, "Oles", "01229");
        carService.addDriverToCar(oles, peugeot); // add 2 Drivers to it.
        peugeot = carService.get(peugeot.getId());

        System.out.println(peugeot); // print it - you should see 2 drivers inside

        carService.removeDriverFromCar(oles, peugeot); // delete one driver
        peugeot = carService.get(peugeot.getId());

        System.out.println(peugeot); //print it - you should see 1 driver inside

        System.out.println(carService.getAllByDriver(1L));

        carService.delete(4L);

        carService.update(peugeot);

        System.out.println(carService.getAll());
    }
}
