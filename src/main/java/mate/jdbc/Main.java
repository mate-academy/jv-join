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

    public static void main(String[] args) {
        Car firstCar = new Car();
        firstCar.setModel("polo");
        Car secondCar = new Car();
        secondCar.setModel("rapid");
        Car thirdCar = new Car();
        thirdCar.setModel("accent");

        ManufacturerService manufacturerService =
                (ManufacturerService) injector.getInstance(ManufacturerService.class);
        Manufacturer volkswagen = manufacturerService
                .create(new Manufacturer("Volkswagen", "Germany"));
        firstCar.setManufacturer(volkswagen);
        Manufacturer skoda = manufacturerService
                .create(new Manufacturer("Skoda", "Czech Republic"));
        secondCar.setManufacturer(skoda);
        Manufacturer hyundai = manufacturerService
                .create(new Manufacturer("Hyundai", "Republic of Korea"));
        thirdCar.setManufacturer(hyundai);

        DriverService driverService = (DriverService) injector.getInstance(DriverService.class);
        Driver aleks = driverService.create(new Driver("Aleks", "DK012345D"));
        Driver sergey = driverService.create(new Driver("Sergey", "AA54321D"));
        List<Driver> firstDriversGroup = new ArrayList<>();
        firstDriversGroup.add(aleks);
        firstDriversGroup.add(sergey);
        firstCar.setDrivers(firstDriversGroup);

        Driver boris = driverService.create(new Driver("Boris", "BS56789B"));
        Driver roman = driverService.create(new Driver("Roman", "CR09874H"));
        List<Driver> secondDriversGroup = new ArrayList<>();
        secondDriversGroup.add(boris);
        secondDriversGroup.add(roman);
        secondCar.setDrivers(secondDriversGroup);

        List<Driver> thirdDriversGroup = new ArrayList<>();
        thirdDriversGroup.add(aleks);
        thirdCar.setDrivers(thirdDriversGroup);

        CarService carService = (CarService) injector.getInstance(CarService.class);
        carService.create(firstCar);
        carService.create(secondCar);
        carService.create(thirdCar);
        carService.getAll().forEach(System.out::println);
        carService.getAllByDriver(1L).forEach(System.out::println);
        thirdCar.setModel("sonata");
        carService.update(thirdCar);
        carService.delete(2L);
        carService.addDriverToCar(roman, thirdCar);
        carService.removeDriverFromCar(aleks, thirdCar);
        System.out.println(carService.get(3L));
    }
}
