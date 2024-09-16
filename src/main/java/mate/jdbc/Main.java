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
    public static void main(String[] args) {
        // test your code here
        Injector injector = Injector.getInstance("mate.jdbc");

        DriverService driverService = (DriverService) injector.getInstance(DriverService.class);
        ManufacturerService manufacturerService = (ManufacturerService) injector
                .getInstance(ManufacturerService.class);
        CarService carService = (CarService) injector.getInstance(CarService.class);

        Driver driver1 = new Driver("driver1", "licenseD1");
        Driver driver2 = new Driver("driver2", "licenseD2");

        Manufacturer manufacturerRenault = new Manufacturer("Renault", "France");
        Manufacturer manufacturerPeugeot = new Manufacturer("Peugeot", "France");

        Driver driver1FromDB = driverService.create(driver1);
        Manufacturer manufacturerRenaultFromDB = manufacturerService.create(manufacturerRenault);
        // Driver driver1FromDB = driverService.get(3L);
        // Manufacturer manufacturerRenaultFromDB = manufacturerService.get(3L);

        Car car1 = new Car();
        car1.setModel("Logan");
        car1.setManufacturer(manufacturerRenaultFromDB);
        car1.setDrivers(new ArrayList<>(List.of(driver1FromDB)));
        Car renaultLogan = carService.create(car1);

        Driver driver2FromDB = driverService.create(driver2);
        Manufacturer manufacturerPeugeotFromDB = manufacturerService.create(manufacturerPeugeot);
        // Driver driver2FromDB = driverService.get(4L);
        // Manufacturer manufacturerPeugeotFromDB = manufacturerService.get(4L);

        Car car2 = new Car();
        car2.setModel("508");
        car2.setManufacturer(manufacturerPeugeotFromDB);
        car2.setDrivers(new ArrayList<>(List.of(driver2FromDB)));
        Car peugeot503 = carService.create(car2);

        Car car1FromDB = carService.get(1L);
        Car car2FromDB = carService.get(2L);

        List<Car> cars = carService.getAll();

        carService.delete(car1FromDB.getId());

        carService.removeDriverFromCar(driver1FromDB, car2FromDB);

        car2FromDB.setModel("3008");
        car2FromDB.setDrivers(new ArrayList<>(List.of(driver1FromDB, driver2FromDB)));
        carService.update(car2FromDB);
        carService.addDriverToCar(driver1FromDB, car2FromDB);

        carService.removeDriverFromCar(driver1FromDB, car2FromDB);
        carService.addDriverToCar(driver1FromDB, car2FromDB);

        List<Car> allByDriver = carService.getAllByDriverId(driver1FromDB.getId());
    }
}
