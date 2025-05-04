package mate.jdbc;

import mate.jdbc.lib.Injector;
import mate.jdbc.model.Car;
import mate.jdbc.model.Driver;
import mate.jdbc.model.Manufacturer;
import mate.jdbc.service.CarService;
import mate.jdbc.service.DriverService;
import mate.jdbc.service.ManufacturerService;

public class Main {

    private static Injector injector = Injector.getInstance("mate.jdbc");

    public static void main(String[] args) {
        ManufacturerService manufacturerService = (ManufacturerService)
                injector.getInstance(ManufacturerService.class);
        DriverService driverService = (DriverService)
                injector.getInstance(DriverService.class);
        Manufacturer manufacturerAudi = new Manufacturer(null, "Hans", "Germany");
        manufacturerService.create(manufacturerAudi);
        Driver driverStepan = new Driver(null, "Stepan", "17495");
        Driver driverPavel = new Driver(null, "Pavel", "84920");
        driverService.create(driverStepan);
        driverService.create(driverPavel);
        driverService.getAll();

        CarService carService = (CarService)
                injector.getInstance(CarService.class);
        Car audi = new Car();
        audi.setModel("A4");
        audi.setManufacturer(manufacturerAudi);
        carService.addDriverToCar(driverStepan, audi);
        carService.create(audi);
        carService.get(audi.getId());

        Car secondAudi = new Car();
        secondAudi.setModel("Q7");
        secondAudi.setManufacturer(manufacturerAudi);
        carService.addDriverToCar(driverStepan, secondAudi);
        carService.create(secondAudi);
        carService.getAll();

        carService.addDriverToCar(driverPavel, secondAudi);
        carService.update(secondAudi);
        carService.getAllByDriver(driverStepan.getId());
        carService.delete(audi.getId());
        carService.getAll();
    }
}
