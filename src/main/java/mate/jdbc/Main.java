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
    private static final DriverService driverService =
            (DriverService) injector.getInstance(DriverService.class);
    private static final ManufacturerService manufacturerService =
            (ManufacturerService) injector.getInstance(ManufacturerService.class);
    private static final CarService carService =
            (CarService) injector.getInstance(CarService.class);

    public static void main(String[] args) {

        Driver johnLennon = new Driver("John Lennon", "S123-456-600-12");
        Driver polMccartney = new Driver("Pol Mccartney", "K200-546-176-01");
        Driver georgeHarrison = new Driver("George Harrison", "W414-842-763-22");
        Driver ringoStarr = new Driver("Ringo Starr", "A134-746-978-75");

        driverService.create(johnLennon);
        driverService.create(polMccartney);
        driverService.create(georgeHarrison);
        driverService.create(ringoStarr);

        Manufacturer bmw = new Manufacturer("BMW", "GERMANY");
        Manufacturer lexus = new Manufacturer("LEXUS", "JAPAN");
        Manufacturer acura = new Manufacturer("ACURA", "JAPAN");
        Manufacturer rollsRoyce = new Manufacturer("ROLLS-ROYCE", "GREAT BRITAIN");

        manufacturerService.create(bmw);
        manufacturerService.create(lexus);
        manufacturerService.create(acura);
        manufacturerService.create(rollsRoyce);

        List<Driver> rollsRoyceDriverList = new ArrayList<>();
        rollsRoyceDriverList.add(driverService.get(1L));

        List<Driver> acuraDriverList = new ArrayList<>();
        acuraDriverList.add(driverService.get(1L));
        acuraDriverList.add(driverService.get(2L));
        acuraDriverList.add(driverService.get(3L));

        List<Driver> lexusDriverList = new ArrayList<>();
        lexusDriverList.add(driverService.get(1L));
        lexusDriverList.add(driverService.get(2L));
        lexusDriverList.add(driverService.get(4L));

        List<Driver> bmwDriverList = new ArrayList<>();
        bmwDriverList.add(driverService.get(1L));
        bmwDriverList.add(driverService.get(2L));
        bmwDriverList.add(driverService.get(3L));
        bmwDriverList.add(driverService.get((4L)));

        Car bmwCar = new Car("BMW", manufacturerService.get(1L),bmwDriverList);
        Car lexusCar = new Car("LEXUS", manufacturerService.get(2L),lexusDriverList);
        Car acuraCar = new Car("ACURA", manufacturerService.get(3L),acuraDriverList);
        Car rollsRoyceCar = new Car("ROLLS-ROYCE",
                manufacturerService.get(4L),rollsRoyceDriverList);

        carService.create(bmwCar);
        carService.create(lexusCar);
        carService.create(acuraCar);
        carService.create(rollsRoyceCar);

        carService.addDriverToCar(driverService.get(2L), carService.get(4L));
        carService.removeDriverFromCar(driverService.get(2L), carService.get(4L));

        bmwCar.setModel("BMW-X6");
        carService.update(carService.get(1L));

        System.out.println(carService.getAllByDriver(1L));
        System.out.println(carService.getAll());
    }
}
