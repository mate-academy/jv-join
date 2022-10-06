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
        // test your code here
        ManufacturerService manufacturerService =
                (ManufacturerService) injector.getInstance(ManufacturerService.class);
        Manufacturer jeep = new Manufacturer();
        jeep.setName("jeep");
        jeep.setCountry("USA");
        final Manufacturer createdManufacturer = manufacturerService.create(jeep);

        final DriverService driverService =
                (DriverService) injector.getInstance(DriverService.class);
        Driver alex = new Driver();
        alex.setId(null);
        alex.setName("Alex");
        alex.setLicenseNumber("121385");
        driverService.create(alex);

        Driver veronika = new Driver();
        veronika.setId(null);
        veronika.setName("Veronika");
        veronika.setLicenseNumber("05082020");
        final Driver createdDriver = driverService.create(veronika);

        final CarService carService =
                (CarService) injector.getInstance(CarService.class);
        Car car = new Car();
        car.setModel("1300");
        car.setManufacturer(createdManufacturer);
        car.setDrivers(List.of(alex));
        Car carCreated = carService.create(car);

        Car getCarById = carService.get(carCreated.getId());
        getCarById.setModel("Renault");
        createdDriver.setName("Ivan");
        createdDriver.setLicenseNumber("148877");

        List<Driver> updateDrivers = getCarById.getDrivers();
        updateDrivers.add(createdDriver);
        System.out.println(carService.update(getCarById));

        List<Car> driversCar = carService.getAllByDriver(getCarById.getId());
        driversCar.forEach(System.out::println);

        Driver john = new Driver(null,"John","123456789");
        driverService.create(john);
        carService.addDriverToCar(john, getCarById);

        carService.removeDriverFromCar(john,getCarById);
    }
}
