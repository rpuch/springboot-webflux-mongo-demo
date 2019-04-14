package ru.rpuch.demo.reactivemongo.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

/**
 * @author rpuch
 */
@Document
public class Vehicle {
    @Id
    private String id;
    @Indexed(unique = true)
    private String code;
    @Indexed
    private String transmission;
    @Indexed
    private String ai;
    @Field("max-speed")
    private Speed maxSpeed;

    public Vehicle() {
    }

    public Vehicle(String code, String transmission, String ai, Speed maxSpeed) {
        this.code = code;
        this.transmission = transmission;
        this.ai = ai;
        this.maxSpeed = maxSpeed;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getTransmission() {
        return transmission;
    }

    public void setTransmission(String transmission) {
        this.transmission = transmission;
    }

    public String getAi() {
        return ai;
    }

    public void setAi(String ai) {
        this.ai = ai;
    }

    public Speed getMaxSpeed() {
        return maxSpeed;
    }

    public void setMaxSpeed(Speed maxSpeed) {
        this.maxSpeed = maxSpeed;
    }

    @Override
    public String toString() {
        return "Vehicle{" +
                "id='" + id + '\'' +
                ", code='" + code + '\'' +
                ", transmission='" + transmission + '\'' +
                ", ai='" + ai + '\'' +
                ", maxSpeed=" + maxSpeed +
                '}';
    }
}
