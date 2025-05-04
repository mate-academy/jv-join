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
    private static final CarService carService =
            (CarService) injector.getInstance(CarService.class);
    private static final ManufacturerService manufacturerService =
            (ManufacturerService) injector.getInstance(ManufacturerService.class);
    private static final DriverService driverService =
            (DriverService) injector.getInstance(DriverService.class);

    public static void main(String[] args) {
        addManufacturersToDB();
        addDriversToDB();

        Car volvoXC90 = new Car();
        volvoXC90.setModel("XC90");
        volvoXC90.setManufacturer(manufacturerService.get(4L));
        List<Driver> volvoDrivers = new ArrayList<>();
        volvoDrivers.add(driverService.get(1L));
        volvoDrivers.add(driverService.get(2L));
        volvoXC90.setDrivers(volvoDrivers);

        Car renaultZoe = new Car();
        renaultZoe.setModel("Zoe");
        renaultZoe.setManufacturer(manufacturerService.get(5L));
        List<Driver> renaultDrivers = new ArrayList<>();
        renaultDrivers.add(driverService.get(4L));
        renaultDrivers.add(driverService.get(5L));
        renaultZoe.setDrivers(renaultDrivers);

        Car skodaFabia = new Car();
        skodaFabia.setModel("Fabia");
        skodaFabia.setManufacturer(manufacturerService.get(6L));
        List<Driver> skodaDrivers = new ArrayList<>();
        skodaDrivers.add(driverService.get(2L));
        skodaDrivers.add(driverService.get(5L));
        skodaFabia.setDrivers(skodaDrivers);

        List<Car> cars = new ArrayList<>();
        cars.add(volvoXC90);
        cars.add(renaultZoe);
        cars.add(skodaFabia);
        cars.forEach(carService::create);

        System.out.println("Check get() method");
        Car volvoCar = carService.get(1L);
        System.out.println(volvoCar);

        System.out.println("Check getAll() method");
        List<Car> carsFromDB = carService.getAll();
        System.out.println(carsFromDB);

        System.out.println("Check update() method");
        List<Driver> volvoDriversUpdate = new ArrayList<>();
        volvoDriversUpdate.add(driverService.get(4L));
        volvoDriversUpdate.add(driverService.get(6L));
        Car volvoEX90 = new Car(1L, "EX90", manufacturerService.get(4L), volvoDriversUpdate);
        carService.update(volvoEX90);
        System.out.println(volvoEX90);

        System.out.println("Check delete() method");
        boolean isDeletedCar = carService.delete(3L);
        System.out.println(isDeletedCar);

        System.out.println("Check getAllByDriver() method");
        List<Car> carsByDriver = carService.getAllByDriver(5L);
        System.out.println(carsByDriver);

        System.out.println("Check addDriverToCar() method");
        Driver roman = new Driver(7L, "Roman", "022");
        driverService.create(roman);
        carService.addDriverToCar(roman, volvoEX90);
        System.out.println(volvoEX90);

        System.out.println("Check removeDriverFromCar() method");
        carService.removeDriverFromCar(volvoEX90.getDrivers().get(0), volvoEX90);
        Car updatedVolvo = carService.get(1L);
        System.out.println(updatedVolvo);
    }

    private static void addManufacturersToDB() {
        Manufacturer volvo = new Manufacturer("Volvo", "Germany");
        Manufacturer renault = new Manufacturer("Renault", "France");
        Manufacturer skoda = new Manufacturer("Skoda", "Czechia");
        List<Manufacturer> manufacturers = new ArrayList<>();
        manufacturers.add(volvo);
        manufacturers.add(renault);
        manufacturers.add(skoda);
        manufacturers.forEach(manufacturerService::create);
    }

    private static void addDriversToDB() {
        Driver elisabet = new Driver("Elisabet", "021");
        Driver amir = new Driver("Amir", "012");
        Driver vincent = new Driver("Vincent", "013");
        List<Driver> drivers = new ArrayList<>();
        drivers.add(elisabet);
        drivers.add(amir);
        drivers.add(vincent);
        drivers.forEach(driverService::create);
    }
}
