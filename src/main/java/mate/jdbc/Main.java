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
        ManufacturerService manufacturerService = (ManufacturerService) injector
                .getInstance(ManufacturerService.class);
        Manufacturer opelManufacturer = new Manufacturer("Opel", "Germany");
        Manufacturer createdManufacturer = manufacturerService.create(opelManufacturer);
        Manufacturer manufacturerById = manufacturerService.get(createdManufacturer.getId());
        List<Manufacturer> allManufacturersList = manufacturerService.getAll();
        Manufacturer hyundaiManufacturer
                = new Manufacturer("Hyundai", "South Korea");
        hyundaiManufacturer.setId(2L);
        Manufacturer updatedManufacturer = manufacturerService.update(hyundaiManufacturer);
        boolean isDeletedManufacturer = manufacturerService.delete(manufacturerById.getId());
        DriverService driverService = (DriverService) injector.getInstance(DriverService.class);
        Driver driverJohn
                = new Driver("John", "45345345");
        Driver createdDriver = driverService.create(driverJohn);
        Driver driverById = driverService.get(createdDriver.getId());
        List<Driver> allDriversList = driverService.getAll();
        Driver driverAlice
                = new Driver("Alice", "34343434");
        driverAlice.setId(2L);
        Driver updatedDriver = driverService.update(driverAlice);
        boolean isDeletedDriver = driverService.delete(driverById.getId());
        CarService carService = (CarService) injector.getInstance(CarService.class);
        Car nissanAlmeraModel = new Car();
        Manufacturer nissanManufacturer
                = manufacturerService.create(new Manufacturer("Nissan", "Japan"));
        nissanAlmeraModel.setModel("Almera");
        nissanAlmeraModel.setManufacturer(nissanManufacturer);
        List<Driver> nissanAlmeraDriversList = new ArrayList<>(List.of(driverJohn, driverAlice));
        nissanAlmeraModel.setDrivers(nissanAlmeraDriversList);
        Car createdCar = carService.create(nissanAlmeraModel);
        Car carById = carService.get(createdCar.getId());
        List<Car> allCars = carService.getAll();
        nissanAlmeraModel.setModel("Maxima");
        Car updatedCar = carService.update(nissanAlmeraModel);
        boolean isDeleteCar = carService.delete(createdCar.getId());
        Driver driverVera = driverService.create(new Driver("Vera", "3423434342"));
        carService.addDriverToCar(driverVera, createdCar);
        carService.removeDriverFromCar(driverAlice, createdCar);
    }
}
