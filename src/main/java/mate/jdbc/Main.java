package mate.jdbc;

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
        ManufacturerService manufacturerService = (ManufacturerService)
                injector.getInstance(ManufacturerService.class);
        DriverService driverService = (DriverService)
                injector.getInstance(DriverService.class);
        Manufacturer manufacturerToyota = new Manufacturer(null, "Toyota", "Japan");
        manufacturerService.create(manufacturerToyota);

        Driver driverVasiliy = new Driver(null, "Vasiliy", "BC12345");
        Driver driverIvan = new Driver(null, "Ivan", "CB54321");
        driverService.create(driverVasiliy);
        driverService.create(driverIvan);
        System.out.println(driverService.getAll());

        CarService carService = (CarService)
                injector.getInstance(CarService.class);
        Car carYaris = new Car();
        carYaris.setModel("Yaris hybrid");
        carYaris.setManufacturer(manufacturerToyota);
        carService.addDriverToCar(driverVasiliy, carYaris);
        carService.create(carYaris);
        carService.get(carYaris.getId());

        Car carRav4 = new Car();
        carRav4.setModel("Rav4");
        carRav4.setManufacturer(manufacturerToyota);
        carService.addDriverToCar(driverVasiliy, carRav4);
        carService.create(carRav4);
        System.out.println(carService.getAll());

        carService.addDriverToCar(driverIvan, carRav4);
        carService.update(carRav4);
        carService.getAllByDriver(driverVasiliy.getId());
        carService.delete(carYaris.getId());
        System.out.println(carService.getAll());
    }
}
