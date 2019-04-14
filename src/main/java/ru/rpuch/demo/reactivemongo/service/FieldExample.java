package ru.rpuch.demo.reactivemongo.service;

/**
 * @author rpuch
 */
public class FieldExample {
    private final String field;
    private final String value;

    public FieldExample(String field, String value) {
        this.field = field;
        this.value = value;
    }

    public String getField() {
        return field;
    }

    public String getValue() {
        return value;
    }
}
