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
    private static final Long FIRST_DRIVER_ID = 7L;
    private static final Long SECOND_DRIVER_ID = 8L;
    private static final Long FIRST_MANUFACTURER_ID = 31L;
    private static final Long SECOND_MANUFACTURER_ID = 32L;
    private static final Long DRIVER_TO_ADD_OR_DELETE_ID = 19L;
    private static final Long CAR_ID = 17L;
    private static final Long UPDATED_ID = 20L;
    private static final Injector injector = Injector.getInstance("mate.jdbc");
    private static final CarService carService
            = (CarService) injector.getInstance(CarService.class);
    private static final DriverService driverService
            = (DriverService) injector.getInstance(DriverService.class);
    private static final ManufacturerService manufacturerService
            = (ManufacturerService) injector.getInstance(ManufacturerService.class);

    public static void main(String[] args) {
        Manufacturer firstManufacturer = new Manufacturer();
        Manufacturer secondManufacturer = new Manufacturer();
        firstManufacturer.setName("Fifa");
        firstManufacturer.setCountry("Georgia");
        secondManufacturer.setCountry("Spain");
        secondManufacturer.setName("Alonso");
        manufacturerService.create(firstManufacturer);
        manufacturerService.create(secondManufacturer);

        Driver firstDriver = new Driver();
        firstDriver.setName("Alex");
        firstDriver.setLicenseNumber("12942581");
        Driver secondDriver = new Driver();
        secondDriver.setName("Fara");
        secondDriver.setLicenseNumber("91284120");
        Car car = new Car();
        car.setModel("Tesla");
        car.setManufacturer(manufacturerService.get(FIRST_MANUFACTURER_ID));
        car.getDrivers().add(driverService.get(FIRST_DRIVER_ID));
        car.getDrivers().add(driverService.get(SECOND_DRIVER_ID));
        System.out.println(car);
        carService.create(car);
        Car secondCar = new Car();
        secondCar.setModel("Booba");
        secondCar.setManufacturer(manufacturerService.get(SECOND_MANUFACTURER_ID));
        carService.create(secondCar);

        System.out.println(carService.get(CAR_ID));

        Car updatedCar = new Car();
        updatedCar.setId(UPDATED_ID);
        updatedCar.setModel("Fuksi");
        updatedCar.setManufacturer(manufacturerService.get(FIRST_MANUFACTURER_ID));

        carService.delete(updatedCar.getId());

        carService.addDriverToCar(driverService.get(DRIVER_TO_ADD_OR_DELETE_ID),secondCar);
        System.out.println(carService.get(secondCar.getId()));

        carService.removeDriverFromCar(driverService.get(DRIVER_TO_ADD_OR_DELETE_ID), secondCar);
        System.out.println(carService.get(secondCar.getId()));

        List<Car> allByDriver = carService.getAllByDriver(driverService
                .get(DRIVER_TO_ADD_OR_DELETE_ID).getId());
        allByDriver.stream().forEach(System.out::println);
    }
}
