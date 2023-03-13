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
        // test your code here
        /**
         * Create manufactures and add them to 'manufacturers table'
         */
        Manufacturer bmw = new Manufacturer();
        bmw.setName("BMW");
        bmw.setCountry("GERMANY");
        Manufacturer nissan = new Manufacturer();
        nissan.setName("NISSAN");
        nissan.setCountry("JAPAN");
        Manufacturer ford = new Manufacturer();
        ford.setName("FORD");
        ford.setCountry("USA");
        ManufacturerService manufacturerService
                = (ManufacturerService) injector.getInstance(ManufacturerService.class);
        manufacturerService.create(bmw);
        manufacturerService.create(nissan);
        manufacturerService.create(ford);
        /**
         * Create drivers and add them to 'drivers table'
         */
        Driver tony = new Driver();
        tony.setName("Tony");
        tony.setLicenseNumber("TonyLicence123");
        Driver bill = new Driver();
        bill.setName("Bill");
        bill.setLicenseNumber("BillLicence123");
        Driver alice = new Driver();
        alice.setName("Alice");
        alice.setLicenseNumber("AliceLicence123");
        Driver marc = new Driver();
        marc.setName("Marc");
        marc.setLicenseNumber("MarcLicence123");
        DriverService driverService = (DriverService) injector.getInstance(DriverService.class);
        driverService.create(tony);
        driverService.create(bill);
        driverService.create(alice);
        driverService.create(marc);
        /**
         * Create cars and check CarService's methods working'
         */
        Car m5 = new Car();
        m5.setModel("M5");
        m5.setManufacturer(bmw);
        List<Driver> m5Drivers = new ArrayList<>();
        m5Drivers.add(tony);
        m5Drivers.add(bill);
        m5Drivers.add(alice);
        m5.setDrivers(m5Drivers);
        Car m3 = new Car();
        m3.setModel("M3");
        m3.setManufacturer(bmw);
        List<Driver> m3Drivers = new ArrayList<>();
        m3Drivers.add(bill);
        m3Drivers.add(marc);
        m3.setDrivers(m3Drivers);
        Car focus = new Car();
        focus.setModel("Focus");
        focus.setManufacturer(ford);
        List<Driver> focusDrivers = new ArrayList<>();
        focusDrivers.add(marc);
        focusDrivers.add(alice);
        focusDrivers.add(tony);
        focus.setDrivers(focusDrivers);
        Car fiesta = new Car();
        fiesta.setModel("Fiesta");
        fiesta.setManufacturer(ford);
        List<Driver> fiestaDrivers = new ArrayList<>();
        fiestaDrivers.add(bill);
        fiestaDrivers.add(tony);
        fiesta.setDrivers(fiestaDrivers);
        Car almera = new Car();
        almera.setModel("Almera");
        almera.setManufacturer(nissan);
        List<Driver> almeraDrivers = new ArrayList<>();
        almeraDrivers.add(bill);
        almeraDrivers.add(tony);
        almeraDrivers.add(alice);
        almeraDrivers.add(marc);
        almera.setDrivers(almeraDrivers);
        //Check 'create' method
        CarService carService = (CarService) injector.getInstance(CarService.class);
        carService.create(m5);
        System.out.println(m5);
        System.out.println();
        carService.create(m3);
        carService.create(focus);
        carService.create(fiesta);
        carService.create(almera);
        System.out.println();
        //Check 'get' method
        System.out.println("Check get method:");
        Car someCar = carService.get(focus.getId());
        System.out.println(someCar);
        System.out.println();
        //Check 'getAll' method (drivers list will be empty)
        System.out.println("Check getAll method:");
        List<Car> cars = carService.getAll();
        cars.stream().forEach(System.out::println);
        System.out.println();
        //Check 'update' method
        System.out.println("Check update method:");
        System.out.println(m5);
        m5.setModel("RIO");
        m5.setManufacturer(ford);
        carService.update(m5);
        System.out.println(carService.get(m5.getId()));
        System.out.println();
        //Check 'delete' method
        carService.delete(almera.getId());
        System.out.println("GetAll after delete");
        carService.getAll();
        System.out.println();
        //Check addDriverToCar
        System.out.println("m3 before add");
        System.out.println(m3);
        carService.addDriverToCar(alice, m3);
        System.out.println("m3 after add");
        System.out.println(m3);
        //Check removeDriverFromCar
        carService.removeDriverFromCar(bill, m3);
        carService.removeDriverFromCar(marc, m3);
        System.out.println("m3 after remove");
        System.out.println(m3);
        //Check  getAllByDriver
        System.out.println(bill.getName());
        List<Car> carsList = carService.getAllByDriver(bill.getId());
        carsList.stream().forEach(System.out::println);
        System.out.println();
    }
}
