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
        Driver driverIvanChernenko = new Driver("Ivan Chernenko", "123456");
        driverIvanChernenko = driverService.create(driverIvanChernenko);
        Driver driverMaksymRomanov = new Driver("Maksym Romanov", "654321");
        driverMaksymRomanov = driverService.create(driverMaksymRomanov);
        Driver driverRomanDzuba = new Driver("Roman Dzuba", "112233");
        driverRomanDzuba = driverService.create(driverRomanDzuba);
        Driver driverIgorShevchenko = new Driver("Igor Shevchenko", "224311");
        driverIgorShevchenko = driverService.create(driverIgorShevchenko);

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

        carService.addDriverToCar(driverIvanChernenko, renoClio);
        carService.addDriverToCar(driverIgorShevchenko, renoClio);
        carService.addDriverToCar(driverMaksymRomanov, audiTt);
        carService.addDriverToCar(driverRomanDzuba, audiA4);
        carService.addDriverToCar(driverIvanChernenko, renoLogan);
        carService.addDriverToCar(driverIvanChernenko, audiA4);

        System.out.println(carService.getAll());

        carService.delete(audiTt.getId());

        System.out.println(carService.getAll());

        System.out.println(carService.getAllByDriver(driverIvanChernenko.getId()));

        carService.removeDriverFromCar(driverIvanChernenko, audiA4);

        System.out.println(carService.getAllByDriver(driverIvanChernenko.getId()));

        carService.addDriverToCar(driverMaksymRomanov, renoLogan);

        System.out.println(carService.getAllByDriver(driverMaksymRomanov.getId()));
    }
}
