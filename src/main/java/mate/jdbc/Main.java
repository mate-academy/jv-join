package mate.jdbc;

import mate.jdbc.lib.Injector;
import mate.jdbc.model.Car;
import mate.jdbc.model.Driver;
import mate.jdbc.model.Manufacturer;
import mate.jdbc.service.CarService;
import mate.jdbc.service.DriverService;
import mate.jdbc.service.ManufacturerService;

import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        // test your code here
        Injector injector = Injector.getInstance("mate.jdbc");

        DriverService driverService = (DriverService) injector.getInstance(DriverService.class);
        ManufacturerService manufacturerService = (ManufacturerService) injector.getInstance(ManufacturerService.class);
        CarService carService = (CarService) injector.getInstance(CarService.class);

        Driver driver1 = new Driver("driver1", "licenseD1");
        Driver driver2 = new Driver("driver2", "licenseD2");

        Driver driver1FromDB = driverService.create(driver1);
        Driver driver2FromDB = driverService.create(driver2);

//        Driver driver1FromDB = driverService.get(3L);
//        Driver driver2FromDB = driverService.get(4L);

        Manufacturer manufacturerRenault = new Manufacturer("Renault", "France");
        Manufacturer manufacturerPeugeot = new Manufacturer("Peugeot", "France");

        Manufacturer manufacturerRenaultFromDB = manufacturerService.create(manufacturerRenault);
        Manufacturer manufacturerPeugeotFromDB = manufacturerService.create(manufacturerPeugeot);

//        Manufacturer manufacturerRenaultFromDB = manufacturerService.get(3L);
//        Manufacturer manufacturerPeugeotFromDB = manufacturerService.get(4L);

        Car car1 = new Car();
        car1.setModel("Logan");
        car1.setManufacturer(manufacturerRenaultFromDB);
        car1.setDrivers(new ArrayList<>(List.of(driver1FromDB)));

        Car renaultLogan = carService.create(car1);

        Car car2 = new Car();
        car2.setModel("503");
        car2.setManufacturer(manufacturerPeugeotFromDB);
        car2.setDrivers(new ArrayList<>(List.of(driver2FromDB)));

        Car peugeot503 = carService.create(car2);

        Car car1FromDB = carService.get(1L);
        Car car2FromDB = carService.get(2L);

        List<Car> cars = carService.getAll();

        carService.delete(car1FromDB.getId());

        carService.removeDriverFromCar(driver1FromDB, car2FromDB);

        car2FromDB.setModel("4003");
        car2FromDB.setDrivers(new ArrayList<>(List.of(driver1FromDB, driver2FromDB)));
        carService.update(car2FromDB);
        carService.addDriverToCar(driver1FromDB, car2FromDB);


        carService.removeDriverFromCar(driver1FromDB, car2FromDB);
        carService.addDriverToCar(driver1FromDB, car2FromDB);

        List<Car> allByDriver = carService.getAllByDriverId(driver1FromDB.getId());
    }
}
