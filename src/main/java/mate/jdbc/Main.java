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
        Manufacturer audi = new Manufacturer("Audi", "Germany");
        Manufacturer reno = new Manufacturer("Reno", "France");
        audi = manufacturerService.create(audi);
        reno = manufacturerService.create(reno);

        DriverService driverService = (DriverService) injector.getInstance(DriverService.class);
        Driver driver1 = new Driver("Ivan Chernenko", "123456");
        driver1 = driverService.create(driver1);
        Driver driver2 = new Driver("Maksym Romanov", "654321");
        driver2 = driverService.create(driver2);
        Driver driver3 = new Driver("Roman Dzuba", "112233");
        driver3 = driverService.create(driver3);
        Driver driver4 = new Driver("Igor Shevchenko", "224311");
        driver4 = driverService.create(driver4);

        Car renoLogan = new Car();
        renoLogan.setModel("Reno Logan");
        renoLogan.setManufacturer(reno);
        Car audiA4 = new Car();
        audiA4.setModel("Audi a4");
        audiA4.setManufacturer(audi);
        Car audiTt = new Car();
        audiTt.setModel("Audi TT");
        audiTt.setManufacturer(audi);
        Car renoClio = new Car();
        renoClio.setModel("Reno Clio");
        renoClio.setManufacturer(reno);

        CarService carService = (CarService) injector.getInstance(CarService.class);
        renoLogan = carService.create(renoLogan);
        renoClio = carService.create(renoClio);
        audiA4 = carService.create(audiA4);
        audiTt = carService.create(audiTt);

        carService.addDriverToCar(driver1, renoClio);
        carService.addDriverToCar(driver4, renoClio);
        carService.addDriverToCar(driver2, audiTt);
        carService.addDriverToCar(driver3, audiA4);
        carService.addDriverToCar(driver1, renoLogan);
        carService.addDriverToCar(driver1, audiA4);

        System.out.println(carService.getAll());

        carService.delete(audiTt.getId());

        System.out.println(carService.getAll());

        System.out.println(carService.getAllByDriver(driver1.getId()));

        carService.removeDriverFromCar(driver1, audiA4);

        System.out.println(carService.getAllByDriver(driver1.getId()));

        carService.addDriverToCar(driver2, renoLogan);

        System.out.println(carService.getAllByDriver(driver2.getId()));
    }
}
