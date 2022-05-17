package mate.jdbc;

import java.util.ArrayList;
import mate.jdbc.lib.Injector;
import mate.jdbc.model.Car;
import mate.jdbc.model.Driver;
import mate.jdbc.model.Manufacturer;
import mate.jdbc.service.CarService;
import mate.jdbc.service.DriverLicenseNumberValidator;
import mate.jdbc.service.DriverService;
import mate.jdbc.service.ManufacturerService;

public class Main {
    public static final Injector injector = Injector.getInstance("mate.jdbc");

    public static void main(String[] args) {
        ManufacturerService manufacturerService = (ManufacturerService) injector
                .getInstance(ManufacturerService.class);
        Manufacturer nissan = new Manufacturer("Nissan", "Japan");
        manufacturerService.create(nissan);

        DriverService driverService = (DriverService) injector
                .getInstance(DriverService.class);
        DriverLicenseNumberValidator validator =
                (DriverLicenseNumberValidator) injector
                        .getInstance(DriverLicenseNumberValidator.class);
        Driver lewis = driverService.create(new Driver("Lewis Hamilton",
                validator.validate("162223433163")));
        Driver max = driverService.create(new Driver("Max Verstappen",
                validator.validate("765837621171")));
        Driver fernando = driverService.create(new Driver("Fernando Alonso",
                validator.validate("311646302392")));

        CarService carService = (CarService) injector
                .getInstance(CarService.class);
        Car skyline = new Car("Skyline R34", nissan, new ArrayList<>());
        Car silvia = new Car("Silvia S15 Type R", nissan, new ArrayList<>());

        carService.create(skyline);
        carService.create(silvia);
        carService.addDriverToCar(lewis, skyline);
        carService.addDriverToCar(max, silvia);
        carService.addDriverToCar(fernando, skyline);

        System.out.println("get car: " + carService.get(skyline.getId()));

        carService.getAll().forEach(e -> System.out.println("All cars: " + e));
        System.out.println("get all by driver: " + carService.getAllByDriver(lewis.getId()));

        silvia.setModel("Silvia S13 Type S");
        System.out.println("update car: " + carService.update(skyline));

        carService.removeDriverFromCar(fernando, skyline);
        System.out.println("delete car: " + carService.delete(silvia.getId()));
    }
}
