package ru.rpuch.demo.reactivemongo.domain;

import java.util.Objects;

/**
 * @author rpuch
 */
public class Speed {
    private final String unit;
    private final double value;

    public Speed(String unit, double value) {
        this.unit = unit;
        this.value = value;
    }

    public String getUnit() {
        return unit;
    }

    public double getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Speed speed = (Speed) o;
        return Double.compare(speed.value, value) == 0 &&
                Objects.equals(unit, speed.unit);
    }

    @Override
    public int hashCode() {
        return Objects.hash(unit, value);
    }

    @Override
    public String toString() {
        return "Speed{" +
                "unit='" + unit + '\'' +
                ", value=" + value +
                '}';
    }
}
