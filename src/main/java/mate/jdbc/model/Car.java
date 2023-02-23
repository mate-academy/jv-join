package mate.jdbc.model;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class Car {
    private Long id;
    private String model;
    private Manufacturer manufacturer;
    private List<Driver> drivers;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public Manufacturer getManufacturer() {
        return manufacturer;
    }

    public void setManufacturer(Manufacturer manufacturer) {
        this.manufacturer = manufacturer;
    }

    public List<Driver> getDrivers() {
        return drivers;
    }

    public void setDrivers(List<Driver> drivers) {
        this.drivers = drivers;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Car car = (Car) o;
        return Objects.equals(id, car.id)
            && Objects.equals(model, car.model)
            && Objects.equals(manufacturer, car.manufacturer)
            && compareTwoLists(drivers, car.drivers);
    }

    private <T> boolean compareTwoLists(List<T> firstList, List<T> secondList) {
        if (firstList == secondList) {
            return true;
        }

        if (firstList != null && secondList != null && firstList.size() == secondList.size()) {
            Object[] firstArray = firstList.toArray();
            Object[] secondArray = firstList.toArray();
            for (int i = 0; firstArray.length > i; ++i) {
                if (!Objects.equals(firstArray[i], secondArray[i])) {
                    return false;
                }
            }
            return true;
        }

        return false;
    }

    @Override
    public int hashCode() {
        int driversHashCode = 0;
        if (drivers != null) {
            driversHashCode = Arrays.hashCode(drivers.toArray());
        }
        return Objects.hash(id, model, manufacturer) + driversHashCode;
    }

    @Override
    public String toString() {
        return "Car{"
            + "id=" + id
            + ", model='" + model + '\''
            + ", manufacturer='" + manufacturer + '\''
            + ", drivers='{"
            + drivers.stream().map(Driver::toString).collect(Collectors.joining(",")) + '}'
            + '}';
    }
}
