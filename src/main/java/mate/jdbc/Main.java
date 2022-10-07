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
        // test your code here
        final ManufacturerService manufacturerService =
                (ManufacturerService) injector.getInstance(ManufacturerService.class);
        Manufacturer jeep = new Manufacturer();
        jeep.setName("jeep");
        jeep.setCountry("USA");
        jeep.setId(33L);
        manufacturerService.create(jeep);
        Manufacturer lexus = new Manufacturer();
        lexus.setName("Lexus");
        lexus.setCountry("Italy");
        lexus.setId(34L);
        manufacturerService.create(lexus);
        manufacturerService.delete(1L);

        CarService carService =
                (CarService) injector.getInstance(CarService.class);
        Car jeepWl = new Car("WL", manufacturerService.get(14L));
        carService.create(jeepWl);
        Car lexus570 = new Car("570", manufacturerService.get(13L));
        carService.create(lexus570);
        Car updateCar = new Car(1L,"Toyota",manufacturerService.get(2L));
        carService.update(updateCar);
        carService.delete(3L);

        DriverService driverService =
                (DriverService) injector.getInstance(DriverService.class);
        Driver veronika = new Driver(33L, "Veronika","05082020");
        Driver danylo = new Driver(34L, "Danylo", "08062013");
        driverService.create(veronika);
        driverService.create(danylo);

        carService.addDriverToCar(driverService.get(33L), carService.get(13L));
        carService.addDriverToCar(driverService.get(34L), carService.get(14L));
        carService.removeDriverFromCar(driverService.get(33L), carService.get(13L));
    }
}
