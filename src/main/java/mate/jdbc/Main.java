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
        ManufacturerService manufacturerService = (ManufacturerService) injector
                .getInstance(ManufacturerService.class);
        Manufacturer bugatti = new Manufacturer(null,"Bugatti", "France");
        Manufacturer rollsRoyce = new Manufacturer(null,"Rolls-Royce", "England");
        Manufacturer ford = new Manufacturer(null,"Ford", "USA");
        bugatti = manufacturerService.create(bugatti);
        rollsRoyce = manufacturerService.create(rollsRoyce);
        ford = manufacturerService.create(ford);
        DriverService driverService =
                (DriverService) injector.getInstance(DriverService.class);
        Driver yarik = new Driver(null, "Yarik", "JV7777777JV");
        Driver oleg = new Driver(null, "Oleg", "QQ1111111HT");
        yarik = driverService.create(yarik);
        oleg = driverService.create(oleg);
        CarService carService =
                (CarService) injector.getInstance(CarService.class);
        Car bugattiVeyron = new Car(null, "Veyron", bugatti, List.of(yarik));
        Car rollsRoyceFantom = new Car(null, "Fantom", rollsRoyce, List.of(yarik, oleg));
        Car fordFusion = new Car(null, "Fusion", ford, List.of(oleg, yarik));
        carService.create(bugattiVeyron);
        carService.create(rollsRoyceFantom);
        carService.create(fordFusion);
        System.out.println("***** Create Car Bugatti Veyron, Rolls Royce Fantom, Ford Fusion *****");
        carService.getAll().forEach(System.out::println);
        System.out.println("**********************************************************************");
        System.out.println("***** Get Car by id Rolls Royce Fantom *****");
        Car carGetId = carService.get(rollsRoyceFantom.getId());
        System.out.println(carGetId);
        System.out.println("**********************************************************************");
        System.out.println("***** Get All Cars *****");
        List<Car> cars = carService.getAll();
        cars.forEach(System.out::println);
        System.out.println("**********************************************************************");
        System.out.println("***** Car and Driver update *****");
        Car updateCar = carGetId;
        updateCar.setModel("New Rolls Royce Fantom");
        Driver dmytro = new Driver(null, "Dmytro", "MA000000JV");
        dmytro = driverService.create(dmytro);
        List<Driver> updateCarDrivers = updateCar.getDrivers();
        updateCarDrivers.add(dmytro);
        System.out.println(carService.update(updateCar));
        System.out.println("**********************************************************************");
        System.out.println("***** Get all cars from Driver Dmytro *****");
        List<Car> driverCars = carService.getAllByDriver(dmytro.getId());
        driverCars.forEach(System.out::println);
        System.out.println("**********************************************************************");
        System.out.println("***** Add Driver to Car *****");
        Driver semen = new Driver(null, "Semen", "SS888888AH");
        driverService.create(semen);
        carService.addDriverToCar(semen,carGetId);
        System.out.println(carService.get(carGetId.getId()));
        System.out.println("**********************************************************************");
        System.out.println("***** Remove Driver *****");
        carService.removeDriverFromCar(oleg, fordFusion);
        System.out.println(carService.get(fordFusion.getId()));
        System.out.println("**********************************************************************");

    }
}
