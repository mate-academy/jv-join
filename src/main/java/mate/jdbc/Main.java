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
        DriverService driverService = (DriverService) injector.getInstance(DriverService.class);
        Driver igorPetrovych = new Driver("Igor Petrovych", "DL123-123");
        Driver johnKuvaldin = new Driver("John Kuvaldin", "DL321-321");
        driverService.create(igorPetrovych);
        driverService.create(johnKuvaldin);
        Manufacturer zazManufacturer = new Manufacturer("ZAZ", "Ukraine");
        ManufacturerService manufacturerService
                = (ManufacturerService) injector.getInstance(ManufacturerService.class);
        manufacturerService.create(zazManufacturer);
        Car taxi = new Car("Slavuta", zazManufacturer);
        CarService carService = (CarService) injector.getInstance(CarService.class);
        carService.create(taxi);
        carService.addDriverToCar(igorPetrovych, taxi);
        carService.addDriverToCar(johnKuvaldin, taxi);
        Car oldCar = carService.get(1L);
        carService.addDriverToCar(igorPetrovych, oldCar);
        Car secondCar = carService.get(3L);
        carService.addDriverToCar(igorPetrovych, secondCar);
        carService.delete(secondCar.getId());
    }
}
