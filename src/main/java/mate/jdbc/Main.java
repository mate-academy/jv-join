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
    private static final ManufacturerService manufacturerService
            = (ManufacturerService) injector.getInstance(ManufacturerService.class);
    private static final DriverService driverService
            = (DriverService) injector.getInstance(DriverService.class);
    private static final CarService carService
            = (CarService) injector.getInstance(CarService.class);

    public static void main(String[] args) {

        Manufacturer opel = new Manufacturer("opel","germany");
        Manufacturer renault = new Manufacturer("renault", "france");
        Manufacturer kia = new Manufacturer("kia", "korea");
        manufacturerService.create(opel);
        manufacturerService.create(renault);
        manufacturerService.create(kia);
        Driver abram = new Driver("abram", "11111");
        Driver fransua = new Driver("fransua", "22222");
        Driver vova = new Driver("vova", "33333");
        driverService.create(abram);
        driverService.create(fransua);
        driverService.create(vova);
        List<Driver> driversForFirstCar = new ArrayList<>();
        driversForFirstCar.add(abram);
        List<Driver> driversForSecondCar = new ArrayList<>();
        driversForSecondCar.add(abram);
        driversForSecondCar.add(fransua);
        driversForSecondCar.add(vova);
        List<Driver> driversForThirdCar = new ArrayList<>();
        driversForThirdCar.add(vova);
        Car omega = new Car("omega", opel, driversForFirstCar);
        System.out.println(carService.create(omega));
        Car duster = new Car("duster", renault, driversForSecondCar);
        System.out.println(carService.create(duster));
        Car k2 = new Car();
        k2.setManufacturer(kia);
        k2.setModel("k2");
        k2.setDrivers(driversForThirdCar);
        carService.create(k2);
        System.out.println(carService.getAll());
        System.out.println(carService.get(k2.getId()));
        carService.addDriverToCar(fransua, omega);
        carService.removeDriverFromCar(abram,omega);
        System.out.println(carService.getAllByDriver(vova.getId()));
        System.out.println(carService.getAll());
    }
}
