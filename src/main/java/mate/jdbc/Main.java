package mate.jdbc;

import java.util.ArrayList;
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

        ArrayList<Driver> driverList = new ArrayList<>();
        driverList.add(new Driver("Aleksandr Moskovchuk", "BAH000-111"));
        driverList.add(new Driver("Anatolii Huryk", "BAH000-222"));
        driverList.add(new Driver("Andrew Dzundza", "BAH000-333"));
        driverList.add(new Driver("Andrey Sidorkin", "BAH000-444"));
        driverList.add(new Driver("Anton Volskyi", "BAH000-555"));
        driverList.add(new Driver("Artem Slobodianiuk", "BAH000-666"));
        driverList.add(new Driver("Dany Nesterov", "BAH000-777"));
        driverList.add(new Driver("Natalia Vidzivashets", "BAH000-888"));
        Driver pavloMartsiniv = new Driver("Pavlo Martsiniv", "BAH000-999");
        driverList.add(pavloMartsiniv);
        driverList.forEach(driverService::create);

        Manufacturer volkswagenManufacturer
                = new Manufacturer("Volkswagen", "Deutschland");
        Manufacturer oldManufacturer
                = new Manufacturer("ZAZ", "Ukraine");
        ManufacturerService manufacturerService
                = (ManufacturerService) injector.getInstance(ManufacturerService.class);
        manufacturerService.create(volkswagenManufacturer);
        manufacturerService.create(oldManufacturer);

        Car taxi = new Car("Touran", volkswagenManufacturer);
        Car oldTaxi = new Car("Zaporozhets", oldManufacturer);
        CarService carService = (CarService) injector.getInstance(CarService.class);
        carService.create(taxi);
        carService.create(oldTaxi);
        driverList.forEach(e -> carService.addDriverToCar(e, taxi));
        carService.addDriverToCar(pavloMartsiniv, oldTaxi);
        carService.delete(oldTaxi.getId());
    }
}
