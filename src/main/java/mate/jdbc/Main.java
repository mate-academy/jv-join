package mate.jdbc;

import java.util.Arrays;
import java.util.HashSet;
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
        Manufacturer bmwManufacturer = new Manufacturer("bmw", "germany");
        Manufacturer audiManufacturer = new Manufacturer("audi", "germany");
        Manufacturer mazdaManufacturer = new Manufacturer("mazda", "japan");
        Manufacturer peugeotManufacturer = new Manufacturer("peugeot", "france");
        Manufacturer fiatManufacturer = new Manufacturer("fiat", "italy");

        ManufacturerService manufacturerService = (ManufacturerService)
                injector.getInstance(ManufacturerService.class);
        manufacturerService.create(bmwManufacturer);
        manufacturerService.create(audiManufacturer);
        manufacturerService.create(mazdaManufacturer);
        manufacturerService.create(peugeotManufacturer);
        manufacturerService.create(fiatManufacturer);

        Driver andyDriver = new Driver("andy", "111");
        Driver bobDriver = new Driver("bob", "222");
        Driver kevenDriver = new Driver("keven", "333");
        Driver scottDriver = new Driver("scott", "444");
        Driver mikalDriver = new Driver("mikal", "555");
        Driver benDriver = new Driver("ben", "666");
        Driver nataDriver = new Driver("nata", "777");
        Driver lucyDriver = new Driver("lucy", "888");
        Driver larsDriver = new Driver("lars", "999");

        DriverService driverService = (DriverService) injector.getInstance(DriverService.class);
        driverService.create(andyDriver);
        driverService.create(bobDriver);
        driverService.create(kevenDriver);
        driverService.create(scottDriver);
        driverService.create(mikalDriver);
        driverService.create(benDriver);
        driverService.create(nataDriver);
        driverService.create(lucyDriver);
        driverService.create(larsDriver);

        Car fiveBmw = new Car("five",
                manufacturerService.get(1L),
                new HashSet<>(Arrays.asList(driverService.get(1L), driverService.get(2L))));
        Car sevenBmw = new Car("seven",
                manufacturerService.get(1L),
                new HashSet<>(Arrays.asList(driverService.get(2L), driverService.get(3L))));
        Car asixAudi = new Car("a_six",
                manufacturerService.get(2L),
                new HashSet<>(Arrays.asList(driverService.get(1L), driverService.get(4L))));
        Car thrieMazda = new Car("thrie",
                manufacturerService.get(3L),
                new HashSet<>(Arrays.asList(driverService.get(5L), driverService.get(8L))));
        Car fiftyEightPeugeot = new Car("fifty_eight",
                manufacturerService.get(4L),
                new HashSet<>(Arrays.asList(driverService.get(2L), driverService.get(7L))));

        CarService carService = (CarService) injector.getInstance(CarService.class);
        carService.create(fiveBmw);
        carService.create(sevenBmw);
        carService.create(asixAudi);
        carService.create(thrieMazda);
        carService.create(fiftyEightPeugeot);

        Car firstCar = carService.get(1L);
        System.out.println(firstCar);

        List<Car> allCar = carService.getAll();
        allCar.forEach(System.out::println);

        Car fiveXBmw = new Car("five_x",
                manufacturerService.get(2L),
                new HashSet<>(Arrays.asList(driverService.get(8L), driverService.get(9L))));
        fiveXBmw.setId(1L);
        carService.update(fiveXBmw);

        carService.delete(1L);

        carService.addDriverToCar(driverService.get(7L), carService.get(2L));

        carService.removeDriverFromCar(driverService.get(7L), carService.get(2L));

        List<Car> allByDriver = carService.getAllByDriver(2L);
        allByDriver.forEach(System.out::println);
    }
}
