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
        DriverService driverService = (DriverService) injector.getInstance(DriverService.class);
        Driver ihor = new Driver("Ihor", "111");
        Driver oleg = new Driver("Oleg", "1919_i");
        driverService.create(ihor);
        driverService.create(oleg);
        driverService.get(ihor.getId());
        driverService.getAll().forEach(System.out::println);
        ihor.setLicenseNumber("O1O");
        driverService.update(ihor);
        driverService.delete(oleg.getId());

        ManufacturerService manufacturerService = (ManufacturerService)
                injector.getInstance(ManufacturerService.class);
        Manufacturer kia = new Manufacturer("KIA", "Ukraine");
        Manufacturer tesla = new Manufacturer("Tesla", "Ukraine");
        manufacturerService.create(kia);
        manufacturerService.create(tesla);
        manufacturerService.get(kia.getId());
        manufacturerService.getAll();//.forEach(System.out::println);
        kia.setCountry("Iran");
        manufacturerService.update(kia);
        manufacturerService.delete(tesla.getId());

        CarService carService = (CarService) injector.getInstance(CarService.class);
        Car car1 = new Car("KIA", kia, driverService.getAll());
        Car car2 = new Car("KIA", kia, driverService.getAll());

        carService.create(car1);
        carService.create(car2);
        carService.get(car1.getId());
        carService.getAll();
        car1.setModel("Audi");
        carService.update(car1);
        carService.delete(car2.getId());
        carService.addDriverToCar(ihor, car1);
        carService.removeDriverFromCar(ihor, car1);
        carService.getAllByDriver(car2.getId());
    }
}
