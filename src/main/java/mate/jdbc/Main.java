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
    private static Injector injector = Injector.getInstance("mate.jdbc");

    public static void main(String[] args) {
        DriverService driverService = (DriverService) injector.getInstance(DriverService.class);
        ManufacturerService manufacturerService = (ManufacturerService) injector
                .getInstance(ManufacturerService.class);

        Manufacturer manufacturerBmw = new Manufacturer("BMW", "Germany");
        manufacturerService.create(manufacturerBmw);

        Driver driverLeo = new Driver("Leo", "711199");
        Driver driverAlexa = new Driver("Alexa", "922278");
        driverService.create(driverLeo);
        driverService.create(driverAlexa);
        Car carS1 = new Car(1L,"S1", manufacturerBmw);
        carS1.setDrivers(List.of(driverLeo, driverAlexa));

        CarService carService = (CarService) injector.getInstance(CarService.class);
        System.out.println(carService.create(carS1));
        System.out.println(carService.get(1L).toString());
        carService.getAll().forEach(System.out::println);

        Driver driverMasha = new Driver("Masha", "933399");
        Driver driverStepan = new Driver("Stepan", "944491");
        driverService.create(driverMasha);
        driverService.create(driverStepan);
        Car carS3 = new Car(1L,"S3", manufacturerBmw);
        carS3.setDrivers(List.of(driverMasha, driverStepan));
        System.out.println(carService.update(carS3));

        Driver driverAlisa = new Driver("Alisa", "123479");
        driverService.create(driverAlisa);
        carService.addDriverToCar(driverAlisa, carS1);
        System.out.println(carService.get(1L));
        System.out.println(carService.delete(1L));
    }
}
