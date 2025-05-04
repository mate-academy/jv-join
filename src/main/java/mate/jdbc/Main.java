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

        final ManufacturerService manufacturerService =
                (ManufacturerService) injector.getInstance(ManufacturerService.class);
        final DriverService driverService =
                (DriverService) injector.getInstance(DriverService.class);
        final CarService carService =
                (CarService) injector.getInstance(CarService.class);

        Driver driverViktor = new Driver("Viktor", "00001");
        Driver driverAndre = new Driver("Andre", "00002");
        Driver driverOleg = new Driver("Oleg", "00003");
        Driver driverSava = new Driver("Sava", "00004");
        Driver driverOlga = new Driver("Olga", "00005");
        Driver driverNata = new Driver("Nata", "00006");
        Manufacturer kia = new Manufacturer("KIA", "Venesuella");
        Manufacturer mersedes = new Manufacturer("Mersedes", "Ukraine");
        Manufacturer tesla = new Manufacturer("Tesla", "Italia");
        Manufacturer shkoda = new Manufacturer("Shkoda", "USA");
        Manufacturer subaru = new Manufacturer("Subaru", "Egypt");
        final Car kiaNiro = new Car("kiaNiro", kia);
        final Car kiaSeltos = new Car("kiaSeltos", kia);
        final Car kiaTelluride = new Car("kiaTelluride", kia);
        final Car mersMaybach = new Car("mersMaybach ", mersedes);
        final Car mersGls = new Car("mersSUV", mersedes);
        final Car teslaS = new Car("teslaS", tesla);
        final Car shkodaRapid = new Car("shkodaRapid", shkoda);
        final Car shkodaSuperb = new Car("shkodaSuperb", shkoda);
        final Car shkodaYeti = new Car("shkodaYeti", shkoda);
        final Car subaruForester = new Car("subaruForester", subaru);
        final Car subaruLegacy = new Car("subaruLegacy", subaru);
        final Car subaruTribeca = new Car("subaruTribeca", subaru);
        driverService.create(driverAndre);
        driverService.create(driverNata);
        driverService.create(driverOleg);
        driverService.create(driverOlga);
        driverService.create(driverSava);
        driverService.create(driverViktor);
        manufacturerService.create(kia);
        manufacturerService.create(mersedes);
        manufacturerService.create(tesla);
        manufacturerService.create(shkoda);
        manufacturerService.create(subaru);
        carService.create(kiaNiro);
        carService.create(kiaSeltos);
        carService.create(kiaTelluride);
        carService.create(mersGls);
        carService.create(mersMaybach);
        carService.create(teslaS);
        carService.create(shkodaRapid);
        carService.create(shkodaSuperb);
        carService.create(shkodaYeti);
        carService.create(subaruForester);
        carService.create(subaruLegacy);
        carService.create(subaruTribeca);
        carService.addDriverToCar(driverOleg, shkodaRapid);
        carService.addDriverToCar(driverAndre, shkodaRapid);
        carService.addDriverToCar(driverNata, mersGls);
        carService.addDriverToCar(driverNata, subaruForester);
        carService.addDriverToCar(driverSava, subaruForester);
        carService.addDriverToCar(driverViktor, subaruLegacy);
        carService.addDriverToCar(driverOlga, kiaSeltos);
        carService.addDriverToCar(driverOlga, kiaNiro);
        carService.addDriverToCar(driverOlga, kiaTelluride);
        carService.addDriverToCar(driverAndre, kiaTelluride);
        carService.addDriverToCar(driverSava, kiaSeltos);
        carService.addDriverToCar(driverViktor, shkodaYeti);
        carService.getAll().forEach(System.out::println);
        carService.getAllByDriver(3L).forEach(System.out::println);
        System.out.println(carService.get(8L));
        carService.delete(8L);
        carService.removeDriverFromCar(driverOleg, shkodaRapid);
    }
}
