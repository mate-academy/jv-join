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
        final ManufacturerService manufacturerService =
                (ManufacturerService) injector.getInstance(ManufacturerService.class);
        final DriverService driverService =
                (DriverService) injector.getInstance(DriverService.class);
        final CarService carService =
                (CarService) injector.getInstance(CarService.class);

        Driver dan = new Driver("Dan", "112233123456789");
        dan = driverService.create(dan);
        Driver ben = new Driver("Ben", "223344123456789");
        ben = driverService.create(ben);
        Driver sean = new Driver("Sean", "334455123456789");
        sean = driverService.create(sean);
        Driver ron = new Driver("Ron", "445566123456789");
        ron = driverService.create(ron);

        Manufacturer vw = new Manufacturer("VW", "Germany");
        Manufacturer audi = new Manufacturer("Audi", "Germany");
        Manufacturer renault = new Manufacturer("Renault", "France");
        Manufacturer honda = new Manufacturer("Honda", "Japan");

        manufacturerService.create(vw);
        manufacturerService.create(audi);
        manufacturerService.create(renault);
        manufacturerService.create(honda);

        List<Driver> firstSchedule = new ArrayList<>();
        firstSchedule.add(dan);
        firstSchedule.add(ben);

        List<Driver> secondSchedule = new ArrayList<>();
        secondSchedule.add(ron);
        secondSchedule.add(sean);

        Car vwAmarok = new Car("Amarok", vw);
        vwAmarok.setDrivers(firstSchedule);
        carService.create(vwAmarok);

        Car audiQSeven = new Car("Q7", audi);
        audiQSeven.setDrivers(secondSchedule);
        carService.create(audiQSeven);

        Car renaultMegane = new Car("Megane", renault);
        renaultMegane.setDrivers(firstSchedule);
        carService.create(renaultMegane);

        Car hondaAccord = new Car("Accord", honda);
        hondaAccord.setDrivers(secondSchedule);
        carService.create(hondaAccord);

        carService.getAll().forEach(System.out::println);

        audiQSeven.setDrivers(secondSchedule);
        carService.update(audiQSeven);

        carService.getAll().forEach(System.out::println);

        carService.delete(hondaAccord.getId());

        carService.getAll().forEach(System.out::println);
    }
}
