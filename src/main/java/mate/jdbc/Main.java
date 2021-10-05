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
        ManufacturerService manufacturerService
                = (ManufacturerService) injector.getInstance(ManufacturerService.class);
        Manufacturer honda = new Manufacturer("Honda", "Japan");
        manufacturerService.create(honda);
        Manufacturer bmw = new Manufacturer("BMW", "Germany");
        manufacturerService.create(bmw);
        System.out.println(manufacturerService.getAll());
        DriverService driverService = (DriverService) injector.getInstance(DriverService.class);
        Driver specialDriver = new Driver("Jack", "AC889885t");
        driverService.create(specialDriver);
        System.out.println(driverService.getAll());
        CarService carService = (CarService) injector.getInstance(CarService.class);
        Car civic = new Car();
        civic.setModel("Honda Civic");
        civic.setManufacturer(bmw);
        carService.create(civic);
        carService.addDriverToCar(specialDriver, civic);
        System.out.println(carService.getAllByDriver(1L));
    }
}
