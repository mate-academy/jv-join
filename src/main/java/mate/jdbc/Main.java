package mate.jdbc;

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
        Driver vova = new Driver("Vovislav", "JK9000009");
        Driver julia = new Driver("Julia", "VI155343");
        Driver fred = new Driver("Fred", "CAL43573XJ");
        DriverService driverService = (DriverService) injector
                .getInstance(DriverService.class);
        driverService.create(vova);
        driverService.create(julia);
        driverService.create(fred);

        Manufacturer pinkRocket = new Manufacturer("Pink Rocket", "Mars Confederation");
        Manufacturer apm = new Manufacturer("APM Industries", "Nauru Republic");
        ManufacturerService manufacturerService = (ManufacturerService) injector
                .getInstance(ManufacturerService.class);
        manufacturerService.create(pinkRocket);
        manufacturerService.create(apm);

        CarService carService = (CarService) injector
                .getInstance(CarService.class);
        Car bigRocket = new Car("Rocket 1000", pinkRocket, List.of(vova, julia));
        Car hoverCruiser = new Car("HoverCruiser-XXL", apm, List.of(julia, fred));
        carService.create(bigRocket);
        carService.create(hoverCruiser);
        System.out.println(carService.getAll());
        System.out.println(carService.get(bigRocket.getId()));
        carService.addDriverToCar(fred, bigRocket);
        System.out.println(carService.get(bigRocket.getId()));
        carService.removeDriverFromCar(julia, bigRocket);
        carService.delete(hoverCruiser.getId());
        System.out.println(carService.getAllByDriver(vova.getId()));
    }
}
