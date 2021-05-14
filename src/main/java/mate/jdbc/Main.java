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
    private static final Injector injector = Injector.getInstance("mate.jdbc");
    private static final CarService carService =
            (CarService) injector.getInstance(CarService.class);
    private static final DriverService driverService =
            (DriverService) injector.getInstance(DriverService.class);
    private static final ManufacturerService manufacturerService =
            (ManufacturerService) injector.getInstance(ManufacturerService.class);


    public static void main(String[] args) {
        Manufacturer audi = new Manufacturer("AUDI", "Germany");
        manufacturerService.create(audi);

        Driver yevheniiParchevskiy = new Driver("Yevhenii Parchevskiy", "11111111111111111");
        Driver alexanderKabanov = new Driver("Alexander Kabanov", "22222222222222");
        Driver illyaNemykin = new Driver("Illya Nemykin", "33333333333");
        Driver arianaGrande = new Driver("Ariana Grande", "444444444444");

        driverService.create(yevheniiParchevskiy);
        driverService.create(alexanderKabanov);
        driverService.create(illyaNemykin);
        driverService.create(arianaGrande);

        List<Driver> audiRs5Drivers= new ArrayList<>();
        List<Driver> audiEtronGtDrivers = new ArrayList<>();
        audiRs5Drivers.add(yevheniiParchevskiy);
        audiRs5Drivers.add(illyaNemykin);
        audiEtronGtDrivers.add(arianaGrande);
        audiEtronGtDrivers.add(alexanderKabanov);

        Car audiRs5 = new Car(audi, audiRs5Drivers);
        Car audiEtronGt = new Car(audi, audiEtronGtDrivers);

        carService.create(audiEtronGt);
        carService.create(audiRs5);
        //create Ok
        System.out.println("Audi RS5: " + carService.get(audiRs5.getId()));
        //System.out.println("Audi RS5: " + carService.get(audiRs5.getId() + 1)); get Ok
        System.out.println(carService.delete(audiRs5.getId()));
        //System.out.println(carService.get(audiRs5.getId())); delete Ok

        System.out.println(carService.getAll());
        //getAll Ok

        System.out.println(carService.getAllByDriver(alexanderKabanov.getId()));
        //getAllByDriver  Ok
        audiEtronGtDrivers.add(yevheniiParchevskiy);
        audiEtronGtDrivers.add(illyaNemykin);
        audiEtronGt.setDriverList(audiEtronGtDrivers);
        System.out.println(carService.update(audiEtronGt));
        //update Ok
    }
}
