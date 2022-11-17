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
        ManufacturerService manufacturerService =
                (ManufacturerService) injector.getInstance(ManufacturerService.class);
        Manufacturer bmwManufacturer = new Manufacturer("BMW", "Germany");
        Manufacturer lamborghiniManufacturer = new Manufacturer("Lamborghini", "Italy");
        manufacturerService.create(bmwManufacturer);
        manufacturerService.create(lamborghiniManufacturer);

        DriverService driverService = (DriverService) injector.getInstance(DriverService.class);
        Driver iraDriver = new Driver("Ira", "2812741248");
        Driver denisDriver = new Driver("Denis", "1987349304");
        Driver stanislawDriver = new Driver("Stanislaw", "8123749231");
        Driver angelaDriver = new Driver("Angela", "8127349123");
        driverService.create(iraDriver);
        driverService.create(denisDriver);
        driverService.create(stanislawDriver);
        driverService.create(angelaDriver);

        CarService carService = (CarService) injector.getInstance(CarService.class);
        Car bmwM5Car = new Car("M5", bmwManufacturer,
                List.of(iraDriver, angelaDriver));
        Car lamborghiniSvjCar = new Car("SVJ", lamborghiniManufacturer,
                List.of(denisDriver, stanislawDriver));
        carService.create(bmwM5Car);
        carService.create(lamborghiniSvjCar);

        System.out.println("------All cars------");
        List<Car> cars = carService.getAll();
        for (Car car: cars) {
            System.out.println(car);
        }
        System.out.println("------Updated BMW M5 car------");
        carService.removeDriverFromCar(iraDriver, bmwM5Car);
        carService.addDriverToCar(denisDriver, bmwM5Car);
        System.out.println(carService.get(bmwM5Car.getId()));

        System.out.println("------All cars with Denis driver------");
        List<Car> carsByDenisDriver = carService.getAllByDriver(denisDriver.getId());
        for (Car car: carsByDenisDriver) {
            System.out.println(car);
        }
    }
}
