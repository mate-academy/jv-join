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
    private static final CarService carService =
            (CarService) injector.getInstance(CarService.class);
    private static final DriverService driverService =
            (DriverService) injector.getInstance(DriverService.class);
    private static final ManufacturerService manufacturerService =
            (ManufacturerService) injector.getInstance(ManufacturerService.class);

    public static void main(String[] args) {
        Driver sebastian = new Driver("Sebastian Vettel", "V-23413");
        Driver lewis = new Driver("Lewis Hamilton", "F-1345245");
        Driver valtteri = new Driver("Valtteri Bottas", "F-134524");
        Driver george = new Driver("George Russell", "E-3r322");
        Driver carlos = new Driver("Carlos Sainz", "q2df322");
        Driver piere = new Driver("Piere Gasly", "133evvf");
        Driver charles = new Driver("Charles Leclerc", "133evvf");
        driverService.create(sebastian);
        driverService.create(lewis);
        driverService.create(valtteri);
        driverService.create(george);
        driverService.create(carlos);
        driverService.create(piere);
        driverService.create(charles);

        Manufacturer astonMartin = new Manufacturer("Aston Martin", "United Kingdom");//sebastian
        Manufacturer mercedesBenz = new Manufacturer("Mercedes-Benz", "Germany");//lewis, valtteri
        Manufacturer williams = new Manufacturer("Williams Racing", "United Kingdom");//george
        Manufacturer ferrari = new Manufacturer("Ferrari S.p.A.", "Italy");//carlos, charles
        Manufacturer alphaTauri = new Manufacturer("Scuderia AlphaTauri", "Italy");//piere
        manufacturerService.create(astonMartin);
        manufacturerService.create(mercedesBenz);
        manufacturerService.create(williams);
        manufacturerService.create(ferrari);
        manufacturerService.create(alphaTauri);

        Car f1MercedesBenz = new Car("Mercedes F1 W12", mercedesBenz,
                new ArrayList<>(List.of(lewis, valtteri)));
        carService.create(f1MercedesBenz);
        f1MercedesBenz.setModel("Mercedes F1 W13");
        carService.update(f1MercedesBenz);
        carService.removeDriverFromCar(lewis, f1MercedesBenz);
        carService.addDriverToCar(lewis, f1MercedesBenz);
        Car f1AstonMartin = new Car("Aston Martin RB16", astonMartin,
                new ArrayList<>(List.of(sebastian)));
        carService.create(f1AstonMartin);
        Car f1Williams = new Car("Williams FW15C", williams,
                new ArrayList<>(List.of(george, valtteri)));
        carService.create(f1Williams);
        System.out.println(carService.get(f1Williams.getId()));
        carService.delete(f1Williams.getId());
        Car f1Ferrari = new Car("Scuderia Ferrari SF1000", ferrari,
                new ArrayList<>(List.of(carlos, charles)));
        carService.create(f1Ferrari);
        Car f1AlphaTauri = new Car("Alpha Tauri AT01", alphaTauri,
                new ArrayList<>(List.of(piere)));
        carService.create(f1AlphaTauri);
        carService.getAllByDriver(valtteri.getId()).forEach(System.out::println);
        carService.getAll().forEach(System.out::println);
    }
}
