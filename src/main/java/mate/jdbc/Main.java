package mate.jdbc;

import mate.jdbc.lib.Injector;
import mate.jdbc.model.Car;
import mate.jdbc.service.CarService;
import mate.jdbc.service.DriverService;
import mate.jdbc.service.ManufacturerService;

public class Main {
    private static final Injector injector = Injector.getInstance("mate.jdbc");

    public static void main(String[] args) {
        CarService carService = (CarService)injector
                    .getInstance(CarService.class);
        DriverService driverService = (DriverService)injector
                    .getInstance(DriverService.class);
        ManufacturerService manufacturerService = (ManufacturerService)injector
                    .getInstance(ManufacturerService.class);
        Car tavria = new Car("ЗАЗ-1102",manufacturerService.get(1L));
        Car toyota = new Car("Toyota Corolla 1.6", manufacturerService.get(3L));
        carService.create(tavria);
        carService.create(toyota);
        carService.addDriverToCar(driverService.get(1L), tavria);
        carService.addDriverToCar(driverService.get(2L), tavria);
        carService.addDriverToCar(driverService.get(3L), toyota);
        carService.addDriverToCar(driverService.get(3L), tavria);
        System.out.println(carService.getAll());
        System.out.println(carService.getAllByDriver(3L));
        carService.removeDriverFromCar(driverService.get(3L), tavria);
        tavria.setModel("Maserati");
        tavria.setManufacturer(manufacturerService.get(2L));
        carService.update(tavria);
        System.out.println(carService.getAll());
    }
}
