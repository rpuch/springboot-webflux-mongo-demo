package ru.rpuch.demo.reactivemongo.domain;

/**
 * @author rpuch
 */
public class Length {
    private String unit;
    private double value;

    public Length() {
    }

    public Length(String unit, double value) {
        this.unit = unit;
        this.value = value;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }
}
