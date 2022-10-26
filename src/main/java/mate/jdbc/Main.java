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

        CarService carService = (CarService) injector.getInstance(CarService.class);
        DriverService driverService = (DriverService) injector.getInstance(DriverService.class);
        ManufacturerService manufacturerService = (ManufacturerService)
                injector.getInstance(ManufacturerService.class);

        Manufacturer bmw = manufacturerService.get(16L);
        Manufacturer ford = manufacturerService.get(17L);
        Manufacturer vw = manufacturerService.get(18L);

        Driver jack = driverService.get(5L);
        Driver max = driverService.get(6L);
        Car bmwI3 = carService.get(5L);
        Car fordFiesta = carService.get(2L);

        //carService.addDriverToCar(jack, bmwI3);
        //carService.create(bmwI3);
        //System.out.println(carService.get(2L));
        //System.out.println(carService.getAllByDriver(4L));
        //System.out.println(fordFiesta);
        //carService.removeDriverFromCar(max, bmwI3);
        //System.out.println(fordFiesta);
        //carService.delete(1L);
        //System.out.println(carService.get(4L));
    }
}
